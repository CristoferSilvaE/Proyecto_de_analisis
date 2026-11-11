package com.canvia.ats_gestion_talento.controller;

import com.canvia.ats_gestion_talento.entity.Usuario;
import com.canvia.ats_gestion_talento.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login"; // Renderizará templates/auth/login.html
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo, 
                                @RequestParam String password, 
                                @RequestParam String rol,
                                RedirectAttributes redirectAttributes) {
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isPresent() && usuarioOpt.get().getPasswordHash().equals(password)) {
            if (rol.equalsIgnoreCase("CANDIDATO")) {
                return "redirect:/candidato/vacantes"; // Va al portal del candidato
            } else {
                return "redirect:/reclutador/dashboard"; // Va al dashboard corporativo
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas.");
        return "redirect:/login";
    }
}