package com.scholarstay.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.ReservaRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final NotificacionService notificacionService;

    public ReservaService(ReservaRepository reservaRepository, NotificacionService notificacionService) {
        this.reservaRepository = reservaRepository;
        this.notificacionService = notificacionService;
    }

    public Reserva crearReserva(Reserva reserva, Usuario usuario) {
        reserva.setUsuario(usuario);
        reserva.setEstado("PENDIENTE");
        Reserva guardada = reservaRepository.save(reserva);
        
        notificacionService.crearNotificacion(usuario, "Tu reserva para el alojamiento " + reserva.getAlojamiento().getTitulo() + " ha sido creada.", "RESERVA");
        return guardada;
    }

    public Reserva confirmarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado("CONFIRMADA");
        Reserva actualizada = reservaRepository.save(reserva);
        
        notificacionService.crearNotificacion(reserva.getUsuario(), "Tu reserva para el alojamiento " + reserva.getAlojamiento().getTitulo() + " ha sido confirmada.", "RESERVA");
        return actualizada;
    }

    public List<Reserva> obtenerReservasPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}
