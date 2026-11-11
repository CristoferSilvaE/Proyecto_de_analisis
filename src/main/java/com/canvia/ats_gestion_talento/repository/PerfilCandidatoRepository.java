package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.PerfilCandidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilCandidatoRepository extends JpaRepository<PerfilCandidato, Integer> {
}