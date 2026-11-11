package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.CurriculumVitae;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CurriculumVitaeRepository extends JpaRepository<CurriculumVitae, Integer> {
    List<CurriculumVitae> findByCandidatoIdUsuario(Integer idCandidato);
}