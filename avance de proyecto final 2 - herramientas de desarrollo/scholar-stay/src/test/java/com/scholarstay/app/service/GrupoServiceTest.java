package com.scholarstay.app.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scholarstay.app.model.Grupo;
import com.scholarstay.app.model.GrupoMiembro;
import com.scholarstay.app.model.PostComentario;
import com.scholarstay.app.model.PostGrupo;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.GrupoMiembroRepository;
import com.scholarstay.app.repository.GrupoRepository;
import com.scholarstay.app.repository.PostComentarioRepository;
import com.scholarstay.app.repository.PostGrupoRepository;
import com.scholarstay.app.repository.PostGuardadoRepository;
import com.scholarstay.app.repository.PostLikeRepository;
import com.scholarstay.app.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class GrupoServiceTest {

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private GrupoMiembroRepository grupoMiembroRepository;

    @Mock
    private PostGrupoRepository postGrupoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostGuardadoRepository postGuardadoRepository;

    @Mock
    private PostComentarioRepository postComentarioRepository;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private GrupoService grupoService;

    private Usuario autor;
    private Usuario otroUsuario;
    private Usuario adminGrupo;
    private Grupo grupo;
    private PostGrupo post;
    private GrupoMiembro membresiaAutor;
    private GrupoMiembro membresiaAdmin;
    private GrupoMiembro membresiaSeguidor;

    @BeforeEach
    void setUp() {
        autor = new Usuario();
        autor.setId(1L);
        autor.setNombre("Autor Uno");

        otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        otroUsuario.setNombre("Otro Usuario");

        adminGrupo = new Usuario();
        adminGrupo.setId(3L);
        adminGrupo.setNombre("Admin Grupo");

        grupo = new Grupo();
        grupo.setId(100L);
        grupo.setNombre("Círculo de Prueba");

        post = new PostGrupo();
        post.setId(10L);
        post.setGrupo(grupo);
        post.setAutor(autor);
        post.setContenido("Contenido del post de prueba");
        post.setRespuestas(0);
        post.setLikes(0);

        membresiaAutor = new GrupoMiembro(grupo, autor, "MIEMBRO");
        membresiaAdmin = new GrupoMiembro(grupo, adminGrupo, "ADMIN");
        membresiaSeguidor = new GrupoMiembro(grupo, otroUsuario, "SEGUIDOR");
    }

    // ─────────────────────────────────────────────────────────────────────
    // PRUEBAS: crearComentario
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void crearComentario_Exito() {
        when(postGrupoRepository.findById(10L)).thenReturn(Optional.of(post));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(grupoMiembroRepository.findByGrupoIdAndUsuarioId(100L, 1L))
            .thenReturn(Optional.of(membresiaAutor));
        when(postComentarioRepository.save(any(PostComentario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(postComentarioRepository.countByPostId(10L)).thenReturn(1L);

        PostComentario resultado = grupoService.crearComentario(10L, 1L, "Excelente post!");

        assertNotNull(resultado);
        assertEquals("Excelente post!", resultado.getContenido());
        assertEquals(autor, resultado.getAutor());
        assertEquals(post, resultado.getPost());
        verify(postComentarioRepository, times(1)).save(any(PostComentario.class));
        verify(postGrupoRepository, times(1)).save(post);
    }

    @Test
    void crearComentario_ContenidoVacio_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            grupoService.crearComentario(10L, 1L, "");
        });
        assertEquals("El comentario no puede estar vacío.", ex.getMessage());
        verify(postComentarioRepository, never()).save(any(PostComentario.class));
    }

    @Test
    void crearComentario_ContenidoNulo_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            grupoService.crearComentario(10L, 1L, null);
        });
        assertEquals("El comentario no puede estar vacío.", ex.getMessage());
        verify(postComentarioRepository, never()).save(any(PostComentario.class));
    }

    @Test
    void crearComentario_ContenidoMuyCorto_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            grupoService.crearComentario(10L, 1L, "a");
        });
        assertEquals("El comentario debe tener al menos 2 caracteres.", ex.getMessage());
        verify(postComentarioRepository, never()).save(any(PostComentario.class));
    }

    @Test
    void crearComentario_ContenidoMuyLargo_ThrowsIllegalArgumentException() {
        String largo = "a".repeat(1001);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            grupoService.crearComentario(10L, 1L, largo);
        });
        assertEquals("El comentario no puede superar los 1000 caracteres.", ex.getMessage());
        verify(postComentarioRepository, never()).save(any(PostComentario.class));
    }

    @Test
    void crearComentario_PostNoExiste_ThrowsIllegalArgumentException() {
        when(postGrupoRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            grupoService.crearComentario(999L, 1L, "Buen post!");
        });
        assertEquals("Post no encontrado.", ex.getMessage());
        verify(postComentarioRepository, never()).save(any(PostComentario.class));
    }

    @Test
    void crearComentario_UsuarioNoEsMiembro_ThrowsIllegalStateException() {
        when(postGrupoRepository.findById(10L)).thenReturn(Optional.of(post));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(otroUsuario));
        when(grupoMiembroRepository.findByGrupoIdAndUsuarioId(100L, 2L))
            .thenReturn(Optional.of(membresiaSeguidor));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            grupoService.crearComentario(10L, 2L, "Buen post!");
        });
        assertEquals("Los seguidores no pueden comentar. Únete al grupo para participar.", ex.getMessage());
        verify(postComentarioRepository, never()).save(any(PostComentario.class));
    }

    @Test
    void crearComentario_UsuarioNoEnGrupo_ThrowsIllegalStateException() {
        when(postGrupoRepository.findById(10L)).thenReturn(Optional.of(post));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(otroUsuario));
        when(grupoMiembroRepository.findByGrupoIdAndUsuarioId(100L, 2L))
            .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            grupoService.crearComentario(10L, 2L, "Buen post!");
        });
        assertEquals("Debes ser miembro del grupo para comentar.", ex.getMessage());
        verify(postComentarioRepository, never()).save(any(PostComentario.class));
    }

    @Test
    void crearComentario_SanitizacionXSS() {
        when(postGrupoRepository.findById(10L)).thenReturn(Optional.of(post));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(grupoMiembroRepository.findByGrupoIdAndUsuarioId(100L, 1L))
            .thenReturn(Optional.of(membresiaAutor));
        when(postComentarioRepository.save(any(PostComentario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(postComentarioRepository.countByPostId(10L)).thenReturn(1L);

        PostComentario resultado = grupoService.crearComentario(10L, 1L, "<script>alert('xss')</script>");

        assertNotNull(resultado);
        assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt;", resultado.getContenido());
        verify(postComentarioRepository, times(1)).save(any(PostComentario.class));
    }

    // ─────────────────────────────────────────────────────────────────────
    // PRUEBAS: eliminarComentario
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void eliminarComentario_ComoAutor_Exito() {
        PostComentario comentario = new PostComentario(post, autor, "Un comentario");
        comentario.setId(20L);

        when(postComentarioRepository.findById(20L)).thenReturn(Optional.of(comentario));
        when(postComentarioRepository.countByPostId(10L)).thenReturn(0L);

        grupoService.eliminarComentario(20L, 1L);

        verify(postComentarioRepository, times(1)).delete(comentario);
        verify(postGrupoRepository, times(1)).save(post);
    }

    @Test
    void eliminarComentario_ComoAdmin_Exito() {
        PostComentario comentario = new PostComentario(post, autor, "Un comentario");
        comentario.setId(20L);

        when(postComentarioRepository.findById(20L)).thenReturn(Optional.of(comentario));
        when(grupoMiembroRepository.findByGrupoIdAndUsuarioId(100L, 3L))
            .thenReturn(Optional.of(membresiaAdmin));
        when(postComentarioRepository.countByPostId(10L)).thenReturn(0L);

        grupoService.eliminarComentario(20L, 3L);

        verify(postComentarioRepository, times(1)).delete(comentario);
        verify(postGrupoRepository, times(1)).save(post);
    }

    @Test
    void eliminarComentario_NoAutorNiAdmin_ThrowsIllegalStateException() {
        PostComentario comentario = new PostComentario(post, autor, "Un comentario");
        comentario.setId(20L);

        when(postComentarioRepository.findById(20L)).thenReturn(Optional.of(comentario));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            grupoService.eliminarComentario(20L, 2L);
        });
        assertEquals("No tienes permiso para eliminar este comentario.", ex.getMessage());
        verify(postComentarioRepository, never()).delete(any(PostComentario.class));
    }

    @Test
    void eliminarComentario_NoExiste_ThrowsIllegalArgumentException() {
        when(postComentarioRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            grupoService.eliminarComentario(999L, 1L);
        });
        assertEquals("Comentario no encontrado.", ex.getMessage());
        verify(postComentarioRepository, never()).delete(any(PostComentario.class));
    }
}
