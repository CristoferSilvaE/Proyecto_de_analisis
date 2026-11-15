package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.DetalleEvaluacion;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DetalleEvaluacionDAO extends BaseDAO<DetalleEvaluacion> {

  public DetalleEvaluacionDAO() {
    super(DetalleEvaluacion.class);
  }

  public List<DetalleEvaluacion> findByEvaluacionIdEvaluacion(Integer idEvaluacion) {
    return entityManager
        .createQuery(
            "SELECT d FROM DetalleEvaluacion d WHERE d.evaluacion.idEvaluacion = :id",
            DetalleEvaluacion.class)
        .setParameter("id", idEvaluacion)
        .getResultList();
  }
}
