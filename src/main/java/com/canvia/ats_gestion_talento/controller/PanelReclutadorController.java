package com.canvia.ats_gestion_talento.controller;

import com.canvia.ats_gestion_talento.entity.Vacante;
import com.canvia.ats_gestion_talento.service.PostulacionService;
import com.canvia.ats_gestion_talento.service.PreseleccionService;
import com.canvia.ats_gestion_talento.service.UsuarioService;
import com.canvia.ats_gestion_talento.service.VacanteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/reclutador")
@RequiredArgsConstructor
public class PanelReclutadorController {
    private final VacanteService vacanteService;
    private final PostulacionService postulacionService;
    private final PreseleccionService preseleccionService;
    private final UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String verDashboard(Model model) {
        List<Vacante> vacantesActivas = vacanteService.listarPublicadas();
        long totalPostulaciones = postulacionService.contarTodas();
        model.addAttribute("vacantes", vacantesActivas);
        model.addAttribute("totalVacantes", vacantesActivas.size());
        model.addAttribute("totalPostulaciones", totalPostulaciones);
        model.addAttribute("procesadosATS", (int) (totalPostulaciones * 0.9));
        return "reclutador/dashboard";
    }

    @GetMapping("/escaner")
    public String verEscanerAtsGoblal(Model model) {
        model.addAttribute("postulaciones", postulacionService.listarPendientesAts());
        return "reclutador/escaner";
    }

    @PostMapping("/evaluar/{idPostulacion}")
    public String ejecutarATS(@PathVariable Integer idPostulacion) {
        try {
            Integer idReclutador = 2; // Reclutador de prueba, pendiente de autenticación.
            preseleccionService.evaluarCandidatoAutomatizado(idPostulacion, idReclutador);
            return "redirect:/reclutador/escaner?exito=evaluacionCompletada";
        } catch (Exception e) {
            log.error("Fallo ATS: {}", e.getMessage());
            return "redirect:/reclutador/escaner?error=falloATS";
        }
    }

    @GetMapping("/vacantes")
    public String gestionarVacantes(Model model) {
        model.addAttribute("vacantes", vacanteService.listarTodas());
        return "reclutador/vacantes";
    }

    @GetMapping("/candidatos")
    public String verBaseCandidatos(Model model) {
        model.addAttribute("candidatos", usuarioService.listarPorRol("CANDIDATO"));
        return "reclutador/candidatos";
    }
}
