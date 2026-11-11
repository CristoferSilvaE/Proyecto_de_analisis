package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.CriterioVacante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CriterioVacanteRepository extends JpaRepository<CriterioVacante, Integer> {
    List<CriterioVacante> findByVacanteIdVacante(Integer idVacante);
}