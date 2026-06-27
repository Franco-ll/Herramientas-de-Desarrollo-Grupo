package com.scholarstay.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scholarstay.app.model.Compatibilidad;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.CompatibilidadRepository;
import com.scholarstay.app.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CompatibilidadServiceTest {

    @Mock
    private CompatibilidadRepository compatibilidadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CompatibilidadService compatibilidadService;

    private Usuario usuarioA;
    private Usuario usuarioB;
    private PerfilAcademico perfilA;
    private PerfilAcademico perfilB;

    @BeforeEach
    void setUp() {
        usuarioA = new Usuario();
        usuarioA.setId(1L);
        usuarioA.setNombre("Juan");

        perfilA = new PerfilAcademico();
        perfilA.setId(10L);
        perfilA.setUsuario(usuarioA);
        usuarioA.setPerfilAcademico(perfilA);

        usuarioB = new Usuario();
        usuarioB.setId(2L);
        usuarioB.setNombre("Pedro");

        perfilB = new PerfilAcademico();
        perfilB.setId(20L);
        perfilB.setUsuario(usuarioB);
        usuarioB.setPerfilAcademico(perfilB);
    }

    @Test
    void calcularCompatibilidadesParaUsuario_UsuarioSinPerfil_ReturnsEarly() {
        usuarioA.setPerfilAcademico(null);

        compatibilidadService.calcularCompatibilidadesParaUsuario(usuarioA);

        verify(usuarioRepository, never()).findAll();
        verify(compatibilidadRepository, never()).save(any(Compatibilidad.class));
    }

    @Test
    void calcularCompatibilidadesParaUsuario_ExitoAfinidadCompleta() {
        // Mismo perfil exacto
        perfilA.setCarrera("Ingenieria");
        perfilA.setHorarioEstudio("Mañana");
        perfilA.setHabitosRuido("Silencio");
        perfilA.setHabitosSueno("Madrugador");
        perfilA.setNivelTolerancia("Alto");

        perfilB.setCarrera("Ingenieria");
        perfilB.setHorarioEstudio("Mañana");
        perfilB.setHabitosRuido("Silencio");
        perfilB.setHabitosSueno("Madrugador");
        perfilB.setNivelTolerancia("Alto");

        List<Usuario> todosLosUsuarios = List.of(usuarioA, usuarioB);
        when(usuarioRepository.findAll()).thenReturn(todosLosUsuarios);
        when(compatibilidadRepository.findByUsuario1IdAndUsuario2Id(anyLong(), anyLong())).thenReturn(null);

        compatibilidadService.calcularCompatibilidadesParaUsuario(usuarioA);

        // Se calcula bidireccionalmente: A->B y B->A
        ArgumentCaptor<Compatibilidad> compatibilidadCaptor = ArgumentCaptor.forClass(Compatibilidad.class);
        verify(compatibilidadRepository, times(2)).save(compatibilidadCaptor.capture());

        List<Compatibilidad> guardados = compatibilidadCaptor.getAllValues();
        assertEquals(2, guardados.size());

        Compatibilidad comp1 = guardados.get(0); // A -> B
        assertEquals(usuarioA, comp1.getUsuario1());
        assertEquals(usuarioB, comp1.getUsuario2());
        assertEquals(100.0, comp1.getPorcentaje());
        assertTrue(comp1.getCriteriosEvaluados().contains("Misma carrera (+30%)."));
        assertTrue(comp1.getCriteriosEvaluados().contains("Horarios similares (+30%)."));
        assertTrue(comp1.getCriteriosEvaluados().contains("Hábitos compatibles (+20%)."));
        assertTrue(comp1.getCriteriosEvaluados().contains("Nivel de tolerancia similar (+20%)."));
    }

    @Test
    void calcularCompatibilidadesParaUsuario_AfinidadParcial() {
        // Solo coinciden en carrera (+30%) y tolerancia (+20%) -> 50%
        perfilA.setCarrera("Ingenieria");
        perfilA.setHorarioEstudio("Mañana");
        perfilA.setHabitosRuido("Silencio");
        perfilA.setHabitosSueno("Madrugador");
        perfilA.setNivelTolerancia("Alto");

        perfilB.setCarrera("Ingenieria");
        perfilB.setHorarioEstudio("Noche"); // No
        perfilB.setHabitosRuido("Ruido"); // No
        perfilB.setHabitosSueno("Noctambulo"); // No
        perfilB.setNivelTolerancia("Alto");

        List<Usuario> todosLosUsuarios = List.of(usuarioA, usuarioB);
        when(usuarioRepository.findAll()).thenReturn(todosLosUsuarios);
        when(compatibilidadRepository.findByUsuario1IdAndUsuario2Id(anyLong(), anyLong())).thenReturn(null);

        compatibilidadService.calcularCompatibilidadesParaUsuario(usuarioA);

        ArgumentCaptor<Compatibilidad> compatibilidadCaptor = ArgumentCaptor.forClass(Compatibilidad.class);
        verify(compatibilidadRepository, times(2)).save(compatibilidadCaptor.capture());

        Compatibilidad comp = compatibilidadCaptor.getAllValues().get(0);
        assertEquals(50.0, comp.getPorcentaje());
        assertTrue(comp.getCriteriosEvaluados().contains("Misma carrera (+30%)."));
        assertFalse(comp.getCriteriosEvaluados().contains("Horarios similares"));
        assertFalse(comp.getCriteriosEvaluados().contains("Hábitos compatibles"));
        assertTrue(comp.getCriteriosEvaluados().contains("Nivel de tolerancia similar (+20%)."));
    }

    @Test
    void calcularCompatibilidadesParaUsuario_OmiteUsuarioSinPerfilOUsuarioMismoId() {
        Usuario usuarioSinPerfil = new Usuario();
        usuarioSinPerfil.setId(3L);
        usuarioSinPerfil.setPerfilAcademico(null);

        List<Usuario> todosLosUsuarios = List.of(usuarioA, usuarioB, usuarioSinPerfil);
        when(usuarioRepository.findAll()).thenReturn(todosLosUsuarios);

        perfilA.setCarrera("Ingenieria");
        perfilB.setCarrera("Medicina");

        compatibilidadService.calcularCompatibilidadesParaUsuario(usuarioA);

        // Solo calcula bidireccional A <-> B (2 invocaciones). Salta usuarioSinPerfil y
        // a sí mismo.
        verify(compatibilidadRepository, times(2)).save(any(Compatibilidad.class));
    }

    @Test
    void calcularCompatibilidadesParaUsuario_ExistenteActualizaPorcentaje() {
        perfilA.setCarrera("Ingenieria");
        perfilB.setCarrera("Ingenieria");

        Compatibilidad compExistente = new Compatibilidad(usuarioA, usuarioB, 10.0, "Ninguno");
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioA, usuarioB));
        when(compatibilidadRepository.findByUsuario1IdAndUsuario2Id(1L, 2L)).thenReturn(compExistente);

        compatibilidadService.calcularCompatibilidadesParaUsuario(usuarioA);

        verify(compatibilidadRepository, times(2)).save(any(Compatibilidad.class));
        assertEquals(30.0, compExistente.getPorcentaje());
        assertEquals("Misma carrera (+30%).", compExistente.getCriteriosEvaluados());
    }

    @Test
    void obtenerCompatibilidades_Exito() {
        List<Compatibilidad> lista = List.of(new Compatibilidad());
        when(compatibilidadRepository.findByUsuario1Id(1L)).thenReturn(lista);

        List<Compatibilidad> resultado = compatibilidadService.obtenerCompatibilidades(1L);

        assertEquals(lista, resultado);
    }
}
