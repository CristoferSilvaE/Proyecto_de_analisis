package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.EvaluacionPreseleccion;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluacionPreseleccionDAO extends BaseDAO<EvaluacionPreseleccion> {

  public EvaluacionPreseleccionDAO() {
    super(EvaluacionPreseleccion.class);
  }

  public Optional<EvaluacionPreseleccion> findByPostulacionIdPostulacion(Integer idPostulacion) {
    return entityManager
        .createQuery(
            "SELECT e FROM EvaluacionPreseleccion e WHERE e.postulacion.idPostulacion = :id",
            EvaluacionPreseleccion.class)
        .setParameter("id", idPostulacion)
        .getResultStream()
        .findFirst();
  }
}
