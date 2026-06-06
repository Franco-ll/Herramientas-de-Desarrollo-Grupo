package com.scholarstay.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuario;
    private Alojamiento alojamiento;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Perez");
        usuario.setEmail("juan.perez@universidad.edu");

        alojamiento = new Alojamiento();
        alojamiento.setId(10L);
        alojamiento.setTitulo("Residencia Universitaria");
        alojamiento.setPrecioMensual(600.0); // 20.0 por dia

        reserva = new Reserva();
        reserva.setAlojamiento(alojamiento);
    }

    @Test
    void crearReserva_Exito() {
        // Reservar por 10 dias a partir de mañana
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fin = LocalDate.now().plusDays(11);
        
        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);

        when(reservaRepository.existeSuperposicion(eq(10L), eq(inicio), eq(fin))).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva resultado = reservaService.crearReserva(reserva, usuario);

        assertNotNull(resultado);
        assertEquals("CONFIRMADA", resultado.getEstado());
        assertEquals(usuario, resultado.getUsuario());
        // 10 dias * (600.0 / 30.0) = 200.0
        assertEquals(200.0, resultado.getPrecioTotal());
        verify(reservaRepository, times(1)).save(reserva);
        verify(notificacionService, times(1)).crearNotificacion(eq(usuario), anyString(), eq("RESERVA"));
    }

    @Test
    void crearReserva_FechasNulas_ThrowsIllegalArgumentException() {
        reserva.setFechaInicio(null);
        reserva.setFechaFin(LocalDate.now().plusDays(5));

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.crearReserva(reserva, usuario);
        });
        assertEquals("Las fechas de inicio y fin son obligatorias.", ex1.getMessage());

        reserva.setFechaInicio(LocalDate.now().plusDays(1));
        reserva.setFechaFin(null);

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.crearReserva(reserva, usuario);
        });
        assertEquals("Las fechas de inicio y fin son obligatorias.", ex2.getMessage());
    }

    @Test
    void crearReserva_FechaFinAntesDeInicio_ThrowsIllegalArgumentException() {
        reserva.setFechaInicio(LocalDate.now().plusDays(5));
        reserva.setFechaFin(LocalDate.now().plusDays(3));

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.crearReserva(reserva, usuario);
        });
        assertEquals("La fecha final debe ser posterior a la fecha inicial.", ex1.getMessage());

        // Fecha de inicio igual a fin
        reserva.setFechaInicio(LocalDate.now().plusDays(3));
        reserva.setFechaFin(LocalDate.now().plusDays(3));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.crearReserva(reserva, usuario);
        });
        assertEquals("La fecha final debe ser posterior a la fecha inicial.", ex2.getMessage());
    }

    @Test
    void crearReserva_FechaInicioPasada_ThrowsIllegalArgumentException() {
        reserva.setFechaInicio(LocalDate.now().minusDays(1));
        reserva.setFechaFin(LocalDate.now().plusDays(5));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.crearReserva(reserva, usuario);
        });
        assertEquals("No se pueden realizar reservas en fechas pasadas.", ex.getMessage());
    }

    @Test
    void crearReserva_Superpuesta_ThrowsIllegalStateException() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fin = LocalDate.now().plusDays(5);
        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);

        when(reservaRepository.existeSuperposicion(eq(10L), eq(inicio), eq(fin))).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            reservaService.crearReserva(reserva, usuario);
        });
        assertEquals("El alojamiento ya no está disponible para las fechas seleccionadas.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void confirmarReserva_Exito() {
        reserva.setId(100L);
        reserva.setEstado("PENDIENTE");
        reserva.setUsuario(usuario);

        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva resultado = reservaService.confirmarReserva(100L);

        assertNotNull(resultado);
        assertEquals("CONFIRMADA", resultado.getEstado());
        verify(reservaRepository, times(1)).save(reserva);
        verify(notificacionService, times(1)).crearNotificacion(eq(usuario), anyString(), eq("RESERVA"));
    }

    @Test
    void confirmarReserva_Inexistente_ThrowsRuntimeException() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            reservaService.confirmarReserva(999L);
        });
        assertEquals("Reserva no encontrada", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void obtenerReservasPorUsuario_Exito() {
        List<Reserva> lista = new ArrayList<>();
        lista.add(reserva);
        when(reservaRepository.findByUsuarioId(1L)).thenReturn(lista);

        List<Reserva> resultado = reservaService.obtenerReservasPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals(reserva, resultado.get(0));
    }
}
