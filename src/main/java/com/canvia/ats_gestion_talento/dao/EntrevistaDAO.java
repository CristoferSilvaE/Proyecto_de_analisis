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

  // Listado para el panel de entrevistas, con candidato y vacante ya cargados para Thymeleaf.
  public List<Entrevista> findAllConDetalle() {
    return entityManager
        .createQuery(
            "SELECT e FROM Entrevista e JOIN FETCH e.postulacion p "
                + "JOIN FETCH p.candidato JOIN FETCH p.vacante JOIN FETCH p.estado "
                + "ORDER BY e.fechaHora ASC",
            Entrevista.class)
        .getResultList();
  }
}
