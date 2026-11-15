package com.canvia.ats_gestion_talento.dao;

import com.canvia.ats_gestion_talento.entity.Notificacion;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class NotificacionDAO extends BaseDAO<Notificacion> {

  public NotificacionDAO() {
    super(Notificacion.class);
  }

  public List<Notificacion> findByUsuarioIdUsuarioOrderByFechaEnvioDesc(Integer idUsuario) {
    return entityManager
        .createQuery(
            "SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = :id ORDER BY n.fechaEnvio"
                + " DESC",
            Notificacion.class)
        .setParameter("id", idUsuario)
        .getResultList();
  }
}
