package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.CurriculumVitae;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CurriculumVitaeDAO extends BaseDAO<CurriculumVitae> {

  public CurriculumVitaeDAO() {
    super(CurriculumVitae.class);
  }

  public List<CurriculumVitae> findByCandidatoIdUsuario(Integer idCandidato) {
    return entityManager
        .createQuery(
            "SELECT c FROM CurriculumVitae c WHERE c.candidato.idUsuario = :id",
            CurriculumVitae.class)
        .setParameter("id", idCandidato)
        .getResultList();
  }
}
