package com.scholarstay.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Notificacion;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.NotificacionRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion crearNotificacion(Usuario usuario, String mensaje, String tipo) {

        Notificacion notificacion =
                new Notificacion(usuario, mensaje, tipo);

        return notificacionRepository.save(notificacion);
    }

    public List<Notificacion> obtenerNotificaciones(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public List<Notificacion> obtenerNoLeidas(Long usuarioId) {
        return notificacionRepository
                .findByUsuarioIdAndLeidoFalseOrderByFechaDesc(usuarioId);
    }

    public void marcarComoLeida(Long notificacionId) {

        notificacionRepository.findById(notificacionId)
                .ifPresent(notificacion -> {

                    notificacion.setLeido(true);

                    notificacionRepository.save(notificacion);
                });
    }

    public void marcarTodasComoLeidas(Long usuarioId) {

        List<Notificacion> notificaciones =
                notificacionRepository
                        .findByUsuarioIdAndLeidoFalseOrderByFechaDesc(usuarioId);

        for (Notificacion notificacion : notificaciones) {
            notificacion.setLeido(true);
        }

        notificacionRepository.saveAll(notificaciones);
    }

    public Notificacion obtenerPorId(Long id) {
        return notificacionRepository.findById(id).orElse(null);
    }
}