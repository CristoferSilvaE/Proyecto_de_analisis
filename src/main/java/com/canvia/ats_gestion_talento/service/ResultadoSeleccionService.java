package com.canvia.ats_gestion_talento.service;

import com.canvia.ats_gestion_talento.dao.PostulacionDAO;
import com.canvia.ats_gestion_talento.dao.ResultadoSeleccionDAO;
import com.canvia.ats_gestion_talento.dao.UsuarioDAO;
import com.canvia.ats_gestion_talento.dto.ResultadoSeleccionDto;
import com.canvia.ats_gestion_talento.entity.Postulacion;
import com.canvia.ats_gestion_talento.entity.ResultadoSeleccion;
import com.canvia.ats_gestion_talento.entity.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU08 - Registrar resultado de selección y CU09 - Notificar resultado.
 * PENDIENTE corresponde al flujo alternativo: se guardan observaciones
 * y la decisión final queda abierta (no cambia el estado ni se notifica).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResultadoSeleccionService {

    private static final Set<String> RESULTADOS = Set.of("SELECCIONADO", "NO_SELECCIONADO", "PENDIENTE");
    /** Estados de postulación en los que se puede registrar el resultado. */
    private static final Set<String> ESTADOS_PERMITIDOS = Set.of("PRESELECCIONADO", "EN_ENTREVISTA");

    private final ResultadoSeleccionDAO resultadoDAO;
    private final PostulacionDAO postulacionDAO;
    private final UsuarioDAO usuarioDAO;
    private final HistorialService historialService;
    private final NotificacionService notificacionService;

    @Transactional(readOnly = true)
    public Optional<ResultadoSeleccion> buscarPorPostulacion(Integer idPostulacion) {
        return resultadoDAO.findByPostulacionIdPostulacion(idPostulacion);
    }

    /** Postulaciones a las que se les puede registrar un resultado. */
    @Transactional(readOnly = true)
    public List<Postulacion> listarPostulacionesAptas() {
        return postulacionDAO.findByEstadoNombreIn(List.copyOf(ESTADOS_PERMITIDOS));
    }

    @Transactional
    public ResultadoSeleccion registrar(ResultadoSeleccionDto dto, Integer idReclutador) {
        String resultado = dto.getResultado() == null ? "" : dto.getResultado().trim().toUpperCase();
        if (!RESULTADOS.contains(resultado)) {
            throw new IllegalArgumentException("resultadoInvalido");
        }

        Postulacion postulacion = postulacionDAO.findById(dto.getIdPostulacion())
                .orElseThrow(() -> new IllegalArgumentException("postulacionNoEncontrada"));
        if (!ESTADOS_PERMITIDOS.contains(postulacion.getEstado().getNombre())) {
            throw new IllegalArgumentException("estadoNoPermiteResultado");
        }

        Usuario reclutador = usuarioDAO.findById(idReclutador)
                .filter(u -> "RECLUTADOR".equals(u.getRol()))
                .orElseThrow(() -> new IllegalArgumentException("reclutadorNoEncontrado"));

        // resultado_seleccion es único por postulación: si ya hay uno (PENDIENTE), se actualiza.
        ResultadoSeleccion registro = resultadoDAO.findByPostulacionIdPostulacion(postulacion.getIdPostulacion())
                .orElseGet(ResultadoSeleccion::new);
        registro.setPostulacion(postulacion);
        registro.setReclutador(reclutador);
        registro.setResultado(resultado);
        registro.setObservaciones(dto.getObservaciones());
        registro = resultadoDAO.save(registro);

        if (!"PENDIENTE".equals(resultado)) {
            historialService.cambiarEstado(postulacion, resultado, reclutador,
                    "Resultado final registrado: " + resultado);
            notificarResultado(postulacion, resultado);
        }

        log.info("Resultado {} registrado para la postulación {}", resultado, postulacion.getIdPostulacion());
        return registro;
    }

    private void notificarResultado(Postulacion postulacion, String resultado) {
        Usuario candidato = postulacion.getCandidato();
        String vacante = postulacion.getVacante().getTitulo();
        boolean seleccionado = "SELECCIONADO".equals(resultado);

        String asunto = (seleccionado ? "¡Felicitaciones! Fuiste seleccionado - " : "Resultado de tu postulación - ")
                + vacante;
        if (asunto.length() > 150) { // notificacion.asunto es VARCHAR(150)
            asunto = asunto.substring(0, 150);
        }

        String cuerpo = seleccionado
                ? "Nos complace informarte que has sido seleccionado(a) para la vacante \"" + vacante
                        + "\". En los próximos días nos comunicaremos contigo para coordinar tu incorporación."
                : "Agradecemos tu interés y el tiempo dedicado al proceso de selección para la vacante \""
                        + vacante + "\". En esta oportunidad hemos decidido continuar con otros candidatos. "
                        + "Te animamos a postular a nuestras próximas convocatorias.";

        String mensaje = "Hola " + candidato.getNombres() + ",\n\n" + cuerpo
                + "\n\nEquipo de Adquisición de Talento - Canvia";

        notificacionService.registrarYEnviarNotificacion(candidato, postulacion, "RESULTADO", asunto, mensaje);
    }
}
