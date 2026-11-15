package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.Postulacion;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class PostulacionDAO extends BaseDAO<Postulacion> {

    public PostulacionDAO() {
        super(Postulacion.class);
    }

    public List<Postulacion> findByCandidatoIdUsuario(Integer idCandidato) {
        return entityManager.createQuery(
            "SELECT p FROM Postulacion p JOIN FETCH p.vacante JOIN FETCH p.estado " +
            "WHERE p.candidato.idUsuario = :id", Postulacion.class
        ).setParameter("id", idCandidato).getResultList();
    }

    public List<Postulacion> findByVacanteIdVacante(Integer idVacante) {
        return entityManager.createQuery(
            "SELECT p FROM Postulacion p WHERE p.vacante.idVacante = :id", Postulacion.class
        ).setParameter("id", idVacante).getResultList();
    }

    public Optional<Postulacion> findByCandidatoIdUsuarioAndVacanteIdVacante(
            Integer idCandidato, Integer idVacante) {
        return entityManager.createQuery(
            "SELECT p FROM Postulacion p WHERE p.candidato.idUsuario = :candidato " +
            "AND p.vacante.idVacante = :vacante", Postulacion.class
        ).setParameter("candidato", idCandidato)
         .setParameter("vacante", idVacante)
         .getResultStream().findFirst();
    }

    public List<Postulacion> findByEstadoNombreIn(List<String> estados) {
        return entityManager.createQuery(
            "SELECT p FROM Postulacion p JOIN FETCH p.estado e " +
            "JOIN FETCH p.candidato JOIN FETCH p.vacante " +
            "WHERE e.nombre IN :estados", Postulacion.class
        ).setParameter("estados", estados).getResultList();
    }
}
