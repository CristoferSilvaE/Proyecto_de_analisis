package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.HistorialEstadoPostulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialEstadoPostulacionRepository extends JpaRepository<HistorialEstadoPostulacion, Integer> {
    List<HistorialEstadoPostulacion> findByPostulacionIdPostulacionOrderByFechaCambioDesc(Integer idPostulacion);
}