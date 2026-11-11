package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.EvaluacionPreseleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EvaluacionPreseleccionRepository extends JpaRepository<EvaluacionPreseleccion, Integer> {
    Optional<EvaluacionPreseleccion> findByPostulacionIdPostulacion(Integer idPostulacion);
}