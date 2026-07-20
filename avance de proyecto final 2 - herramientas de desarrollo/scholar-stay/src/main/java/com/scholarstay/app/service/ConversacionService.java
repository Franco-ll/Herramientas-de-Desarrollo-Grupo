package com.scholarstay.app.service;

import com.scholarstay.app.dto.ConversacionDTO;
import com.scholarstay.app.model.Conversacion;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.ConversacionRepository;
import com.scholarstay.app.repository.MensajeRepository;
import com.scholarstay.app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversacionService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    public ConversacionService(ConversacionRepository conversacionRepository,
                                MensajeRepository mensajeRepository,
                                UsuarioRepository usuarioRepository) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ConversacionDTO obtenerOCrear(Long usuarioId, Long otroUsuarioId) {
        if (usuarioId.equals(otroUsuarioId)) {
            throw new IllegalArgumentException("No puedes crear una conversación contigo mismo.");
        }

        Conversacion conversacion = conversacionRepository.findByUsuarios(usuarioId, otroUsuarioId)
                .orElse(null);

        if (conversacion != null) {
            boolean usuarioEsUno = conversacion.getUsuario1().getId().equals(usuarioId);
            if (usuarioEsUno && conversacion.isEliminadoPorUsuario1()) {
                conversacion.setEliminadoPorUsuario1(false);
                conversacionRepository.save(conversacion);
            } else if (!usuarioEsUno && conversacion.isEliminadoPorUsuario2()) {
                conversacion.setEliminadoPorUsuario2(false);
                conversacionRepository.save(conversacion);
            }
            return toDTO(conversacion, usuarioId);
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Usuario otroUsuario = usuarioRepository.findById(otroUsuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El otro usuario no existe."));

        conversacion = new Conversacion(usuario, otroUsuario);
        conversacion = conversacionRepository.save(conversacion);
        return toDTO(conversacion, usuarioId);
    }

    public List<ConversacionDTO> listarActivas(Long usuarioId) {
        return conversacionRepository.findActivasByUsuarioId(usuarioId).stream()
                .map(c -> toDTO(c, usuarioId))
                .collect(Collectors.toList());
    }

    public Conversacion obtenerPorId(Long conversacionId) {
        return conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new IllegalArgumentException("Conversación no encontrada."));
    }

    public void verificarParticipacion(Long conversacionId, Long usuarioId) {
        Conversacion c = obtenerPorId(conversacionId);
        if (!c.getUsuario1().getId().equals(usuarioId) && !c.getUsuario2().getId().equals(usuarioId)) {
            throw new SecurityException("No tienes acceso a esta conversación.");
        }
    }

    @Transactional
    public void eliminarParaUsuario(Long conversacionId, Long usuarioId) {
        Conversacion c = obtenerPorId(conversacionId);
        verificarParticipacion(conversacionId, usuarioId);

        if (c.getUsuario1().getId().equals(usuarioId)) {
            c.setEliminadoPorUsuario1(true);
        } else {
            c.setEliminadoPorUsuario2(true);
        }
        conversacionRepository.save(c);
    }

    public ConversacionDTO toDTO(Conversacion c, Long usuarioId) {
        ConversacionDTO dto = new ConversacionDTO();
        dto.setId(c.getId());

        boolean usuarioEsUno = c.getUsuario1().getId().equals(usuarioId);
        Usuario otro = usuarioEsUno ? c.getUsuario2() : c.getUsuario1();

        dto.setOtroUsuarioId(otro.getId());
        dto.setOtroUsuarioNombre(otro.getNombre());
        dto.setOtroUsuarioAvatar(otro.getAvatar());
        dto.setUltimoMensaje(c.getUltimoMensaje());
        dto.setFechaUltimoMensaje(c.getFechaUltimoMensaje());
        dto.setNoLeidos(mensajeRepository.countByConversacionIdAndLeidoFalseAndRemitenteIdNot(c.getId(), usuarioId));
        return dto;
    }
}
