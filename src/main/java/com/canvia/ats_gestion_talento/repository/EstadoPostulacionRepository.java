package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.EstadoPostulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoPostulacionRepository extends JpaRepository<EstadoPostulacion, Integer> {
    Optional<EstadoPostulacion> findByNombre(String nombre);
}