package com.canvia.ats_gestion_talento.controller;

import com.canvia.ats_gestion_talento.dto.EntrevistaDto;
import com.canvia.ats_gestion_talento.dto.ResultadoSeleccionDto;
import com.canvia.ats_gestion_talento.service.EntrevistaService;
import com.canvia.ats_gestion_talento.service.ResultadoSeleccionService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CU07 - Programar entrevista, CU08 - Registrar resultado y CU09 - Notificar resultado.
 * Controlador separado de PanelReclutadorController para no interferir con ese módulo.
 */
@Slf4j
@Controller
@RequestMapping("/reclutador/entrevistas")
@RequiredArgsConstructor
public class EntrevistaController {

    // Reclutador de prueba, igual que en PanelReclutadorController; se reemplazará por la sesión.
    private static final Integer ID_RECLUTADOR = 2;

    /** Traducción de los códigos de error de los servicios a mensajes para la vista. */
    private static final Map<String, String> MENSAJES_ERROR = Map.ofEntries(
            Map.entry("postulacionNoEncontrada", "La postulación seleccionada no existe."),
            Map.entry("estadoNoPermiteEntrevista", "Solo se pueden programar entrevistas a candidatos preseleccionados o en entrevista."),
            Map.entry("estadoNoPermiteResultado", "Esta postulación ya tiene un resultado final o no está en una etapa válida."),
            Map.entry("fechaInvalida", "La fecha y hora deben ser posteriores al momento actual."),
            Map.entry("modalidadInvalida", "La modalidad debe ser VIRTUAL o PRESENCIAL."),
            Map.entry("tipoInvalido", "El tipo de entrevista no es válido."),
            Map.entry("enlaceObligatorio", "Para una entrevista virtual debe registrar el enlace de la reunión."),
            Map.entry("entrevistaNoEncontrada", "La entrevista seleccionada no existe."),
            Map.entry("entrevistaFinalizada", "La entrevista ya fue realizada o cancelada."),
            Map.entry("reclutadorNoEncontrado", "No se encontró al reclutador responsable."),
            Map.entry("resultadoInvalido", "El resultado debe ser SELECCIONADO, NO_SELECCIONADO o PENDIENTE."));

    private final EntrevistaService entrevistaService;
    private final ResultadoSeleccionService resultadoService;

    @GetMapping
    public String verEntrevistas(Model model) {
        model.addAttribute("postulaciones", entrevistaService.listarPostulacionesAptas());
        model.addAttribute("entrevistas", entrevistaService.listarTodas());
        model.addAttribute("entrevistaDto", new EntrevistaDto());
        model.addAttribute("resultadoDto", new ResultadoSeleccionDto());
        return "reclutador/entrevistas";
    }

    @PostMapping("/programar")
    public String programar(@Valid @ModelAttribute("entrevistaDto") EntrevistaDto dto,
                            BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return conError(redirect, result.getAllErrors().get(0).getDefaultMessage());
        }
        return ejecutar(redirect, "Entrevista programada. Se registró la notificación al candidato.",
                () -> entrevistaService.programar(dto, ID_RECLUTADOR));
    }

    @PostMapping("/{id}/reprogramar")
    public String reprogramar(@PathVariable Integer id,
                              @RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
                              @RequestParam(required = false) String enlaceReunion,
                              RedirectAttributes redirect) {
        return ejecutar(redirect, "Entrevista reprogramada. Se registró la notificación al candidato.",
                () -> entrevistaService.reprogramar(id, fechaHora, enlaceReunion));
    }

    @PostMapping("/{id}/realizada")
    public String marcarRealizada(@PathVariable Integer id,
                                  @RequestParam(required = false) String observaciones,
                                  RedirectAttributes redirect) {
        return ejecutar(redirect, "Entrevista marcada como realizada.",
                () -> entrevistaService.marcarRealizada(id, observaciones));
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Integer id,
                           @RequestParam(required = false) String motivo,
                           RedirectAttributes redirect) {
        return ejecutar(redirect, "Entrevista cancelada.",
                () -> entrevistaService.cancelar(id, motivo));
    }

    @PostMapping("/resultado")
    public String registrarResultado(@Valid @ModelAttribute("resultadoDto") ResultadoSeleccionDto dto,
                                     BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return conError(redirect, result.getAllErrors().get(0).getDefaultMessage());
        }
        String exito = "PENDIENTE".equalsIgnoreCase(dto.getResultado())
                ? "Observaciones guardadas. La decisión final queda pendiente."
                : "Resultado registrado. Se registró la notificación al candidato.";
        return ejecutar(redirect, exito, () -> resultadoService.registrar(dto, ID_RECLUTADOR));
    }

    // ---------------------------------------------------------------- auxiliares

    private String ejecutar(RedirectAttributes redirect, String mensajeExito, Runnable accion) {
        try {
            accion.run();
            redirect.addFlashAttribute("exito", mensajeExito);
        } catch (IllegalArgumentException e) {
            log.warn("Operación de entrevista rechazada: {}", e.getMessage());
            redirect.addFlashAttribute("error", MENSAJES_ERROR.getOrDefault(e.getMessage(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error en el módulo de entrevistas", e);
            redirect.addFlashAttribute("error", "Ocurrió un error inesperado. Intente nuevamente.");
        }
        return "redirect:/reclutador/entrevistas";
    }

    private String conError(RedirectAttributes redirect, String mensaje) {
        redirect.addFlashAttribute("error", mensaje);
        return "redirect:/reclutador/entrevistas";
    }
}
