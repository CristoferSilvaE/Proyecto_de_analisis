package com.canvia.ats_gestion_talento.controller;

import com.canvia.ats_gestion_talento.dto.PostulacionRequestDto;
import com.canvia.ats_gestion_talento.service.PostulacionService;
import com.canvia.ats_gestion_talento.service.VacanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/candidato")
@RequiredArgsConstructor
public class PortalCandidatoController {
    private final VacanteService vacanteService;
    private final PostulacionService postulacionService;

    @GetMapping("/mis-postulaciones")
    public String verMisPostulaciones(Model model) {
        // Se conserva el candidato de prueba; se sustituirá por sesión autenticada.
        Integer idCandidato = 1;
        model.addAttribute("postulaciones", postulacionService.listarPorCandidato(idCandidato));
        return "candidato/mis-postulaciones";
    }

    @GetMapping("/vacantes")
    public String listarVacantesPublicas(Model model) {
        model.addAttribute("vacantes", vacanteService.listarPublicadas());
        model.addAttribute("postulacionDto", new PostulacionRequestDto());
        return "candidato/vacantes";
    }

    @PostMapping("/postular")
    public String procesarPostulacion(@Valid @ModelAttribute("postulacionDto") PostulacionRequestDto dto,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "redirect:/candidato/vacantes?error=datosInvalidos";
        }
        try {
            postulacionService.registrarPostulacion(dto);
            return "redirect:/candidato/vacantes?exito=postulacionRegistrada";
        } catch (IOException e) {
            log.error("Error al guardar el CV: {}", e.getMessage());
            return "redirect:/candidato/vacantes?error=errorArchivo";
        } catch (IllegalArgumentException e) {
            log.warn("Postulación no válida: {}", e.getMessage());
            return "redirect:/candidato/vacantes?error=" +
                    ("formatoInvalido".equals(e.getMessage()) ? "formatoInvalido" : "datosInvalidos");
        }
    }
}
