package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.CriterioVacante;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CriterioVacanteDAO extends BaseDAO<CriterioVacante> {

  public CriterioVacanteDAO() {
    super(CriterioVacante.class);
  }

  public List<CriterioVacante> findByVacanteIdVacante(Integer idVacante) {
    return entityManager
        .createQuery(
            "SELECT c FROM CriterioVacante c WHERE c.vacante.idVacante = :id",
            CriterioVacante.class)
        .setParameter("id", idVacante)
        .getResultList();
  }
}
