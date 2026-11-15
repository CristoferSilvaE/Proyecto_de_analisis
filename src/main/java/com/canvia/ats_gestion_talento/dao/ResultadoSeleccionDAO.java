package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.ResultadoSeleccion;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ResultadoSeleccionDAO extends BaseDAO<ResultadoSeleccion> {

  public ResultadoSeleccionDAO() {
    super(ResultadoSeleccion.class);
  }

  public Optional<ResultadoSeleccion> findByPostulacionIdPostulacion(Integer idPostulacion) {
    return entityManager
        .createQuery(
            "SELECT r FROM ResultadoSeleccion r WHERE r.postulacion.idPostulacion = :id",
            ResultadoSeleccion.class)
        .setParameter("id", idPostulacion)
        .getResultStream()
        .findFirst();
  }
}
