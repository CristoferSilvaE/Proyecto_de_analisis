package com.canvia.ats_gestion_talento.controller;

import com.canvia.ats_gestion_talento.entity.Usuario;
import com.canvia.ats_gestion_talento.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String password,
                                @RequestParam String rol,
                                RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorCorreo(correo);
        // Mantiene el comportamiento actual de login; seguridad se revisará aparte.
        if (usuarioOpt.isPresent() && usuarioOpt.get().getPasswordHash().equals(password)) {
            if (rol.equalsIgnoreCase("CANDIDATO")) {
                return "redirect:/candidato/vacantes";
            } else {
                return "redirect:/reclutador/dashboard";
            }
        }
        redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas.");
        return "redirect:/login";
    }
}
