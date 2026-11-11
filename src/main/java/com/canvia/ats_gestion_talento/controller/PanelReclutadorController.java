package com.canvia.ats_gestion_talento.controller;

import com.canvia.ats_gestion_talento.entity.EvaluacionPreseleccion;
import com.canvia.ats_gestion_talento.entity.Postulacion;
import com.canvia.ats_gestion_talento.entity.Vacante;
import com.canvia.ats_gestion_talento.repository.PostulacionRepository;
import com.canvia.ats_gestion_talento.repository.VacanteRepository;
import com.canvia.ats_gestion_talento.service.PreseleccionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/reclutador")
@RequiredArgsConstructor
public class PanelReclutadorController {

    private final VacanteRepository vacanteRepository;
    private final PostulacionRepository postulacionRepository;
    private final PreseleccionService preseleccionService;
    private final com.canvia.ats_gestion_talento.repository.UsuarioRepository usuarioRepository;

    // 1. RUTA: DASHBOARD PRINCIPAL (Basado en el boceto)
    @GetMapping("/dashboard")
    public String verDashboard(Model model) {
        List<Vacante> vacantesActivas = vacanteRepository.findByEstado("PUBLICADA");
        long totalPostulaciones = postulacionRepository.count();
        
        model.addAttribute("vacantes", vacantesActivas);
        model.addAttribute("totalVacantes", vacantesActivas.size());
        model.addAttribute("totalPostulaciones", totalPostulaciones);
        // Stats simuladas para el dashboard
        model.addAttribute("procesadosATS", (int)(totalPostulaciones * 0.9)); 
        
        return "reclutador/dashboard"; 
    }

    // 2. RUTA: ESCÁNER ATS & SCORING (Diseño a criterio propio)
    @GetMapping("/escaner")
    public String verEscanerAtsGoblal(Model model) {
        // Obtenemos todas las postulaciones que están en estado RECIBIDA (Pendientes de leer CV)
        List<Postulacion> pendientesATS = postulacionRepository.findAll().stream()
                .filter(p -> p.getEstado().getNombre().equals("RECIBIDA") || p.getEstado().getNombre().equals("PRESELECCIONADO"))
                .toList();
        
        model.addAttribute("postulaciones", pendientesATS);
        return "reclutador/escaner";
    }

    // 3. ACCIÓN: EJECUTAR MOTOR ATS (PDFBox)
    @PostMapping("/evaluar/{idPostulacion}")
    public String ejecutarATS(@PathVariable Integer idPostulacion) {
        try {
            Integer idReclutador = 2; // Simulado en sesión
            preseleccionService.evaluarCandidatoAutomatizado(idPostulacion, idReclutador);
            return "redirect:/reclutador/escaner?exito=evaluacionCompletada";
        } catch (Exception e) {
            log.error("Fallo ATS: {}", e.getMessage());
            return "redirect:/reclutador/escaner?error=falloATS";
        }
    }

    // 4. RUTA: GESTIÓN DE VACANTES ACTIVAS
    @GetMapping("/vacantes")
    public String gestionarVacantes(Model model) {
        // Obtenemos todas las vacantes (puedes filtrarlas por ID del reclutador en un entorno real)
        List<Vacante> todasLasVacantes = vacanteRepository.findAll();
        
        model.addAttribute("vacantes", todasLasVacantes);
        return "reclutador/vacantes"; // Renderizará templates/reclutador/vacantes.html
    }

    // 5. RUTA: BASE DE CANDIDATOS (Talent Pool)
    @GetMapping("/candidatos")
    public String verBaseCandidatos(Model model) {
        // Obtenemos solo a los usuarios con rol de CANDIDATO
        List<com.canvia.ats_gestion_talento.entity.Usuario> candidatos = usuarioRepository.findByRol("CANDIDATO");
        
        model.addAttribute("candidatos", candidatos);
        return "reclutador/candidatos"; // Renderizará templates/reclutador/candidatos.html
    }
}