package com.scholarstay.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scholarstay.app.dto.PerfilAcademicoDTO;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.PerfilAcademicoRepository;
import com.scholarstay.app.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class PerfilAcademicoServiceTest {

    @Mock
    private PerfilAcademicoRepository perfilAcademicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CompatibilidadService compatibilidadService;

    @InjectMocks
    private PerfilAcademicoService perfilAcademicoService;

    private Usuario usuario;
    private PerfilAcademicoDTO dto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Carlos V");
        usuario.setEmail("carlos.v@universidad.edu");

        dto = new PerfilAcademicoDTO();
        dto.setNombre("Carlos Modificado");
        dto.setEmail("carlos.m@universidad.edu");
        dto.setCarrera("Ingenieria de Software");
        dto.setHorarioEstudio("Tarde");
        dto.setHabitosRuido("Música de fondo");
        dto.setHabitosSueno("Noctámbulo");
        dto.setNivelTolerancia("Medio");
        dto.setBiografia("Estudiante entusiasta");
    }

    @Test
    void actualizarPerfil_UsuarioNoEncontrado_ThrowsRuntimeException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            perfilAcademicoService.actualizarPerfil(1L, dto);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(perfilAcademicoRepository, never()).save(any(PerfilAcademico.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(compatibilidadService, never()).calcularCompatibilidadesParaUsuario(any(Usuario.class));
    }

    @Test
    void actualizarPerfil_CreaNuevoPerfil_Exito() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilAcademicoRepository.findByUsuarioId(1L)).thenReturn(null);
        when(perfilAcademicoRepository.save(any(PerfilAcademico.class))).thenAnswer(i -> i.getArgument(0));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        PerfilAcademico resultado = perfilAcademicoService.actualizarPerfil(1L, dto);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals("Ingenieria de Software", resultado.getCarrera());
        assertEquals("Tarde", resultado.getHorarioEstudio());
        assertEquals("Música de fondo", resultado.getHabitosRuido());
        assertEquals("Noctámbulo", resultado.getHabitosSueno());
        assertEquals("Medio", resultado.getNivelTolerancia());
        assertEquals("Estudiante entusiasta", resultado.getBiografia());

        // Verificar mapeo sobre el usuario
        assertEquals("Carlos Modificado", usuario.getNombre());
        assertEquals("carlos.m@universidad.edu", usuario.getEmail());
        assertEquals(resultado, usuario.getPerfilAcademico());

        verify(perfilAcademicoRepository, times(1)).save(any(PerfilAcademico.class));
        verify(usuarioRepository, times(1)).save(usuario);
        verify(compatibilidadService, times(1)).calcularCompatibilidadesParaUsuario(usuario);
    }

    @Test
    void actualizarPerfil_ActualizaPerfilExistente_Exito() {
        PerfilAcademico perfilExistente = new PerfilAcademico();
        perfilExistente.setId(100L);
        perfilExistente.setUsuario(usuario);
        perfilExistente.setCarrera("Medicina");
        usuario.setPerfilAcademico(perfilExistente);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilAcademicoRepository.findByUsuarioId(1L)).thenReturn(perfilExistente);
        when(perfilAcademicoRepository.save(any(PerfilAcademico.class))).thenAnswer(i -> i.getArgument(0));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        PerfilAcademico resultado = perfilAcademicoService.actualizarPerfil(1L, dto);

        assertNotNull(resultado);
        assertEquals(perfilExistente, resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Ingenieria de Software", resultado.getCarrera()); // Campo actualizado

        verify(perfilAcademicoRepository, times(1)).save(perfilExistente);
        verify(usuarioRepository, times(1)).save(usuario);
        verify(compatibilidadService, times(1)).calcularCompatibilidadesParaUsuario(usuario);
    }

    @Test
    void obtenerPorUsuario_Exito() {
        PerfilAcademico perfil = new PerfilAcademico();
        when(perfilAcademicoRepository.findByUsuarioId(1L)).thenReturn(perfil);

        PerfilAcademico resultado = perfilAcademicoService.obtenerPorUsuario(1L);

        assertEquals(perfil, resultado);
    }
}
