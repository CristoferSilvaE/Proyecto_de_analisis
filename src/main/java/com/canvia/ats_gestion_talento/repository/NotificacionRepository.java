package com.canvia.ats_gestion_talento.repository;

import com.canvia.ats_gestion_talento.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByUsuarioIdUsuarioOrderByFechaEnvioDesc(Integer idUsuario);
}