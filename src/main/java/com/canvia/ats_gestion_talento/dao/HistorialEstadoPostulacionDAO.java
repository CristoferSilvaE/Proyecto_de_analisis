package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.HistorialEstadoPostulacion;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HistorialEstadoPostulacionDAO extends BaseDAO<HistorialEstadoPostulacion> {

  public HistorialEstadoPostulacionDAO() {
    super(HistorialEstadoPostulacion.class);
  }

  public List<HistorialEstadoPostulacion> findByPostulacionIdPostulacionOrderByFechaCambioDesc(
      Integer idPostulacion) {
    return entityManager
        .createQuery(
            "SELECT h FROM HistorialEstadoPostulacion h "
                + "WHERE h.postulacion.idPostulacion = :id ORDER BY h.fechaCambio DESC",
            HistorialEstadoPostulacion.class)
        .setParameter("id", idPostulacion)
        .getResultList();
  }
}
