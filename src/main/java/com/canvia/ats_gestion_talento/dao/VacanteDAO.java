package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.Vacante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class VacanteDAO {

  @PersistenceContext private EntityManager entityManager;

  // Consultar todas las vacantes
  public List<Vacante> listarTodas() {
    return entityManager.createQuery("SELECT v FROM Vacante v", Vacante.class).getResultList();
  }

  // Consultar vacantes según su estado
  public List<Vacante> buscarPorEstado(String estado) {
    return entityManager
        .createQuery("SELECT v FROM Vacante v WHERE v.estado = :estado", Vacante.class)
        .setParameter("estado", estado)
        .getResultList();
  }

  // Buscar una vacante por su identificador
  public Optional<Vacante> buscarPorId(Integer id) {
    return Optional.ofNullable(entityManager.find(Vacante.class, id));
  }

  // Registrar o actualizar una vacante
  public Vacante guardar(Vacante vacante) {
    if (vacante.getIdVacante() == null) {
      entityManager.persist(vacante);
      return vacante;
    }

    return entityManager.merge(vacante);
  }
}
