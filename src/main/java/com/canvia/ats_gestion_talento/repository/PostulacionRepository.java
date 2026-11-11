package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Integer> {
    List<Postulacion> findByCandidatoIdUsuario(Integer idCandidato);
    List<Postulacion> findByVacanteIdVacante(Integer idVacante);
    Optional<Postulacion> findByCandidatoIdUsuarioAndVacanteIdVacante(Integer idCandidato, Integer idVacante);
}