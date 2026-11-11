package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.Vacante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VacanteRepository extends JpaRepository<Vacante, Integer> {
    List<Vacante> findByEstado(String estado);
}