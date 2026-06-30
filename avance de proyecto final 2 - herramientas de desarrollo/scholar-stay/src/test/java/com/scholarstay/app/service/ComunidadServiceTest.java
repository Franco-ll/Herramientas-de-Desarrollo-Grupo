package com.scholarstay.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scholarstay.app.dto.MatchDTO;
import com.scholarstay.app.dto.PerfilDTO;
import com.scholarstay.app.model.Grupo;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.GrupoRepository;
import com.scholarstay.app.repository.PerfilAcademicoRepository;

@ExtendWith(MockitoExtension.class)
class ComunidadServiceTest {

    @Mock
    private PerfilAcademicoRepository perfilAcademicoRepository;

    @Mock
    private GrupoRepository grupoRepository;

    private ComunidadService comunidadService;

    private Usuario usuarioA;
    private Usuario usuarioB;
    private PerfilAcademico perfilA;
    private PerfilAcademico perfilB;

    @BeforeEach
    void setUp() {
        // Evitamos que el constructor intente poblar la BD mockeando el count a > 0
        lenient().when(grupoRepository.count()).thenReturn(3L);
        comunidadService = new ComunidadService(perfilAcademicoRepository, grupoRepository);

        usuarioA = new Usuario();
        usuarioA.setId(1L);
        usuarioA.setNombre("Juan");

        perfilA = new PerfilAcademico();
        perfilA.setUsuario(usuarioA);
        usuarioA.setPerfilAcademico(perfilA);

        usuarioB = new Usuario();
        usuarioB.setId(2L);
        usuarioB.setNombre("Maria");

        perfilB = new PerfilAcademico();
        perfilB.setUsuario(usuarioB);
        usuarioB.setPerfilAcademico(perfilB);
    }

    @Test
    void getAllCarreras_FiltraCorrectamente() {
        PerfilAcademico p1 = new PerfilAcademico();
        p1.setCarrera(" Computacion ");
        PerfilAcademico p2 = new PerfilAcademico();
        p2.setCarrera("");
        PerfilAcademico p3 = new PerfilAcademico();
        p3.setCarrera(null);
        PerfilAcademico p4 = new PerfilAcademico();
        p4.setCarrera("Derecho");
        PerfilAcademico p5 = new PerfilAcademico();
        p5.setCarrera("Computacion");

        when(perfilAcademicoRepository.findAll()).thenReturn(List.of(p1, p2, p3, p4, p5));

        List<String> carreras = comunidadService.getAllCarreras();

        assertEquals(2, carreras.size());
        assertTrue(carreras.contains("Computacion"));
        assertTrue(carreras.contains("Derecho"));
    }

    @Test
    void getAllIntereses_FiltraYOrdena() {
        PerfilAcademico p1 = new PerfilAcademico();
        p1.setIntereses("musica, deporte");
        PerfilAcademico p2 = new PerfilAcademico();
        p2.setIntereses(null);
        PerfilAcademico p3 = new PerfilAcademico();
        p3.setIntereses("  deporte, lectura  ");

        when(perfilAcademicoRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        List<String> intereses = comunidadService.getAllIntereses();

        assertEquals(3, intereses.size());
        assertEquals("deporte", intereses.get(0));
        assertEquals("lectura", intereses.get(1));
        assertEquals("musica", intereses.get(2));
    }

    @Test
    void findMatches_ConUsuarioAutenticado_CalculaScoreCorrectamente() {
        // Configurar afinidad parcial
        perfilA.setCarrera("Computacion");
        perfilA.setIntereses("ia, programacion");
        perfilA.setHorarioEstudio("Mañana");

        perfilB.setCarrera("Computacion"); // +35 carrera
        perfilB.setIntereses("ia, lectura"); // +8 un interes compartido
        perfilB.setHorarioEstudio("Mañana"); // +10 horario
        // Total esperado: 20 base + 35 + 8 + 10 = 73

        when(perfilAcademicoRepository.findByUsuarioId(1L)).thenReturn(perfilA);
        when(perfilAcademicoRepository.findAll()).thenReturn(List.of(perfilA, perfilB));

        List<MatchDTO> matches = comunidadService.findMatches(1L, null, null);

        // Retorna a perfilA y perfilB
        assertEquals(2, matches.size());
        MatchDTO matchB = matches.stream().filter(m -> m.getUsuarioId().equals(2L)).findFirst().orElse(null);
        assertNotNull(matchB);
        assertEquals(78.0, matchB.getPorcentaje());
    }

    @Test
    void findMatches_SinUsuarioAutenticado_CalculaScoreDefault() {
        perfilB.setCarrera("Computacion"); // +25
        perfilB.setIntereses("ia"); // +20
        perfilB.setHorarioEstudio("Mañana"); // +10
        // Total esperado: 20 base + 25 + 20 + 10 = 75

        when(perfilAcademicoRepository.findAll()).thenReturn(List.of(perfilB));

        List<MatchDTO> matches = comunidadService.findMatches(null, null, null);

        assertEquals(1, matches.size());
        assertEquals(75.0, matches.get(0).getPorcentaje());
    }

    @Test
    void findMatches_FiltrosDeBusqueda() {
        perfilA.setCarrera("Computacion");
        perfilB.setCarrera("Derecho");

        PerfilAcademico perfilC = new PerfilAcademico();
        Usuario usuarioC = new Usuario();
        usuarioC.setId(3L);
        usuarioC.setNombre("Carlos");
        perfilC.setUsuario(usuarioC);
        perfilC.setCarrera("Computacion");
        perfilC.setIntereses("programacion, bases");

        when(perfilAcademicoRepository.findByUsuarioId(1L)).thenReturn(perfilA);
        when(perfilAcademicoRepository.findAll()).thenReturn(List.of(perfilA, perfilB, perfilC));

        // Filtrar por carrera "computacion" e interes "bases"
        List<MatchDTO> matches = comunidadService.findMatches(1L, "computacion", "bases");

        assertEquals(1, matches.size());
        assertEquals(3L, matches.get(0).getUsuarioId());
    }

    @Test
    void findGrupos_FiltrosCombinadosSinDuplicados() {
        Grupo g1 = new Grupo("Grupo A", "Desc", "Computacion", "ia", 10);
        Grupo g2 = new Grupo("Grupo B", "Desc", "Derecho", "leyes", 5);

        // Sin filtros retorna todos
        when(grupoRepository.findAll()).thenReturn(List.of(g1, g2));
        List<Grupo> todos = comunidadService.findGrupos(null, "");
        assertEquals(2, todos.size());

        // Con filtros
        when(grupoRepository.findByCarreraContainingIgnoreCase("Computacion")).thenReturn(List.of(g1));
        when(grupoRepository.findByInteresesContainingIgnoreCase("leyes")).thenReturn(List.of(g2));

        List<Grupo> filtrados = comunidadService.findGrupos("Computacion", "leyes");
        assertEquals(2, filtrados.size());
        assertTrue(filtrados.contains(g1));
        assertTrue(filtrados.contains(g2));
    }

    @Test
    void getPerfilByUsuarioId_Exito() {
        perfilA.setCarrera("Computacion");
        perfilA.setUniversidad("UNMSM");
        perfilA.setBiografia("Hola");
        perfilA.setIntereses("deportes");
        perfilA.setHabitosRuido("Silencio");

        when(perfilAcademicoRepository.findByUsuarioId(1L)).thenReturn(perfilA);

        PerfilDTO dto = comunidadService.getPerfilByUsuarioId(1L);

        assertNotNull(dto);
        assertEquals("Juan", dto.getNombre());
        assertEquals("Computacion", dto.getCarrera());
        assertEquals("UNMSM", dto.getUniversidad());
        assertTrue(dto.getEstiloVida().contains("Prefiere espacios silenciosos"));
    }

    @Test
    void getPerfilByUsuarioId_Inexistente_ReturnsNull() {
        when(perfilAcademicoRepository.findByUsuarioId(999L)).thenReturn(null);

        PerfilDTO dto = comunidadService.getPerfilByUsuarioId(999L);

        assertNull(dto);
    }

    @Test
    void calcularCompatibilidad_Exito() {
        perfilA.setCarrera("Computacion");
        perfilB.setCarrera("Computacion");

        when(perfilAcademicoRepository.findByUsuarioId(1L)).thenReturn(perfilA);
        when(perfilAcademicoRepository.findByUsuarioId(2L)).thenReturn(perfilB);

        Double score = comunidadService.calcularCompatibilidad(1L, 2L);

        // 20 base + 30 carrera = 50.0
        assertEquals(50.0, score);
    }
}
