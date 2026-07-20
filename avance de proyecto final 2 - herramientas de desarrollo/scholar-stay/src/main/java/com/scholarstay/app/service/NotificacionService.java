package com.scholarstay.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scholarstay.app.dto.NotificacionDTO;
import com.scholarstay.app.model.Notificacion;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import com.scholarstay.app.repository.NotificacionRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificationUrlResolver urlResolver;

    public NotificacionService(NotificacionRepository notificacionRepository, NotificationUrlResolver urlResolver) {
        this.notificacionRepository = notificacionRepository;
        this.urlResolver = urlResolver;
    }

    public Notificacion crearNotificacion(Usuario usuario, String mensaje, NotificationType type, NotificationPriority priority, Long entityId) {
        Notificacion notificacion = new Notificacion(usuario, mensaje, type, priority, entityId);
        return notificacionRepository.save(notificacion);
    }

    public List<NotificacionDTO> obtenerNotificacionesDTO(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificacionDTO> obtenerNoLeidasDTO(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeidoFalseOrderByFechaDesc(usuarioId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public NotificacionDTO obtenerNotificacionDTO(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada."));
        return toDTO(n);
    }

    public Notificacion obtenerPorId(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada."));
    }

    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidoFalse(usuarioId);
    }

    @Transactional
    public void marcarComoLeida(Long notificacionId) {
        notificacionRepository.findById(notificacionId).ifPresent(n -> {
            n.setLeido(true);
            n.setFechaLeido(java.time.LocalDateTime.now());
            notificacionRepository.save(n);
        });
    }

    @Transactional
    public void marcarTodasComoLeidas(Long usuarioId) {
        notificacionRepository.marcarTodasComoLeidas(usuarioId);
    }

    @Transactional
    public void eliminar(Long notificacionId) {
        notificacionRepository.deleteById(notificacionId);
    }

    @Transactional
    public void eliminarLeidas(Long usuarioId) {
        notificacionRepository.eliminarLeidas(usuarioId);
    }

    private NotificacionDTO toDTO(Notificacion n) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(n.getId());
        dto.setMensaje(n.getMensaje());
        dto.setType(n.getType());
        dto.setPriority(n.getPriority());
        dto.setEntityId(n.getEntityId());
        dto.setLeido(n.getLeido());
        dto.setFecha(n.getFecha());
        dto.setFechaLeido(n.getFechaLeido());
        dto.setRedirectUrl(urlResolver.resolve(n.getType(), n.getEntityId()));

        String opacity = n.getLeido() ? "opacity-70" : "opacity-100";
        NotificationPriority safePriority = n.getPriority() != null ? n.getPriority() : NotificationPriority.INFO;
        String borderColor = switch (safePriority) {
            case ERROR   -> "border-error";
            case WARNING -> "border-tertiary";
            case SUCCESS -> "border-secondary";
            case INFO    -> "border-primary";
        };
        dto.setCssClass(opacity + " border-l-4 " + borderColor);

        return dto;
    }
}
