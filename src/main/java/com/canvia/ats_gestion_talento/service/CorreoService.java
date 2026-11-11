package com.canvia.ats_gestion_talento.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorreoService {

    private final JavaMailSender mailSender;

    public boolean enviarCorreoSimple(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(destinatario);
            mailMessage.setSubject(asunto);
            mailMessage.setText(mensaje);
            // mailMessage.setFrom("tu_correo@gmail.com"); // Opcional, Spring lo toma del yml

            mailSender.send(mailMessage);
            log.info("Correo enviado exitosamente a: {}", destinatario);
            return true;
        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", destinatario, e.getMessage());
            return false;
        }
    }
}