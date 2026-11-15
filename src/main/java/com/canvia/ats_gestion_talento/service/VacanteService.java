package com.canvia.ats_gestion_talento.service;

import com.canvia.ats_gestion_talento.dao.VacanteDAO;
import com.canvia.ats_gestion_talento.entity.Vacante;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacanteService {
    private final VacanteDAO vacanteDAO;

    public List<Vacante> listarPublicadas() {
        return vacanteDAO.buscarPorEstado("PUBLICADA");
    }

    public List<Vacante> listarTodas() {
        return vacanteDAO.listarTodas();
    }

    public Optional<Vacante> buscarPorId(Integer id) {
        return vacanteDAO.buscarPorId(id);
    }

    @Transactional
    public Vacante guardar(Vacante vacante) {
        return vacanteDAO.guardar(vacante);
    }
}
