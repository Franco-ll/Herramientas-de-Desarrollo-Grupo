package com.scholarstay.app.controller.api;

import com.scholarstay.app.dto.NotificacionDTO;
import com.scholarstay.app.model.Notificacion;
import com.scholarstay.app.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionRestController {

    private final NotificacionService notificacionService;

    public NotificacionRestController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionDTO>> obtenerNotificaciones(@PathVariable Long usuarioId) {
        List<Notificacion> notificaciones = notificacionService.obtenerNotificaciones(usuarioId);
        List<NotificacionDTO> dtos = notificaciones.stream().map(n -> {
            NotificacionDTO dto = new NotificacionDTO();
            dto.setId(n.getId());
            dto.setMensaje(n.getMensaje());
            dto.setTipo(n.getTipo());
            dto.setLeido(n.getLeido());
            dto.setFecha(n.getFecha());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok().build();
    }
}
