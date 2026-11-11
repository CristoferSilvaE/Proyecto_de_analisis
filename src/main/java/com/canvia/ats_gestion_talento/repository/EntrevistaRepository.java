package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.Entrevista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntrevistaRepository extends JpaRepository<Entrevista, Integer> {
    List<Entrevista> findByPostulacionIdPostulacion(Integer idPostulacion);
}