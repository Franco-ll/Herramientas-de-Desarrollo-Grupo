package com.scholarstay.app.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.Pago;
import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import com.scholarstay.app.repository.PagoRepository;
import com.scholarstay.app.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private PagoService pagoService;

    private Usuario usuario;
    private Alojamiento alojamiento;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana Torres");
        usuario.setEmail("ana.torres@universidad.edu");

        alojamiento = new Alojamiento();
        alojamiento.setId(10L);
        alojamiento.setTitulo("Residencia San Marcos");
        alojamiento.setPrecioMensual(900.0);

        reserva = new Reserva();
        reserva.setId(100L);
        reserva.setUsuario(usuario);
        reserva.setAlojamiento(alojamiento);
        reserva.setEstado("CONFIRMADA");
        reserva.setPrecioTotal(300.0);
    }

    // =========================================================
    // TESTS DE procesarPago()
    // =========================================================

    @Test
    void procesarPago_Exito() {
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(pagoRepository.existsByReservaId(100L)).thenReturn(false);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        Pago resultado = pagoService.procesarPago(100L, "YAPE");

        assertNotNull(resultado);
        assertEquals("COMPLETADO", resultado.getEstado());
        assertEquals(300.0, resultado.getMonto());
        assertEquals("YAPE", resultado.getMetodoPago());
        assertNotNull(resultado.getFechaPago());

        verify(pagoRepository, times(1)).save(any(Pago.class));
        verify(notificacionService, times(1))
            .crearNotificacion(eq(usuario), anyString(), eq(NotificationType.PAGO), eq(NotificationPriority.SUCCESS), anyLong());
    }

    @Test
    void procesarPago_MetodoPagoNulo_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> pagoService.procesarPago(100L, null)
        );
        assertEquals("El método de pago es obligatorio.", ex.getMessage());

        // No debe llegar a consultar la reserva ni guardar nada
        verify(reservaRepository, never()).findById(any());
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void procesarPago_MetodoInvalido_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> pagoService.procesarPago(100L, "BITCOIN")
        );
        assertTrue(ex.getMessage().contains("Método de pago no válido"));
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void procesarPago_ReservaInexistente_ThrowsIllegalArgumentException() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> pagoService.procesarPago(999L, "TARJETA")
        );
        assertEquals(
            "No existe una reserva con el ID proporcionado.",
            ex.getMessage()
        );
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void procesarPago_ReservaNoConfirmada_ThrowsIllegalStateException() {
        reserva.setEstado("PENDIENTE");
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> pagoService.procesarPago(100L, "PLIN")
        );
        assertEquals(
            "Solo se pueden pagar reservas en estado CONFIRMADA.",
            ex.getMessage()
        );
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void procesarPago_PagoDuplicado_ThrowsIllegalStateException() {
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        // Simula que ya existe un pago para esta reserva
        when(pagoRepository.existsByReservaId(100L)).thenReturn(true);

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> pagoService.procesarPago(100L, "TRANSFERENCIA")
        );
        assertEquals(
            "Esta reserva ya tiene un pago registrado.",
            ex.getMessage()
        );
        verify(pagoRepository, never()).save(any());
    }

    // =========================================================
    // TESTS DE cancelarPago()
    // =========================================================

    @Test
    void cancelarPago_Exito() {
        Pago pago = new Pago();
        pago.setId(50L);
        pago.setEstado("COMPLETADO");
        pago.setMonto(300.0);
        pago.setReserva(reserva);

        when(pagoRepository.findById(50L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        Pago resultado = pagoService.cancelarPago(50L);

        assertEquals("FALLIDO", resultado.getEstado());
        verify(pagoRepository, times(1)).save(pago);
        verify(notificacionService, times(1))
            .crearNotificacion(eq(usuario), anyString(), eq(NotificationType.PAGO), eq(NotificationPriority.WARNING), anyLong());
    }

    @Test
    void cancelarPago_PagoYaFallido_ThrowsIllegalStateException() {
        Pago pago = new Pago();
        pago.setId(50L);
        pago.setEstado("FALLIDO");
        pago.setReserva(reserva);

        when(pagoRepository.findById(50L)).thenReturn(Optional.of(pago));

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> pagoService.cancelarPago(50L)
        );
        assertEquals(
            "Solo se pueden cancelar pagos en estado COMPLETADO.",
            ex.getMessage()
        );
        verify(pagoRepository, never()).save(any());
    }

    // =========================================================
    // TESTS DE obtenerPagosPorUsuario()
    // =========================================================

    @Test
    void obtenerPagosPorUsuario_Exito() {
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(300.0);
        pago.setEstado("COMPLETADO");

        when(pagoRepository.findByReservaUsuarioId(1L)).thenReturn(List.of(pago));

        List<Pago> resultado = pagoService.obtenerPagosPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals("COMPLETADO", resultado.get(0).getEstado());
        verify(pagoRepository, times(1)).findByReservaUsuarioId(1L);
    }

    @Test
    void obtenerPagosPorUsuario_IdNulo_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> pagoService.obtenerPagosPorUsuario(null)
        );
        assertEquals("El ID de usuario es obligatorio.", ex.getMessage());
        verify(pagoRepository, never()).findByReservaUsuarioId(any());
    }
}