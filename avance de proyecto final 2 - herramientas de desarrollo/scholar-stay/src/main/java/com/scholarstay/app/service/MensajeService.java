package com.scholarstay.app.service;

import com.scholarstay.app.dto.MensajeDTO;
import com.scholarstay.app.model.Conversacion;
import com.scholarstay.app.model.Mensaje;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import com.scholarstay.app.repository.ConversacionRepository;
import com.scholarstay.app.repository.MensajeRepository;
import com.scholarstay.app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final ConversacionRepository conversacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final ConversacionService conversacionService;

    public MensajeService(MensajeRepository mensajeRepository,
                           ConversacionRepository conversacionRepository,
                           UsuarioRepository usuarioRepository,
                           NotificacionService notificacionService,
                           ConversacionService conversacionService) {
        this.mensajeRepository = mensajeRepository;
        this.conversacionRepository = conversacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.conversacionService = conversacionService;
    }

    @Transactional
    public MensajeDTO enviar(Long conversacionId, Long remitenteId, String contenido) {
        System.out.println("[DEBUG ENVIAR] ===== INICIO =====");
        System.out.println("[DEBUG ENVIAR] conversacionId=" + conversacionId + ", remitenteId=" + remitenteId + ", contenido=" + contenido);

        System.out.println("[DEBUG ENVIAR] paso 1: verificarParticipacion...");
        conversacionService.verificarParticipacion(conversacionId, remitenteId);
        System.out.println("[DEBUG ENVIAR] paso 1: OK");

        System.out.println("[DEBUG ENVIAR] paso 2: obtenerPorId...");
        Conversacion conversacion = conversacionService.obtenerPorId(conversacionId);
        System.out.println("[DEBUG ENVIAR] paso 2: OK, conversacion.id=" + conversacion.getId());

        System.out.println("[DEBUG ENVIAR] paso 3: buscar remitente...");
        Usuario remitente = usuarioRepository.findById(remitenteId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        System.out.println("[DEBUG ENVIAR] paso 3: OK, remitente.id=" + remitente.getId() + ", nombre=" + remitente.getNombre());

        System.out.println("[DEBUG ENVIAR] paso 4: validar contenido...");
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío.");
        }
        System.out.println("[DEBUG ENVIAR] paso 4: OK");

        System.out.println("[DEBUG ENVIAR] paso 5: crear new Mensaje...");
        Mensaje mensaje = new Mensaje(conversacion, remitente, contenido.trim());
        System.out.println("[DEBUG ENVIAR] paso 5: OK");

        System.out.println("[DEBUG ENVIAR] paso 6: mensajeRepository.save...");
        mensaje = mensajeRepository.save(mensaje);
        System.out.println("[DEBUG ENVIAR] paso 6: OK, mensaje.id=" + mensaje.getId() + ", fecha=" + mensaje.getFecha());

        System.out.println("[DEBUG ENVIAR] paso 7: actualizar conversacion...");
        conversacion.setUltimoMensaje(mensaje.getContenido());
        conversacion.setFechaUltimoMensaje(mensaje.getFecha());
        System.out.println("[DEBUG ENVIAR] paso 7: OK");

        System.out.println("[DEBUG ENVIAR] paso 8: conversacionRepository.save...");
        conversacionRepository.save(conversacion);
        System.out.println("[DEBUG ENVIAR] paso 8: OK");

        System.out.println("[DEBUG ENVIAR] paso 9: obtener destinatario...");
        Usuario destinatario = conversacion.getUsuario1().getId().equals(remitenteId)
                ? conversacion.getUsuario2()
                : conversacion.getUsuario1();
        System.out.println("[DEBUG ENVIAR] paso 9: OK, destinatario.id=" + destinatario.getId() + ", nombre=" + destinatario.getNombre());

        System.out.println("[DEBUG ENVIAR] paso 10: notificacionService.crearNotificacion...");
        notificacionService.crearNotificacion(
                destinatario,
                remitente.getNombre() + " te ha enviado un mensaje: " + contenido,
                NotificationType.MENSAJE,
                NotificationPriority.INFO,
                conversacionId
        );
        System.out.println("[DEBUG ENVIAR] paso 10: OK");

        System.out.println("[DEBUG ENVIAR] paso 11: toDTO...");
        MensajeDTO dto = toDTO(mensaje, remitenteId);
        System.out.println("[DEBUG ENVIAR] paso 11: OK, dto.id=" + dto.getId());

        System.out.println("[DEBUG ENVIAR] ===== FIN OK =====");
        return dto;
    }

    public List<MensajeDTO> obtenerMensajes(Long conversacionId, Long usuarioId) {
        conversacionService.verificarParticipacion(conversacionId, usuarioId);
        return mensajeRepository.findByConversacionIdOrderByFechaAsc(conversacionId).stream()
                .map(m -> toDTO(m, usuarioId))
                .collect(Collectors.toList());
    }

    @Transactional
    public int marcarLeidos(Long conversacionId, Long usuarioId) {
        conversacionService.verificarParticipacion(conversacionId, usuarioId);
        return mensajeRepository.marcarLeidosPorConversacion(conversacionId, usuarioId);
    }

    public long contarNoLeidos(Long usuarioId) {
        return mensajeRepository.countNoLeidosByUsuarioId(usuarioId);
    }

    public long contarNoLeidosPorConversacion(Long conversacionId, Long usuarioId) {
        return mensajeRepository.countByConversacionIdAndLeidoFalseAndRemitenteIdNot(conversacionId, usuarioId);
    }

    private MensajeDTO toDTO(Mensaje m, Long usuarioId) {
        MensajeDTO dto = new MensajeDTO();
        dto.setId(m.getId());
        dto.setConversacionId(m.getConversacion().getId());
        dto.setRemitenteId(m.getRemitente().getId());
        dto.setContenido(m.getContenido());
        dto.setFecha(m.getFecha());
        dto.setLeido(m.isLeido());
        dto.setEsMio(m.getRemitente().getId().equals(usuarioId));
        return dto;
    }
}
