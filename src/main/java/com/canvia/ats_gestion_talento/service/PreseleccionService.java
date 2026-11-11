package com.canvia.ats_gestion_talento.service;

import com.canvia.ats_gestion_talento.entity.*;
import com.canvia.ats_gestion_talento.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreseleccionService {

    private final PostulacionRepository postulacionRepository;
    private final CriterioVacanteRepository criterioRepository;
    private final EvaluacionPreseleccionRepository evaluacionRepository;
    private final DetalleEvaluacionRepository detalleRepository;
    private final EstadoPostulacionRepository estadoRepository;
    private final HistorialEstadoPostulacionRepository historialRepository;
    
    private final PdfService pdfService;

    @Transactional // Garantiza que si algo falla, no se guarde información a medias
    public EvaluacionPreseleccion evaluarCandidatoAutomatizado(Integer idPostulacion, Integer idReclutador) {
        
        Optional<Postulacion> postulacionOpt = postulacionRepository.findById(idPostulacion);
        if (postulacionOpt.isEmpty()) {
            throw new IllegalArgumentException("Postulación no encontrada.");
        }
        
        Postulacion postulacion = postulacionOpt.get();
        Vacante vacante = postulacion.getVacante();
        
        // 1. Extraer el texto del CV del candidato
        // Nota: Asumimos que 'archivoUrl' tiene la ruta local. Si usas la nube, esto se ajusta.
        String textoCv = pdfService.extraerTextoDePdf(postulacion.getCurriculum().getArchivoUrl());
        
        // 2. Obtener los criterios que exige la vacante
        List<CriterioVacante> criterios = criterioRepository.findByVacanteIdVacante(vacante.getIdVacante());
        
        // 3. Crear la cabecera de la evaluación
        EvaluacionPreseleccion evaluacion = new EvaluacionPreseleccion();
        evaluacion.setPostulacion(postulacion);
        
        // Simulamos obtener el reclutador (idealmente viene del usuario autenticado)
        Usuario reclutador = new Usuario(); 
        reclutador.setIdUsuario(idReclutador);
        evaluacion.setReclutador(reclutador);
        
        evaluacion = evaluacionRepository.save(evaluacion);
        
        // 4. Evaluar criterio por criterio leyendo el PDF
        BigDecimal puntajeTotal = BigDecimal.ZERO;
        boolean cumpleTodosObligatorios = true;

        for (CriterioVacante criterio : criterios) {
            boolean cumple = textoCv.contains(criterio.getNombre().toLowerCase());
            
            if (criterio.getObligatorio() && !cumple) {
                cumpleTodosObligatorios = false;
            }

            BigDecimal puntajeObtenido = cumple ? criterio.getPeso() : BigDecimal.ZERO;
            puntajeTotal = puntajeTotal.add(puntajeObtenido);

            DetalleEvaluacion detalle = new DetalleEvaluacion();
            detalle.setEvaluacion(evaluacion);
            detalle.setCriterio(criterio);
            detalle.setCumple(cumple);
            detalle.setPuntaje(puntajeObtenido);
            detalle.setObservacion(cumple ? "Palabra clave encontrada en CV" : "Palabra clave NO encontrada");
            
            detalleRepository.save(detalle);
        }

        // 5. Determinar Veredicto
        evaluacion.setPuntajeTotal(puntajeTotal);
        String veredicto = (cumpleTodosObligatorios && puntajeTotal.compareTo(new BigDecimal("50.0")) >= 0) 
                           ? "PRESELECCIONADO" : "DESCARTADO";
        evaluacion.setResultado(veredicto);
        
        // 6. Actualizar el estado de la postulación
        EstadoPostulacion nuevoEstado = estadoRepository.findByNombre(veredicto)
                .orElseThrow(() -> new RuntimeException("Estado no configurado en BD"));
        
        postulacion.setEstado(nuevoEstado);
        postulacionRepository.save(postulacion);

        // 7. Guardar trazabilidad (Historial)
        HistorialEstadoPostulacion historial = new HistorialEstadoPostulacion();
        historial.setPostulacion(postulacion);
        historial.setEstado(nuevoEstado);
        historial.setUsuarioCambio(reclutador);
        historial.setObservacion("Evaluación automática completada. Puntaje: " + puntajeTotal);
        historialRepository.save(historial);

        log.info("Evaluación automática completada para postulación {}. Resultado: {}", idPostulacion, veredicto);
        
        return evaluacionRepository.save(evaluacion);
    }
}