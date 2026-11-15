package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.EstadoPostulacion;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class EstadoPostulacionDAO extends BaseDAO<EstadoPostulacion> {

  public EstadoPostulacionDAO() {
    super(EstadoPostulacion.class);
  }

  public Optional<EstadoPostulacion> findByNombre(String nombre) {
    return entityManager
        .createQuery(
            "SELECT e FROM EstadoPostulacion e WHERE e.nombre = :nombre", EstadoPostulacion.class)
        .setParameter("nombre", nombre)
        .getResultStream()
        .findFirst();
  }
}
