package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.Usuario;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDAO extends BaseDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Optional<Usuario> findByCorreo(String correo) {
        return entityManager.createQuery(
            "SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class
        ).setParameter("correo", correo).getResultStream().findFirst();
    }

    public List<Usuario> findByRol(String rol) {
        return entityManager.createQuery(
            "SELECT u FROM Usuario u WHERE u.rol = :rol", Usuario.class
        ).setParameter("rol", rol).getResultList();
    }
}
