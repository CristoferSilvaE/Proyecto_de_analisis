package com.canvia.ats_gestion_talento.service;

import com.canvia.ats_gestion_talento.dao.EstadoPostulacionDAO;
import com.canvia.ats_gestion_talento.dao.HistorialEstadoPostulacionDAO;
import com.canvia.ats_gestion_talento.dao.PostulacionDAO;
import com.canvia.ats_gestion_talento.entity.EstadoPostulacion;
import com.canvia.ats_gestion_talento.entity.HistorialEstadoPostulacion;
import com.canvia.ats_gestion_talento.entity.Postulacion;
import com.canvia.ats_gestion_talento.entity.Usuario;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Centraliza los cambios de estado de una postulación.
 * Regla de negocio "Trazabilidad Inmutable": todo cambio de estado
 * genera un registro en historial_estado_postulacion.
 */
@Service
@RequiredArgsConstructor
public class HistorialService {

    private final PostulacionDAO postulacionDAO;
    private final EstadoPostulacionDAO estadoDAO;
    private final HistorialEstadoPostulacionDAO historialDAO;

    @Transactional
    public EstadoPostulacion cambiarEstado(Postulacion postulacion, String nombreEstado,
                                           Usuario usuarioCambio, String observacion) {
        EstadoPostulacion nuevoEstado = estadoDAO.findByNombre(nombreEstado)
                .orElseThrow(() -> new IllegalStateException(
                        "Estado no configurado en BD: " + nombreEstado));

        postulacion.setEstado(nuevoEstado);
        postulacionDAO.save(postulacion);

        HistorialEstadoPostulacion historial = new HistorialEstadoPostulacion();
        historial.setPostulacion(postulacion);
        historial.setEstado(nuevoEstado);
        historial.setUsuarioCambio(usuarioCambio);
        historial.setObservacion(observacion);
        historialDAO.save(historial);

        return nuevoEstado;
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoPostulacion> listarPorPostulacion(Integer idPostulacion) {
        return historialDAO.findByPostulacionIdPostulacionOrderByFechaCambioDesc(idPostulacion);
    }
}
