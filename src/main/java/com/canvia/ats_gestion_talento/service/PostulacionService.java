package com.canvia.ats_gestion_talento.service;

import com.canvia.ats_gestion_talento.dao.*;
import com.canvia.ats_gestion_talento.dto.PostulacionRequestDto;
import com.canvia.ats_gestion_talento.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostulacionService {
    private final PostulacionDAO postulacionDAO;
    private final UsuarioDAO usuarioDAO;
    private final VacanteDAO vacanteDAO;
    private final EstadoPostulacionDAO estadoDAO;
    private final CurriculumVitaeDAO curriculumDAO;

    @Transactional(readOnly = true)
    public List<Postulacion> listarPorCandidato(Integer idCandidato) {
        return postulacionDAO.findByCandidatoIdUsuario(idCandidato);
    }

    @Transactional(readOnly = true)
    public List<Postulacion> listarPendientesAts() {
        return postulacionDAO.findByEstadoNombreIn(
            List.of("RECIBIDA", "PRESELECCIONADO")
        );
    }

    @Transactional(readOnly = true)
    public long contarTodas() {
        return postulacionDAO.count();
    }

    @Transactional(rollbackFor = IOException.class)
    public void registrarPostulacion(PostulacionRequestDto dto) throws IOException {
        MultipartFile archivo = dto.getArchivoCv();
        if (archivo == null || archivo.isEmpty()
                || !"application/pdf".equalsIgnoreCase(archivo.getContentType())) {
            throw new IllegalArgumentException("formatoInvalido");
        }

        Usuario candidato = usuarioDAO.findById(dto.getIdCandidato())
                .orElseThrow(() -> new IllegalArgumentException("Candidato no encontrado"));
        Vacante vacante = vacanteDAO.buscarPorId(dto.getIdVacante())
                .orElseThrow(() -> new IllegalArgumentException("Vacante no encontrada"));
        EstadoPostulacion estadoInicial = estadoDAO.findByNombre("RECIBIDA")
                .orElseThrow(() -> new IllegalStateException("Estado base no configurado"));

        // Ruta generada en el servidor, nunca se utiliza directamente la ruta del cliente.
        String nombreOriginal = archivo.getOriginalFilename() == null
                ? "curriculum.pdf" : Paths.get(archivo.getOriginalFilename()).getFileName().toString();
        String nombreArchivo = UUID.randomUUID() + "_" + nombreOriginal;
        Path carpeta = Paths.get("uploads", "cvs").toAbsolutePath().normalize();
        Files.createDirectories(carpeta);
        Path rutaArchivo = carpeta.resolve(nombreArchivo).normalize();
        if (!rutaArchivo.startsWith(carpeta)) {
            throw new IllegalArgumentException("Nombre de archivo no permitido");
        }
        try (var entrada = archivo.getInputStream()) {
            Files.copy(entrada, rutaArchivo);
        }

        CurriculumVitae cv = new CurriculumVitae();
        cv.setCandidato(candidato);
        cv.setNombreArchivo(nombreOriginal);
        cv.setArchivoUrl(rutaArchivo.toString());
        cv = curriculumDAO.save(cv);

        Postulacion postulacion = new Postulacion();
        postulacion.setCandidato(candidato);
        postulacion.setVacante(vacante);
        postulacion.setCurriculum(cv);
        postulacion.setEstado(estadoInicial);
        postulacionDAO.save(postulacion);

        log.info("Postulación registrada: candidato {}, vacante {}",
                candidato.getIdUsuario(), vacante.getIdVacante());
    }
}
