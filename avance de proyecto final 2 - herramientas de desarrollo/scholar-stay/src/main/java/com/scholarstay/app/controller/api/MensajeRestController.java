package com.scholarstay.app.controller.api;

import com.scholarstay.app.dto.ConversacionDTO;
import com.scholarstay.app.dto.MensajeDTO;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.security.CustomUserDetails;
import com.scholarstay.app.service.ConversacionService;
import com.scholarstay.app.service.MensajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeRestController {

    private final ConversacionService conversacionService;
    private final MensajeService mensajeService;

    public MensajeRestController(ConversacionService conversacionService, MensajeService mensajeService) {
        this.conversacionService = conversacionService;
        this.mensajeService = mensajeService;
    }

    private Usuario getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails customUser) {
            return customUser.getUsuario();
        }
        return null;
    }

    @GetMapping("/conversaciones")
    public ResponseEntity<List<ConversacionDTO>> listarConversaciones() {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(conversacionService.listarActivas(usuario.getId()));
    }

    @PostMapping("/conversaciones")
    public ResponseEntity<?> obtenerOCrearConversacion(@RequestBody Map<String, Long> body) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        Long otroUsuarioId = body.get("otroUsuarioId");
        if (otroUsuarioId == null) return ResponseEntity.badRequest().body(Map.of("error", "otroUsuarioId es requerido."));

        try {
            ConversacionDTO dto = conversacionService.obtenerOCrear(usuario.getId(), otroUsuarioId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/conversaciones/{id}")
    public ResponseEntity<?> obtenerMensajes(@PathVariable Long id) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        try {
            List<MensajeDTO> mensajes = mensajeService.obtenerMensajes(id, usuario.getId());
            return ResponseEntity.ok(mensajes);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/conversaciones/{id}/enviar")
    public ResponseEntity<?> enviarMensaje(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        String contenido = body.get("contenido");
        if (contenido == null || contenido.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El contenido no puede estar vacío."));
        }

        try {
            MensajeDTO dto = mensajeService.enviar(id, usuario.getId(), contenido);
            return ResponseEntity.ok(dto);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/conversaciones/{id}/leer")
    public ResponseEntity<?> marcarLeidos(@PathVariable Long id) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        try {
            mensajeService.marcarLeidos(id, usuario.getId());
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/conversaciones/{id}")
    public ResponseEntity<?> eliminarConversacion(@PathVariable Long id) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();

        try {
            conversacionService.eliminarParaUsuario(id, usuario.getId());
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/no-leidos")
    public ResponseEntity<Long> contarNoLeidos() {
        Usuario usuario = getLoggedUser();
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(mensajeService.contarNoLeidos(usuario.getId()));
    }
}
