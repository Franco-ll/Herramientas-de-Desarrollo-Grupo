package com.scholarstay.app.service;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.Resena;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import com.scholarstay.app.repository.ResenaRepository;
import com.scholarstay.app.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private ResenaService resenaService;

    private Usuario autor;
    private Usuario anfitrion;
    private Alojamiento alojamiento;
    private Resena resena;

    @BeforeEach
    void setUp() {
        autor = new Usuario();
        autor.setId(1L);
        autor.setNombre("Estudiante Uno");

        anfitrion = new Usuario();
        anfitrion.setId(2L);
        anfitrion.setNombre("Anfitrion Uno");

        alojamiento = new Alojamiento();
        alojamiento.setId(10L);
        alojamiento.setTitulo("Apartamento Academico");
        alojamiento.setAnfitrion(anfitrion);

        resena = new Resena();
        resena.setAlojamiento(alojamiento);
    }

    //Este test verifica el caso exitoso de crear una reseña
    @Test
    void crearResena_Exito() {
        resena.setCalificacion(5);
        resena.setComentario("El alojamiento es excelente y muy comodo.");

        when(reservaRepository.existsByUsuarioIdAndAlojamientoIdAndEstado(eq(1L), eq(10L), eq("CONFIRMADA")))
                .thenReturn(true);
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Resena resultado = resenaService.crearResena(resena, autor);

        assertNotNull(resultado);
        assertEquals(autor, resultado.getUsuario());
        assertEquals("El alojamiento es excelente y muy comodo.", resultado.getComentario());
        verify(resenaRepository, times(1)).save(resena);
        verify(notificacionService, times(1)).crearNotificacion(eq(anfitrion), anyString(), eq(NotificationType.RESENA), eq(NotificationPriority.INFO), anyLong());
    }

    //Este test verifica que no se pueda crear una reseña si la calificación es nula
    @Test
    void crearResena_CalificacionNula_ThrowsIllegalArgumentException() {
        resena.setCalificacion(null);
        resena.setComentario("El alojamiento es excelente.");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("La calificación es obligatoria.", ex.getMessage());
        verify(resenaRepository, never()).save(any(Resena.class));
    }
    
    //Este test verifica que no se pueda crear una reseña si la calificación está fuera del rango válido
    @Test
    void crearResena_CalificacionFueraDeRango_ThrowsIllegalArgumentException() {
        resena.setComentario("El alojamiento es excelente.");

        // Menor a 1
        resena.setCalificacion(0);
        IllegalArgumentException exMenor = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("La calificación debe estar entre 1 y 5.", exMenor.getMessage());

        // Mayor a 5
        resena.setCalificacion(6);
        IllegalArgumentException exMayor = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("La calificación debe estar entre 1 y 5.", exMayor.getMessage());
        
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    //Este test verifica que no se pueda crear una reseña si el comentario es nulo, vacío o solo espacios
    @Test
    void crearResena_ComentarioVacio_ThrowsIllegalArgumentException() {
        resena.setCalificacion(4);

        // Null
        resena.setComentario(null);
        IllegalArgumentException exNull = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("El comentario no puede estar vacío.", exNull.getMessage());

        // Vacio
        resena.setComentario("");
        IllegalArgumentException exVacio = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("El comentario no puede estar vacío.", exVacio.getMessage());

        // Espacios
        resena.setComentario("    ");
        IllegalArgumentException exEspacios = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("El comentario no puede estar vacío.", exEspacios.getMessage());

        verify(resenaRepository, never()).save(any(Resena.class));
    }

    //Este test verifica que no se pueda crear una reseña si el comentario es demasiado corto
    @Test
    void crearResena_ComentarioMuyCorto_ThrowsIllegalArgumentException() {
        resena.setCalificacion(4);
        resena.setComentario("Muy malo"); // 8 caracteres

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("El comentario debe tener entre 10 y 1000 caracteres.", ex.getMessage());
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    //Este test verifica que no se pueda crear una reseña si el comentario es demasiado largo
    @Test
    void crearResena_ComentarioMuyLargo_ThrowsIllegalArgumentException() {
        resena.setCalificacion(4);
        // Generar comentario de 1001 caracteres
        String largo = "a".repeat(1001);
        resena.setComentario(largo);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("El comentario debe tener entre 10 y 1000 caracteres.", ex.getMessage());
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    //Este test verifica que no se pueda crear una reseña si el usuario no tiene una reserva confirmada en el alojamiento
    @Test
    void crearResena_SinReservaConfirmada_ThrowsIllegalStateException() {
        resena.setCalificacion(4);
        resena.setComentario("El alojamiento es excelente y muy comodo.");

        when(reservaRepository.existsByUsuarioIdAndAlojamientoIdAndEstado(eq(1L), eq(10L), eq("CONFIRMADA")))
                .thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            resenaService.crearResena(resena, autor);
        });
        assertEquals("Solo puedes dejar una reseña si has completado una reserva en este alojamiento.", ex.getMessage());
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    //Este test verifica que el comentario de la reseña se sanitiza correctamente para prevenir ataques XSS
    @Test
    void crearResena_SanitizacionSimpleXSS() {
        resena.setCalificacion(4);
        resena.setComentario("<script>alert('hack')</script>"); // 30 caracteres

        when(reservaRepository.existsByUsuarioIdAndAlojamientoIdAndEstado(eq(1L), eq(10L), eq("CONFIRMADA")))
                .thenReturn(true);
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Resena resultado = resenaService.crearResena(resena, autor);

        assertNotNull(resultado);
        assertEquals("&lt;script&gt;alert('hack')&lt;/script&gt;", resultado.getComentario());
    }


    //Este test verifica que se puedan obtener las reseñas de un alojamiento correctamente
    @Test
    void obtenerResenasPorAlojamiento_Exito() {
        List<Resena> lista = new ArrayList<>();
        lista.add(resena);
        when(resenaRepository.findByAlojamientoIdOrderByFechaPublicacionDesc(10L)).thenReturn(lista);

        List<Resena> resultado = resenaService.obtenerResenasPorAlojamiento(10L);

        assertEquals(1, resultado.size());
        assertEquals(resena, resultado.get(0));
    }

    //Este test verifica que se puedan obtener las reseñas de un alojamiento de manera paginada correctamente
    @Test
    void obtenerResenasPaginadasPorAlojamiento_Exito() {
        List<Resena> lista = new ArrayList<>();
        lista.add(resena);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Resena> pagina = new PageImpl<>(lista, pageable, 1);

        when(resenaRepository.findByAlojamientoIdOrderByFechaPublicacionDesc(10L, pageable)).thenReturn(pagina);

        Page<Resena> resultado = resenaService.obtenerResenasPaginadasPorAlojamiento(10L, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(resena, resultado.getContent().get(0));
    }
}
