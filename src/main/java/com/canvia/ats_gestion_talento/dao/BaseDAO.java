package com.canvia.ats_gestion_talento.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public abstract class BaseDAO<T> {

  @PersistenceContext protected EntityManager entityManager;

  private final Class<T> tipoEntidad;

  protected BaseDAO(Class<T> tipoEntidad) {
    this.tipoEntidad = tipoEntidad;
  }

  public Optional<T> findById(Integer id) {
    return Optional.ofNullable(entityManager.find(tipoEntidad, id));
  }

  public List<T> findAll() {
    return entityManager
        .createQuery("SELECT e FROM " + tipoEntidad.getSimpleName() + " e", tipoEntidad)
        .getResultList();
  }

  public long count() {
    return entityManager
        .createQuery("SELECT COUNT(e) FROM " + tipoEntidad.getSimpleName() + " e", Long.class)
        .getSingleResult();
  }

  // Debe ejecutarse dentro de una transacción del servicio (@Transactional).
  public T save(T entidad) {
    Object id =
        entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entidad);
    if (id == null) {
      entityManager.persist(entidad);
      return entidad;
    }
    return entityManager.merge(entidad);
  }

  public void deleteById(Integer id) {
    findById(id).ifPresent(entityManager::remove);
  }
}
