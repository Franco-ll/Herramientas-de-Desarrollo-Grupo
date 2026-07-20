package com.scholarstay.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Pago;
import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import com.scholarstay.app.repository.PagoRepository;
import com.scholarstay.app.repository.ReservaRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final NotificacionService notificacionService;

    // Métodos de pago aceptados por la plataforma
    private static final Set<String> METODOS_VALIDOS = Set.of(
        "TARJETA", "YAPE", "PLIN", "TRANSFERENCIA"
    );

    public PagoService(PagoRepository pagoRepository,
                       ReservaRepository reservaRepository,
                       NotificacionService notificacionService) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Procesa el pago de una reserva.
     * Valida que la reserva exista, que no haya sido pagada antes,
     * que el método de pago sea válido y que el monto sea positivo.
     * Registra el pago como COMPLETADO y notifica al usuario.
     */
    public Pago procesarPago(Long reservaId, String metodoPago) {

        // Validar que el método de pago sea uno de los aceptados
        if (metodoPago == null || metodoPago.isBlank()) {
            throw new IllegalArgumentException(
                "El método de pago es obligatorio."
            );
        }
        if (!METODOS_VALIDOS.contains(metodoPago.toUpperCase())) {
            throw new IllegalArgumentException(
                "Método de pago no válido. Los métodos aceptados son: TARJETA, YAPE, PLIN, TRANSFERENCIA."
            );
        }

        // Verificar que la reserva exista
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No existe una reserva con el ID proporcionado."
            ));

        // Verificar que la reserva esté en estado CONFIRMADA antes de pagar
        if (!"CONFIRMADA".equals(reserva.getEstado())) {
            throw new IllegalStateException(
                "Solo se pueden pagar reservas en estado CONFIRMADA."
            );
        }

        // Verificar que la reserva no tenga ya un pago registrado
        // Esto previene pagos duplicados por doble click o reintentos
        if (pagoRepository.existsByReservaId(reservaId)) {
            throw new IllegalStateException(
                "Esta reserva ya tiene un pago registrado."
            );
        }

        // Verificar que el monto de la reserva sea válido
        if (reserva.getPrecioTotal() == null || reserva.getPrecioTotal() <= 0) {
            throw new IllegalStateException(
                "El monto de la reserva no es válido para procesar el pago."
            );
        }

        // Crear y registrar el pago como COMPLETADO
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(reserva.getPrecioTotal());
        pago.setMetodoPago(metodoPago.toUpperCase());
        pago.setEstado("COMPLETADO");
        pago.setFechaPago(LocalDateTime.now());

        Pago guardado = pagoRepository.save(pago);

        // Notificar al usuario que su pago fue procesado exitosamente
        notificacionService.crearNotificacion(
            reserva.getUsuario(),
            "Tu pago de S/ " + reserva.getPrecioTotal() +
            " por el alojamiento \"" + reserva.getAlojamiento().getTitulo() +
            "\" ha sido procesado exitosamente.",
            NotificationType.PAGO, NotificationPriority.SUCCESS, reserva.getId()
        );

        return guardado;
    }

    /**
     * Cancela un pago existente marcándolo como FALLIDO.
     * Solo se pueden cancelar pagos en estado COMPLETADO.
     * Notifica al usuario sobre la cancelación.
     */
    public Pago cancelarPago(Long pagoId) {

        Pago pago = pagoRepository.findById(pagoId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No existe un pago con el ID proporcionado."
            ));

        // Solo se pueden cancelar pagos que estén COMPLETADOS
        if (!"COMPLETADO".equals(pago.getEstado())) {
            throw new IllegalStateException(
                "Solo se pueden cancelar pagos en estado COMPLETADO."
            );
        }

        pago.setEstado("FALLIDO");
        Pago actualizado = pagoRepository.save(pago);

        // Notificar al usuario sobre la cancelación del pago
        notificacionService.crearNotificacion(
            pago.getReserva().getUsuario(),
            "Tu pago por el alojamiento \"" +
            pago.getReserva().getAlojamiento().getTitulo() +
            "\" ha sido cancelado.",
            NotificationType.PAGO, NotificationPriority.WARNING, pago.getReserva().getId()
        );

        return actualizado;
    }

    /**
     * Obtiene todos los pagos de un usuario específico.
     * Útil para el historial de pagos en el perfil del usuario.
     */
    public List<Pago> obtenerPagosPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID de usuario es obligatorio.");
        }
        return pagoRepository.findByReservaUsuarioId(usuarioId);
    }

    /**
     * Lista todos los pagos registrados en el sistema.
     * Usado principalmente por el panel de administración.
     */
    public List<Pago> listar() {
        return pagoRepository.findAll();
    }
}