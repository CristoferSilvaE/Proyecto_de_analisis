package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.ResultadoSeleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ResultadoSeleccionRepository extends JpaRepository<ResultadoSeleccion, Integer> {
    Optional<ResultadoSeleccion> findByPostulacionIdPostulacion(Integer idPostulacion);
}