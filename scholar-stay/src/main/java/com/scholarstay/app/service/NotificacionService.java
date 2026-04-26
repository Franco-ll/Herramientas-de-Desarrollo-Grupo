package com.scholarstay.app.service;

import com.scholarstay.app.model.Notificacion;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion crearNotificacion(Usuario usuario, String mensaje, String tipo) {
        Notificacion notificacion = new Notificacion(usuario, mensaje, tipo);
        return notificacionRepository.save(notificacion);
    }

    public List<Notificacion> obtenerNotificaciones(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
    
    public List<Notificacion> obtenerNoLeidas(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeidoFalseOrderByFechaDesc(usuarioId);
    }

    public void marcarComoLeida(Long notificacionId) {
        notificacionRepository.findById(notificacionId).ifPresent(notificacion -> {
            notificacion.setLeido(true);
            notificacionRepository.save(notificacion);
        });
    }
}
