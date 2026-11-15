package com.canvia.ats_gestion_talento.service;

import com.canvia.ats_gestion_talento.entity.Notificacion;
import com.canvia.ats_gestion_talento.entity.Postulacion;
import com.canvia.ats_gestion_talento.entity.Usuario;
import com.canvia.ats_gestion_talento.dao.NotificacionDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionDAO notificacionRepository;
    private final CorreoService correoService;

    @Transactional
    public void registrarYEnviarNotificacion(Usuario usuario, Postulacion postulacion, String tipo, String asunto, String mensaje) {
        
        // 1. Crear el registro en BD
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setPostulacion(postulacion);
        notificacion.setTipo(tipo);
        notificacion.setAsunto(asunto);
        notificacion.setMensaje(mensaje);
        notificacion.setCanal("CORREO");

        // 2. Intentar enviar el correo
        boolean enviado = correoService.enviarCorreoSimple(usuario.getCorreo(), asunto, mensaje);
        
        // 3. Actualizar estado según resultado
        if (enviado) {
            notificacion.setEstadoEnvio("ENVIADA");
        } else {
            notificacion.setEstadoEnvio("ERROR");
        }

        // 4. Guardar en historial de notificaciones
        notificacionRepository.save(notificacion);
    }
}