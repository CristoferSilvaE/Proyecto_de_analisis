package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.DetalleEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleEvaluacionRepository extends JpaRepository<DetalleEvaluacion, Integer> {
    List<DetalleEvaluacion> findByEvaluacionIdEvaluacion(Integer idEvaluacion);
}