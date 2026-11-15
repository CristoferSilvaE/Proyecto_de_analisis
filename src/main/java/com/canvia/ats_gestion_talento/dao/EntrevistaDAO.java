package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.Entrevista;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class EntrevistaDAO extends BaseDAO<Entrevista> {

  public EntrevistaDAO() {
    super(Entrevista.class);
  }

  public List<Entrevista> findByPostulacionIdPostulacion(Integer idPostulacion) {
    return entityManager
        .createQuery(
            "SELECT e FROM Entrevista e WHERE e.postulacion.idPostulacion = :id", Entrevista.class)
        .setParameter("id", idPostulacion)
        .getResultList();
  }
}
