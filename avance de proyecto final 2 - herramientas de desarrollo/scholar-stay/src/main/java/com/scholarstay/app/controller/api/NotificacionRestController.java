package com.scholarstay.app.controller.api;

import com.scholarstay.app.dto.NotificacionDTO;
import com.scholarstay.app.model.Notificacion;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.security.CustomUserDetails;
import com.scholarstay.app.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionRestController {

    private final NotificacionService notificacionService;

    public NotificacionRestController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    private Usuario getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails customUser) {
            return customUser.getUsuario();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> obtenerNotificaciones() {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificacionService.obtenerNotificacionesDTO(usuario.getId()));
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificacionDTO>> obtenerNoLeidas() {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificacionService.obtenerNoLeidasDTO(usuario.getId()));
    }

    @GetMapping("/count-no-leidas")
    public ResponseEntity<Long> contarNoLeidas() {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificacionService.contarNoLeidas(usuario.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtenerNotificacion(@PathVariable Long id) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        try {
            NotificacionDTO dto = notificacionService.obtenerNotificacionDTO(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        try {
            Notificacion n = notificacionService.obtenerPorId(id);
            if (!n.getUsuario().getId().equals(usuario.getId())) return ResponseEntity.status(403).build();
            notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas() {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        notificacionService.marcarTodasComoLeidas(usuario.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        try {
            Notificacion n = notificacionService.obtenerPorId(id);
            if (!n.getUsuario().getId().equals(usuario.getId())) return ResponseEntity.status(403).build();
            notificacionService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/leidas")
    public ResponseEntity<Void> eliminarLeidas() {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        notificacionService.eliminarLeidas(usuario.getId());
        return ResponseEntity.ok().build();
    }
}
