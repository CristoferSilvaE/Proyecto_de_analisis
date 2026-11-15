package com.canvia.ats_gestion_talento.service;

import com.canvia.ats_gestion_talento.dao.UsuarioDAO;
import com.canvia.ats_gestion_talento.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioDAO.findByCorreo(correo);
    }

    public List<Usuario> listarPorRol(String rol) {
        return usuarioDAO.findByRol(rol);
    }
}
