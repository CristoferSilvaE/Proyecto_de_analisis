package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.PerfilCandidato;
import org.springframework.stereotype.Repository;

@Repository
public class PerfilCandidatoDAO extends BaseDAO<PerfilCandidato> {

    public PerfilCandidatoDAO() {
        super(PerfilCandidato.class);
    }
}
