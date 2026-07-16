package com.scholarstay.app.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scholarstay.app.dto.ComentarioDTO;
import com.scholarstay.app.dto.GrupoDetalleDTO;
import com.scholarstay.app.model.Grupo;
import com.scholarstay.app.model.GrupoMiembro;
import com.scholarstay.app.model.PostComentario;
import com.scholarstay.app.model.PostGrupo;
import com.scholarstay.app.model.PostGuardado;
import com.scholarstay.app.model.PostLike;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import com.scholarstay.app.repository.GrupoMiembroRepository;
import com.scholarstay.app.repository.GrupoRepository;
import com.scholarstay.app.repository.PostComentarioRepository;
import com.scholarstay.app.repository.PostGrupoRepository;
import com.scholarstay.app.repository.PostGuardadoRepository;
import com.scholarstay.app.repository.PostLikeRepository;
import com.scholarstay.app.repository.UsuarioRepository;

@Service
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final GrupoMiembroRepository grupoMiembroRepository;
    private final PostGrupoRepository postGrupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostGuardadoRepository postGuardadoRepository;
    private final PostComentarioRepository postComentarioRepository;
    private final NotificacionService notificacionService;

    public GrupoService(GrupoRepository grupoRepository,
                        GrupoMiembroRepository grupoMiembroRepository,
                        PostGrupoRepository postGrupoRepository,
                        UsuarioRepository usuarioRepository,
                        PostLikeRepository postLikeRepository,
                        PostGuardadoRepository postGuardadoRepository,
                        PostComentarioRepository postComentarioRepository,
                        NotificacionService notificacionService) {
        this.grupoRepository = grupoRepository;
        this.grupoMiembroRepository = grupoMiembroRepository;
        this.postGrupoRepository = postGrupoRepository;
        this.usuarioRepository = usuarioRepository;
        this.postLikeRepository = postLikeRepository;
        this.postGuardadoRepository = postGuardadoRepository;
        this.postComentarioRepository = postComentarioRepository;
        this.notificacionService = notificacionService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DETALLE DEL GRUPO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Construye el DTO completo para renderizar la vista de detalle del grupo.
     * Calcula métricas reales, estado del usuario y posts según el ordenamiento.
     *
     * @param grupoId   ID del grupo a mostrar
     * @param usuarioId ID del usuario autenticado (para saber si es miembro)
     * @param orden     "recientes" o "populares"
     */
    public GrupoDetalleDTO obtenerDetalle(Long grupoId, Long usuarioId, String orden) {
        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No existe un grupo con ese ID."
            ));

        GrupoDetalleDTO dto = new GrupoDetalleDTO();
        dto.setGrupo(grupo);

        // ── Métricas reales ─────────────────────────────────────────────────
        long totalMiembros = grupoMiembroRepository.contarMiembros(grupoId);
        long totalSeguidores = grupoMiembroRepository.contarSeguidores(grupoId);
        dto.setTotalMiembros(totalMiembros);
        dto.setTotalSeguidores(totalSeguidores);

        // Posts publicados en las últimas 24 horas
        LocalDateTime hace24h = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
        long postsHoy = postGrupoRepository.contarPostsDesdeFecha(grupoId, hace24h);
        dto.setPostsHoy(postsHoy);

        // Última actividad: fecha del post más reciente
        List<PostGrupo> recientes = postGrupoRepository
            .findByGrupoIdOrderByFechaPublicacionDesc(grupoId);
        if (!recientes.isEmpty()) {
            dto.setUltimaActividad(
                calcularTiempoRelativo(recientes.get(0).getFechaPublicacion())
            );
        } else {
            dto.setUltimaActividad("Sin actividad aún");
        }

        // Nivel de vitalidad basado en posts de las últimas 24h
        dto.setNivelVitalidad(calcularNivelVitalidad(postsHoy));

        // ── Estado del usuario respecto al grupo ────────────────────────────
        if (usuarioId != null) {
            Optional<GrupoMiembro> membresia =
                grupoMiembroRepository.findByGrupoIdAndUsuarioId(grupoId, usuarioId);
            if (membresia.isPresent()) {
                String rol = membresia.get().getRol();
                dto.setEsMiembro("MIEMBRO".equals(rol) || "ADMIN".equals(rol));
                dto.setEsSeguidor("SEGUIDOR".equals(rol));
                dto.setEsAdmin("ADMIN".equals(rol));
            }
        }

        // ── Posts del muro según el ordenamiento ────────────────────────────
        if ("populares".equalsIgnoreCase(orden)) {
            dto.setPosts(postGrupoRepository
                .findByGrupoIdOrderByLikesDescFechaPublicacionDesc(grupoId));
        } else {
            dto.setPosts(recientes);
        }

        // ── Likes y guardados del usuario autenticado ───────────────────────
        if (usuarioId != null && dto.getPosts() != null && !dto.getPosts().isEmpty()) {
            List<Long> postIds = dto.getPosts().stream()
                .map(PostGrupo::getId)
                .collect(Collectors.toList());

            List<PostLike> likes = postLikeRepository
                .findByUsuarioIdAndPostIdIn(usuarioId, postIds);
            Set<Long> likedIds = likes.stream()
                .map(l -> l.getPost().getId())
                .collect(Collectors.toSet());
            dto.setPostIdsLiked(likedIds);

            List<PostGuardado> guardados = postGuardadoRepository
                .findByUsuarioIdAndPostIdIn(usuarioId, postIds);
            Set<Long> guardadoIds = guardados.stream()
                .map(g -> g.getPost().getId())
                .collect(Collectors.toSet());
            dto.setPostIdsGuardados(guardadoIds);
        }

        // ── Comentarios agrupados por post ──────────────────────────────────
        if (dto.getPosts() != null && !dto.getPosts().isEmpty()) {
            List<Long> postIds = dto.getPosts().stream()
                .map(PostGrupo::getId)
                .collect(Collectors.toList());

            List<PostComentario> todosComentarios = postComentarioRepository
                .findByPostIdIn(postIds);

            Map<Long, List<ComentarioDTO>> mapaComentarios = new HashMap<>();
            for (PostComentario c : todosComentarios) {
                ComentarioDTO dtoComentario = new ComentarioDTO(
                    c.getId(),
                    c.getAutor().getId(),
                    c.getAutor().getNombre(),
                    c.getAutor().getAvatar(),
                    c.getContenido(),
                    calcularTiempoRelativo(c.getFechaCreacion())
                );
                mapaComentarios
                    .computeIfAbsent(c.getPost().getId(), k -> new ArrayList<>())
                    .add(dtoComentario);
            }
            dto.setComentariosPorPost(mapaComentarios);
        }

        return dto;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UNIRSE AL CÍRCULO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Agrega al usuario como MIEMBRO del grupo.
     * Si ya es seguidor, lo promueve a MIEMBRO.
     * Actualiza el contador de miembros en el modelo Grupo.
     */
    public void unirseAlGrupo(Long grupoId, Long usuarioId) {
        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado."));

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Optional<GrupoMiembro> existente =
            grupoMiembroRepository.findByGrupoIdAndUsuarioId(grupoId, usuarioId);

        if (existente.isPresent()) {
            String rolActual = existente.get().getRol();
            if ("MIEMBRO".equals(rolActual) || "ADMIN".equals(rolActual)) {
                // Ya es miembro, no hacer nada
                return;
            }
            // Era seguidor: promover a MIEMBRO
            existente.get().setRol("MIEMBRO");
            grupoMiembroRepository.save(existente.get());
        } else {
            // Primera vez: crear membresía
            GrupoMiembro membresia = new GrupoMiembro(grupo, usuario, "MIEMBRO");
            grupoMiembroRepository.save(membresia);
        }

        // Actualizar el contador del grupo
        grupo.setMiembros((int) grupoMiembroRepository.contarMiembros(grupoId));
        grupoRepository.save(grupo);

        // Notificar al usuario que se unió
        notificacionService.crearNotificacion(
            usuario,
            "Te uniste al círculo de estudio \"" + grupo.getNombre() + "\". ¡Bienvenido!",
            NotificationType.GRUPO, NotificationPriority.SUCCESS, grupo.getId()
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEGUIR UN CÍRCULO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Agrega al usuario como SEGUIDOR del grupo.
     * Los seguidores reciben notificaciones pero no pueden publicar.
     * Si ya era MIEMBRO o ADMIN, no se hace downgrade.
     */
    public void seguirGrupo(Long grupoId, Long usuarioId) {
        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado."));

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        boolean yaExiste =
            grupoMiembroRepository.existsByGrupoIdAndUsuarioId(grupoId, usuarioId);

        if (yaExiste) {
            // Si ya es miembro o admin, no se hace downgrade a seguidor
            return;
        }

        GrupoMiembro membresia = new GrupoMiembro(grupo, usuario, "SEGUIDOR");
        grupoMiembroRepository.save(membresia);

        notificacionService.crearNotificacion(
            usuario,
            "Ahora sigues el círculo \"" + grupo.getNombre() + "\". Recibirás novedades de este grupo.",
            NotificationType.GRUPO, NotificationPriority.INFO, grupo.getId()
        );
    }

    /**
     * Deja de seguir un grupo. Elimina la membresía completamente.
     * Si era MIEMBRO, también actualiza el contador del grupo.
     */
    public void dejarGrupo(Long grupoId, Long usuarioId) {
        grupoMiembroRepository.findByGrupoIdAndUsuarioId(grupoId, usuarioId)
            .ifPresent(membresia -> {
                boolean eraMiembro = "MIEMBRO".equals(membresia.getRol())
                    || "ADMIN".equals(membresia.getRol());
                grupoMiembroRepository.delete(membresia);

                if (eraMiembro) {
                    grupoRepository.findById(grupoId).ifPresent(g -> {
                        g.setMiembros((int) grupoMiembroRepository.contarMiembros(grupoId));
                        grupoRepository.save(g);
                    });
                }
            });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MURO DE DEBATE — PUBLICAR POST
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Publica un nuevo post en el muro del grupo.
     * Solo pueden publicar usuarios con rol MIEMBRO o ADMIN en el grupo.
     * El contenido se sanitiza antes de guardarse (anti-XSS).
     */
    public PostGrupo publicarPost(Long grupoId, Long usuarioId, String contenido) {
        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException("El contenido del post no puede estar vacío.");
        }
        if (contenido.trim().length() < 5) {
            throw new IllegalArgumentException("El post debe tener al menos 5 caracteres.");
        }
        if (contenido.trim().length() > 2000) {
            throw new IllegalArgumentException("El post no puede superar los 2000 caracteres.");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado."));

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // Verificar que el usuario sea MIEMBRO o ADMIN para poder publicar
        GrupoMiembro membresia = grupoMiembroRepository
            .findByGrupoIdAndUsuarioId(grupoId, usuarioId)
            .orElseThrow(() -> new IllegalStateException(
                "Debes ser miembro del grupo para publicar."
            ));

        if ("SEGUIDOR".equals(membresia.getRol())) {
            throw new IllegalStateException(
                "Los seguidores no pueden publicar. Únete al grupo para participar."
            );
        }

        // Sanitización básica anti-XSS
        String contenidoSanitizado = contenido.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;");

        PostGrupo post = new PostGrupo(grupo, usuario, contenidoSanitizado);
        PostGrupo guardado = postGrupoRepository.save(post);

        // Notificar a todos los miembros del grupo sobre el nuevo post
        List<GrupoMiembro> miembros = grupoMiembroRepository.findByGrupoId(grupoId);
        for (GrupoMiembro m : miembros) {
            // No notificar al autor del post
            if (m.getUsuario().getId().equals(usuarioId)) continue;
            notificacionService.crearNotificacion(
                m.getUsuario(),
                usuario.getNombre() + " publicó en el círculo \"" + grupo.getNombre() + "\".",
                NotificationType.GRUPO, NotificationPriority.INFO, grupo.getId()
            );
        }

        return guardado;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EDITAR POST
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void editarPost(Long postId, Long usuarioId, String nuevoContenido) {
        if (nuevoContenido == null || nuevoContenido.isBlank()) {
            throw new IllegalArgumentException("El contenido del post no puede estar vacío.");
        }
        if (nuevoContenido.trim().length() < 5) {
            throw new IllegalArgumentException("El post debe tener al menos 5 caracteres.");
        }
        if (nuevoContenido.trim().length() > 2000) {
            throw new IllegalArgumentException("El post no puede superar los 2000 caracteres.");
        }

        PostGrupo post = postGrupoRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado."));

        verificarPermisoEdicion(post, usuarioId);

        String contenidoSanitizado = nuevoContenido.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;");

        post.setContenido(contenidoSanitizado);
        postGrupoRepository.save(post);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ELIMINAR POST (con limpieza en cascada)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void eliminarPost(Long postId, Long usuarioId) {
        PostGrupo post = postGrupoRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado."));

        verificarPermisoEdicion(post, usuarioId);

        // Eliminar en orden: comentarios → likes → guardados → post
        postComentarioRepository.deleteByPostId(postId);
        postLikeRepository.deleteByPostId(postId);
        postGuardadoRepository.deleteByPostId(postId);
        postGrupoRepository.delete(post);
    }

    /**
     * Verifica que el usuario sea el autor del post o ADMIN del grupo.
     */
    private void verificarPermisoEdicion(PostGrupo post, Long usuarioId) {
        boolean esAutor = post.getAutor().getId().equals(usuarioId);
        boolean esAdmin = grupoMiembroRepository
            .findByGrupoIdAndUsuarioId(post.getGrupo().getId(), usuarioId)
            .filter(m -> "ADMIN".equals(m.getRol()))
            .isPresent();

        if (!esAutor && !esAdmin) {
            throw new IllegalStateException(
                "No tienes permiso para modificar esta publicación."
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DAR LIKE / QUITAR LIKE (TOGGLE)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public boolean toggleLike(Long postId, Long usuarioId) {
        PostGrupo post = postGrupoRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado."));

        boolean liked = postLikeRepository.existsByPostIdAndUsuarioId(postId, usuarioId);
        if (liked) {
            postLikeRepository.deleteByPostIdAndUsuarioId(postId, usuarioId);
        } else {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
            postLikeRepository.save(new PostLike(post, usuario));
        }
        post.setLikes((int)postLikeRepository.countByPostId(postId));
        postGrupoRepository.save(post);
        return !liked;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GUARDAR / QUITAR GUARDADO (TOGGLE)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public boolean toggleGuardado(Long postId, Long usuarioId) {
        PostGrupo post = postGrupoRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado."));

        boolean guardado = postGuardadoRepository.existsByPostIdAndUsuarioId(postId, usuarioId);
        if (guardado) {
            postGuardadoRepository.deleteByPostIdAndUsuarioId(postId, usuarioId);
        } else {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
            postGuardadoRepository.save(new PostGuardado(post, usuario));
        }
        return !guardado;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COMENTARIOS EN POSTS
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public PostComentario crearComentario(Long postId, Long usuarioId, String contenido) {
        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }
        if (contenido.trim().length() < 2) {
            throw new IllegalArgumentException("El comentario debe tener al menos 2 caracteres.");
        }
        if (contenido.trim().length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede superar los 1000 caracteres.");
        }

        PostGrupo post = postGrupoRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado."));

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // Verificar que el usuario sea MIEMBRO o ADMIN del grupo
        GrupoMiembro membresia = grupoMiembroRepository
            .findByGrupoIdAndUsuarioId(post.getGrupo().getId(), usuarioId)
            .orElseThrow(() -> new IllegalStateException(
                "Debes ser miembro del grupo para comentar."
            ));

        if ("SEGUIDOR".equals(membresia.getRol())) {
            throw new IllegalStateException(
                "Los seguidores no pueden comentar. Únete al grupo para participar."
            );
        }

        String contenidoSanitizado = contenido.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;");

        PostComentario comentario = new PostComentario(post, usuario, contenidoSanitizado);
        PostComentario guardado = postComentarioRepository.save(comentario);

        // Actualizar contador de respuestas del post
        post.setRespuestas((int) postComentarioRepository.countByPostId(postId));
        postGrupoRepository.save(post);

        return guardado;
    }

    @Transactional
    public void eliminarComentario(Long comentarioId, Long usuarioId) {
        PostComentario comentario = postComentarioRepository.findById(comentarioId)
            .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado."));

        PostGrupo post = comentario.getPost();

        // Verificar que sea el autor del comentario o ADMIN del grupo
        boolean esAutor = comentario.getAutor().getId().equals(usuarioId);
        boolean esAdmin = grupoMiembroRepository
            .findByGrupoIdAndUsuarioId(post.getGrupo().getId(), usuarioId)
            .filter(m -> "ADMIN".equals(m.getRol()))
            .isPresent();

        if (!esAutor && !esAdmin) {
            throw new IllegalStateException(
                "No tienes permiso para eliminar este comentario."
            );
        }

        postComentarioRepository.delete(comentario);

        // Actualizar contador de respuestas del post
        post.setRespuestas((int) postComentarioRepository.countByPostId(post.getId()));
        postGrupoRepository.save(post);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INVITAR A UN USUARIO AL GRUPO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Envía una notificación de invitación a un usuario para unirse al grupo.
     * Solo los MIEMBRO y ADMIN pueden invitar.
     * La notificación incluye el link para aceptar la invitación.
     */
    public void invitarUsuario(Long grupoId, Long invitadorId, Long invitadoId) {
        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado."));

        // Verificar que el invitador sea miembro del grupo
        GrupoMiembro membresia = grupoMiembroRepository
            .findByGrupoIdAndUsuarioId(grupoId, invitadorId)
            .orElseThrow(() -> new IllegalStateException(
                "Solo los miembros del grupo pueden invitar a otros usuarios."
            ));

        if ("SEGUIDOR".equals(membresia.getRol())) {
            throw new IllegalStateException(
                "Los seguidores no pueden enviar invitaciones."
            );
        }

        Usuario invitado = usuarioRepository.findById(invitadoId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario invitado no encontrado."));

        // No invitar si ya es miembro o seguidor
        if (grupoMiembroRepository.existsByGrupoIdAndUsuarioId(grupoId, invitadoId)) {
            throw new IllegalStateException(
                "Este usuario ya forma parte del grupo."
            );
        }

        Usuario invitador = usuarioRepository.findById(invitadorId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario invitador no encontrado."));

        // Enviar notificación al invitado con el link para aceptar
        notificacionService.crearNotificacion(
            invitado,
            invitador.getNombre() + " te invitó a unirte al círculo \""
                + grupo.getNombre() + "\". "
                + "Haz clic aquí para unirte: /comunidad/grupos/" + grupoId + "/unirse",
            NotificationType.INVITACION_GRUPO, NotificationPriority.INFO, grupo.getId()
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BUSCAR USUARIOS PARA INVITAR
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Busca usuarios por nombre o email, excluyendo los que ya son miembros del grupo.
     * Retorna una lista de mapas con id, nombre, email y avatar.
     */
    public List<Map<String, Object>> buscarUsuarios(String query, Long grupoId) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        List<Usuario> encontrados = usuarioRepository
            .findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(query.trim(), query.trim());

        // IDs de miembros actuales del grupo para excluirlos
        List<GrupoMiembro> miembros = grupoMiembroRepository.findByGrupoId(grupoId);
        Set<Long> idsMiembros = miembros.stream()
            .map(m -> m.getUsuario().getId())
            .collect(Collectors.toSet());

        return encontrados.stream()
            .filter(u -> !idsMiembros.contains(u.getId()))
            .limit(10)
            .map(u -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", u.getId());
                item.put("nombre", u.getNombre());
                item.put("email", u.getEmail());
                item.put("avatar", u.getAvatar());
                return item;
            })
            .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Convierte una fecha a texto relativo legible.
     * Ej: "Hace 5 min", "Hace 2 horas", "Hace 3 días"
     */
    private String calcularTiempoRelativo(LocalDateTime fecha) {
        long minutos = ChronoUnit.MINUTES.between(fecha, LocalDateTime.now());
        if (minutos < 1) return "Hace un momento";
        if (minutos < 60) return "Hace " + minutos + " min";
        long horas = ChronoUnit.HOURS.between(fecha, LocalDateTime.now());
        if (horas < 24) return "Hace " + horas + " hora" + (horas > 1 ? "s" : "");
        long dias = ChronoUnit.DAYS.between(fecha, LocalDateTime.now());
        if (dias < 7) return "Hace " + dias + " día" + (dias > 1 ? "s" : "");
        return "Hace más de una semana";
    }

    /**
     * Calcula el nivel de vitalidad según los posts de las últimas 24h.
     * Alto: 10+ posts | Medio: 3-9 posts | Bajo: 0-2 posts
     */
    private String calcularNivelVitalidad(long postsHoy) {
        if (postsHoy >= 10) return "Alto";
        if (postsHoy >= 3)  return "Medio";
        return "Bajo";
    }
}
