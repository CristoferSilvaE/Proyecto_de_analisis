package com.canvia.ats_gestion_talento.service;

import com.canvia.ats_gestion_talento.dao.EntrevistaDAO;
import com.canvia.ats_gestion_talento.dao.PostulacionDAO;
import com.canvia.ats_gestion_talento.dao.UsuarioDAO;
import com.canvia.ats_gestion_talento.dto.EntrevistaDto;
import com.canvia.ats_gestion_talento.entity.Entrevista;
import com.canvia.ats_gestion_talento.entity.Postulacion;
import com.canvia.ats_gestion_talento.entity.Usuario;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU07 - Programar entrevista.
 * Los errores de validación se lanzan como IllegalArgumentException con un código corto
 * (igual que PostulacionService) para que el controlador lo pase como ?error=codigo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntrevistaService {

    private static final Set<String> MODALIDADES = Set.of("VIRTUAL", "PRESENCIAL");
    private static final Set<String> TIPOS = Set.of("GENERAL", "RRHH", "TECNICA", "FINAL");
    /** Estados de postulación desde los que se puede programar una entrevista. */
    private static final Set<String> ESTADOS_PERMITIDOS = Set.of("PRESELECCIONADO", "EN_ENTREVISTA");
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");

    private final EntrevistaDAO entrevistaDAO;
    private final PostulacionDAO postulacionDAO;
    private final UsuarioDAO usuarioDAO;
    private final HistorialService historialService;
    private final NotificacionService notificacionService;

    // ---------------------------------------------------------------- consultas

    @Transactional(readOnly = true)
    public List<Entrevista> listarTodas() {
        return entrevistaDAO.findAllConDetalle();
    }

    @Transactional(readOnly = true)
    public List<Entrevista> listarPorPostulacion(Integer idPostulacion) {
        return entrevistaDAO.findByPostulacionIdPostulacion(idPostulacion);
    }

    /** Postulaciones a las que se les puede programar una entrevista. */
    @Transactional(readOnly = true)
    public List<Postulacion> listarPostulacionesAptas() {
        return postulacionDAO.findByEstadoNombreIn(List.copyOf(ESTADOS_PERMITIDOS));
    }

    // ---------------------------------------------------------------- comandos

    @Transactional
    public Entrevista programar(EntrevistaDto dto, Integer idReclutador) {
        Postulacion postulacion = postulacionDAO.findById(dto.getIdPostulacion())
                .orElseThrow(() -> new IllegalArgumentException("postulacionNoEncontrada"));
        Usuario reclutador = buscarReclutador(idReclutador);

        String estadoActual = postulacion.getEstado().getNombre();
        if (!ESTADOS_PERMITIDOS.contains(estadoActual)) {
            throw new IllegalArgumentException("estadoNoPermiteEntrevista");
        }

        String modalidad = normalizar(dto.getModalidad());
        String tipo = dto.getTipo() == null || dto.getTipo().isBlank() ? "GENERAL" : normalizar(dto.getTipo());
        String enlace = dto.getEnlaceReunion() == null ? null : dto.getEnlaceReunion().trim();
        validarDatos(dto.getFechaHora(), modalidad, tipo, enlace);

        Entrevista entrevista = new Entrevista();
        entrevista.setPostulacion(postulacion);
        entrevista.setReclutador(reclutador);
        entrevista.setFechaHora(dto.getFechaHora());
        entrevista.setModalidad(modalidad);
        entrevista.setTipo(tipo);
        entrevista.setEnlaceReunion(enlace == null || enlace.isEmpty() ? null : enlace);
        entrevista.setObservaciones(dto.getObservaciones());
        entrevista.setEstado("PROGRAMADA");
        entrevista = entrevistaDAO.save(entrevista);

        if (!"EN_ENTREVISTA".equals(estadoActual)) {
            historialService.cambiarEstado(postulacion, "EN_ENTREVISTA", reclutador,
                    "Entrevista " + tipo + " programada para el " + dto.getFechaHora().format(FORMATO_FECHA));
        }

        notificarEntrevista(postulacion, entrevista, "Entrevista programada");
        log.info("Entrevista {} programada para la postulación {}",
                entrevista.getIdEntrevista(), postulacion.getIdPostulacion());
        return entrevista;
    }

    /** Flujo alternativo de CU07: cambiar fecha/hora o enlace de una entrevista ya programada. */
    @Transactional
    public Entrevista reprogramar(Integer idEntrevista, LocalDateTime nuevaFechaHora, String nuevoEnlace) {
        Entrevista entrevista = buscarEntrevistaActiva(idEntrevista);
        String enlace = nuevoEnlace == null || nuevoEnlace.isBlank()
                ? entrevista.getEnlaceReunion() : nuevoEnlace.trim();
        validarDatos(nuevaFechaHora, entrevista.getModalidad(), entrevista.getTipo(), enlace);

        entrevista.setFechaHora(nuevaFechaHora);
        entrevista.setEnlaceReunion(enlace);
        entrevista.setEstado("REPROGRAMADA");
        entrevista = entrevistaDAO.save(entrevista);

        notificarEntrevista(entrevista.getPostulacion(), entrevista, "Entrevista reprogramada");
        return entrevista;
    }

    /** Marca la entrevista como realizada y guarda las notas del reclutador. */
    @Transactional
    public Entrevista marcarRealizada(Integer idEntrevista, String observaciones) {
        Entrevista entrevista = buscarEntrevistaActiva(idEntrevista);
        entrevista.setEstado("REALIZADA");
        if (observaciones != null && !observaciones.isBlank()) {
            entrevista.setObservaciones(observaciones.trim());
        }
        return entrevistaDAO.save(entrevista);
    }

    @Transactional
    public Entrevista cancelar(Integer idEntrevista, String motivo) {
        Entrevista entrevista = buscarEntrevistaActiva(idEntrevista);
        entrevista.setEstado("CANCELADA");
        if (motivo != null && !motivo.isBlank()) {
            entrevista.setObservaciones(motivo.trim());
        }
        return entrevistaDAO.save(entrevista);
    }

    // ---------------------------------------------------------------- auxiliares

    private void validarDatos(LocalDateTime fechaHora, String modalidad, String tipo, String enlace) {
        if (fechaHora == null || !fechaHora.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("fechaInvalida");
        }
        if (!MODALIDADES.contains(modalidad)) {
            throw new IllegalArgumentException("modalidadInvalida");
        }
        if (!TIPOS.contains(tipo)) {
            throw new IllegalArgumentException("tipoInvalido");
        }
        if ("VIRTUAL".equals(modalidad) && (enlace == null || enlace.isBlank())) {
            throw new IllegalArgumentException("enlaceObligatorio");
        }
    }

    private Entrevista buscarEntrevistaActiva(Integer idEntrevista) {
        Entrevista entrevista = entrevistaDAO.findById(idEntrevista)
                .orElseThrow(() -> new IllegalArgumentException("entrevistaNoEncontrada"));
        if (Set.of("REALIZADA", "CANCELADA").contains(entrevista.getEstado())) {
            throw new IllegalArgumentException("entrevistaFinalizada");
        }
        return entrevista;
    }

    private Usuario buscarReclutador(Integer idReclutador) {
        Usuario reclutador = usuarioDAO.findById(idReclutador)
                .orElseThrow(() -> new IllegalArgumentException("reclutadorNoEncontrado"));
        if (!"RECLUTADOR".equals(reclutador.getRol())) {
            throw new IllegalArgumentException("reclutadorNoEncontrado");
        }
        return reclutador;
    }

    private void notificarEntrevista(Postulacion postulacion, Entrevista entrevista, String titulo) {
        Usuario candidato = postulacion.getCandidato();
        String vacante = postulacion.getVacante().getTitulo();
        StringBuilder mensaje = new StringBuilder()
                .append("Hola ").append(candidato.getNombres()).append(",\n\n")
                .append("Tu entrevista para la vacante \"").append(vacante).append("\" ")
                .append("VIRTUAL".equals(entrevista.getModalidad()) ? "será virtual" : "será presencial")
                .append(" el ").append(entrevista.getFechaHora().format(FORMATO_FECHA)).append(".\n");
        if (entrevista.getEnlaceReunion() != null) {
            mensaje.append("Enlace de la reunión: ").append(entrevista.getEnlaceReunion()).append("\n");
        }
        mensaje.append("\nTipo de entrevista: ").append(entrevista.getTipo())
                .append("\n\nEquipo de Adquisición de Talento - Canvia");

        String asunto = titulo + " - " + vacante;
        if (asunto.length() > 150) { // notificacion.asunto es VARCHAR(150)
            asunto = asunto.substring(0, 150);
        }
        notificacionService.registrarYEnviarNotificacion(candidato, postulacion, "ENTREVISTA",
                asunto, mensaje.toString());
    }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase();
    }
}
