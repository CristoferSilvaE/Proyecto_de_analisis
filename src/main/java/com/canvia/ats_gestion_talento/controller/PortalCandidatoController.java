package com.canvia.ats_gestion_talento.controller;

import com.canvia.ats_gestion_talento.dto.PostulacionRequestDto;
import com.canvia.ats_gestion_talento.entity.*;
import com.canvia.ats_gestion_talento.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/candidato")
@RequiredArgsConstructor
public class PortalCandidatoController {

    private final VacanteRepository vacanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurriculumVitaeRepository cvRepository;
    private final EstadoPostulacionRepository estadoRepository;
    private final PostulacionRepository postulacionRepository;

    // Directorio local para guardar los PDFs subidos
    private final String UPLOAD_DIR = "uploads/cvs/";

    @GetMapping("/mis-postulaciones")
    public String verMisPostulaciones(Model model) {
        // Simulamos la sesión del candidato con ID = 1
        Integer idCandidato = 1;
        
        // Obtenemos todas las postulaciones y filtramos por el candidato (En producción usaríamos un método del Repository)
        List<Postulacion> misPostulaciones = postulacionRepository.findAll().stream()
                .filter(p -> p.getCandidato().getIdUsuario().equals(idCandidato))
                .toList();
        
        model.addAttribute("postulaciones", misPostulaciones);
        return "candidato/mis-postulaciones"; // Renderizará templates/candidato/mis-postulaciones.html
    }

    @GetMapping("/vacantes")
    public String listarVacantesPublicas(Model model) {
        List<Vacante> vacantes = vacanteRepository.findByEstado("PUBLICADA");
        model.addAttribute("vacantes", vacantes);
        model.addAttribute("postulacionDto", new PostulacionRequestDto());
        return "candidato/vacantes"; // Renderizará templates/candidato/vacantes.html
    }

    @PostMapping("/postular")
    public String procesarPostulacion(@Valid @ModelAttribute("postulacionDto") PostulacionRequestDto dto,
                                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "redirect:/candidato/vacantes?error=datosInvalidos";
        }

        try {
            // 1. Guardar el archivo PDF físicamente
            MultipartFile archivo = dto.getArchivoCv();
            if (archivo.isEmpty() || !archivo.getContentType().equals("application/pdf")) {
                return "redirect:/candidato/vacantes?error=formatoInvalido";
            }

            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path rutaFisica = Paths.get(UPLOAD_DIR + nombreArchivo);
            Files.write(rutaFisica, archivo.getBytes());

            // 2. Obtener entidades relacionadas
            Usuario candidato = usuarioRepository.findById(dto.getIdCandidato())
                    .orElseThrow(() -> new RuntimeException("Candidato no encontrado"));
            Vacante vacante = vacanteRepository.findById(dto.getIdVacante())
                    .orElseThrow(() -> new RuntimeException("Vacante no encontrada"));
            EstadoPostulacion estadoInicial = estadoRepository.findByNombre("RECIBIDA")
                    .orElseThrow(() -> new RuntimeException("Estado base no configurado"));

            // 3. Registrar Curriculum en BD
            CurriculumVitae cv = new CurriculumVitae();
            cv.setCandidato(candidato);
            cv.setNombreArchivo(archivo.getOriginalFilename());
            cv.setArchivoUrl(rutaFisica.toString()); // Importante para que el ATS lo lea después
            cv = cvRepository.save(cv);

            // 4. Registrar Postulacion
            Postulacion postulacion = new Postulacion();
            postulacion.setCandidato(candidato);
            postulacion.setVacante(vacante);
            postulacion.setCurriculum(cv);
            postulacion.setEstado(estadoInicial);
            postulacionRepository.save(postulacion);

            log.info("Postulación exitosa del candidato {} a la vacante {}", candidato.getCorreo(), vacante.getTitulo());
            return "redirect:/candidato/vacantes?exito=postulacionRegistrada";

        } catch (IOException e) {
            log.error("Error al guardar el CV: {}", e.getMessage());
            return "redirect:/candidato/vacantes?error=errorArchivo";
        }
    }
}