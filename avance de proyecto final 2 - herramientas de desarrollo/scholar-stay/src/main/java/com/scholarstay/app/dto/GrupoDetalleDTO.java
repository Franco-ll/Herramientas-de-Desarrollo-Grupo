package com.scholarstay.app.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scholarstay.app.model.Grupo;
import com.scholarstay.app.model.PostGrupo;

/**
 * DTO que agrupa toda la información necesaria para renderizar
 * la vista de detalle de un grupo (grupo_comunidad.html).
 * Incluye el grupo, sus métricas reales, los posts y el estado del usuario.
 */
public class GrupoDetalleDTO {

    private Grupo grupo;

    // Métricas reales calculadas desde la BD
    private long totalMiembros;
    private long totalSeguidores;
    private long postsHoy;
    private String ultimaActividad;  // Ej: "Hace 5 min", "Hace 2 horas"
    private String nivelVitalidad;   // "Alto", "Medio", "Bajo" calculado con postsHoy

    // Estado del usuario autenticado respecto al grupo
    private boolean esMiembro;
    private boolean esSeguidor;
    private boolean esAdmin;

    // Posts del muro (recientes o populares según el tab)
    private List<PostGrupo> posts;

    // IDs de posts que el usuario actual ha dado like
    private Set<Long> postIdsLiked = java.util.Collections.emptySet();

    // IDs de posts que el usuario actual ha guardado
    private Set<Long> postIdsGuardados = java.util.Collections.emptySet();

    // Comentarios agrupados por postId
    private Map<Long, List<ComentarioDTO>> comentariosPorPost = java.util.Collections.emptyMap();

    public GrupoDetalleDTO() {}


    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }

    public long getTotalMiembros() { return totalMiembros; }
    public void setTotalMiembros(long totalMiembros) { this.totalMiembros = totalMiembros; }

    public long getTotalSeguidores() { return totalSeguidores; }
    public void setTotalSeguidores(long totalSeguidores) { this.totalSeguidores = totalSeguidores; }

    public long getPostsHoy() { return postsHoy; }
    public void setPostsHoy(long postsHoy) { this.postsHoy = postsHoy; }

    public String getUltimaActividad() { return ultimaActividad; }
    public void setUltimaActividad(String ultimaActividad) { this.ultimaActividad = ultimaActividad; }

    public String getNivelVitalidad() { return nivelVitalidad; }
    public void setNivelVitalidad(String nivelVitalidad) { this.nivelVitalidad = nivelVitalidad; }

    public boolean isEsMiembro() { return esMiembro; }
    public void setEsMiembro(boolean esMiembro) { this.esMiembro = esMiembro; }

    public boolean isEsSeguidor() { return esSeguidor; }
    public void setEsSeguidor(boolean esSeguidor) { this.esSeguidor = esSeguidor; }

    public boolean isEsAdmin() { return esAdmin; }
    public void setEsAdmin(boolean esAdmin) { this.esAdmin = esAdmin; }

    public List<PostGrupo> getPosts() { return posts; }
    public void setPosts(List<PostGrupo> posts) { this.posts = posts; }

    public Set<Long> getPostIdsLiked() { return postIdsLiked; }
    public void setPostIdsLiked(Set<Long> postIdsLiked) { this.postIdsLiked = postIdsLiked; }

    public Set<Long> getPostIdsGuardados() { return postIdsGuardados; }
    public void setPostIdsGuardados(Set<Long> postIdsGuardados) { this.postIdsGuardados = postIdsGuardados; }

    public Map<Long, List<ComentarioDTO>> getComentariosPorPost() { return comentariosPorPost; }
    public void setComentariosPorPost(Map<Long, List<ComentarioDTO>> comentariosPorPost) { this.comentariosPorPost = comentariosPorPost; }
}
