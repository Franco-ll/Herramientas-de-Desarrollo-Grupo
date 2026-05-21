package com.scholarstay.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.ReservaRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final NotificacionService notificacionService;

    public ReservaService(ReservaRepository reservaRepository, NotificacionService notificacionService) {
        this.reservaRepository = reservaRepository;
        this.notificacionService = notificacionService;
    }

    public Reserva crearReserva(Reserva reserva, Usuario usuario) {
        // Validación obligatoria de fechas nulas
        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
        }

        // Validamos que la fecha final sea posterior a la inicial
        // para evitar reservas inconsistentes o fechas negativas
        if (reserva.getFechaFin().isBefore(reserva.getFechaInicio()) || reserva.getFechaFin().isEqual(reserva.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha final debe ser posterior a la fecha inicial.");
        }

        // Impedir reservas en fechas pasadas
        if (reserva.getFechaInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden realizar reservas en fechas pasadas.");
        }

        // Verificamos disponibilidad en la base de datos para impedir superposiciones
        boolean superpuesta = reservaRepository.existeSuperposicion(
                reserva.getAlojamiento().getId(), 
                reserva.getFechaInicio(), 
                reserva.getFechaFin()
        );
        
        if (superpuesta) {
            throw new IllegalStateException("El alojamiento ya no está disponible para las fechas seleccionadas.");
        }

        // El backend debe calcular el precio REAL de la reserva, ignorando montos manipulados por el cliente
        // Se calculan los días y se multiplica por la proporción del precio mensual
        long dias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
        double precioPorDia = reserva.getAlojamiento().getPrecioMensual() / 30.0;
        double precioCalculado = dias * precioPorDia;
        
        // Redondear a 2 decimales para integridad del dato
        precioCalculado = Math.round(precioCalculado * 100.0) / 100.0;

        reserva.setPrecioTotal(precioCalculado);
        reserva.setUsuario(usuario);
        reserva.setEstado("CONFIRMADA"); // Por ahora se marca confirmada al no haber pasarela real
        
        Reserva guardada = reservaRepository.save(reserva);
        
        notificacionService.crearNotificacion(usuario, "Tu reserva para el alojamiento " + reserva.getAlojamiento().getTitulo() + " ha sido procesada.", "RESERVA");
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
