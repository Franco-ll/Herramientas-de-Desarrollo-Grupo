package com.scholarstay.app.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scholarstay.app.dto.GrupoDetalleDTO;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.security.CustomUserDetails;
import com.scholarstay.app.service.GrupoService;

@Controller
@RequestMapping("/comunidad/grupos")
public class GrupoController {

    private final GrupoService grupoService;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DETALLE DE UN GRUPO — GET /comunidad/grupos/{id}
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Muestra la vista de detalle de un grupo con todas sus métricas,
     * posts del muro y el estado del usuario autenticado.
     *
     * @param id    ID del grupo
     * @param orden "recientes" (default) o "populares"
     */
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id,
                          @RequestParam(defaultValue = "recientes") String orden,
                          Model model) {
        Long usuarioId = getUsuarioId();

        GrupoDetalleDTO detalle = grupoService.obtenerDetalle(id, usuarioId, orden);

        model.addAttribute("detalle", detalle);
        model.addAttribute("orden", orden);
        model.addAttribute("usuarioId", usuarioId);

        // Pasar avatar y nombre del usuario autenticado para el formulario de publicación
        Usuario usuario = getUsuario();
        if (usuario != null) {
            model.addAttribute("usuarioAvatar", usuario.getAvatar());
            model.addAttribute("usuarioNombre", usuario.getNombre());
        }

        return "comunidad/grupo_comunidad";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UNIRSE AL GRUPO — POST /comunidad/grupos/{id}/unirse
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/unirse")
    public String unirse(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.unirseAlGrupo(id, usuarioId);
            redirectAttrs.addFlashAttribute("mensaje", "¡Te uniste al círculo exitosamente!");
        } catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("mensaje", "Ya eres miembro de este círculo.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo completar la acción: " + e.getMessage());
        }

        return "redirect:/comunidad/grupos/" + id;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEGUIR UN GRUPO — POST /comunidad/grupos/{id}/seguir
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/seguir")
    public String seguir(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.seguirGrupo(id, usuarioId);
            redirectAttrs.addFlashAttribute("mensaje", "Ahora sigues este círculo.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo completar la acción: " + e.getMessage());
        }

        return "redirect:/comunidad/grupos/" + id;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SALIR DEL GRUPO — POST /comunidad/grupos/{id}/salir
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/salir")
    public String salir(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.dejarGrupo(id, usuarioId);
            redirectAttrs.addFlashAttribute("mensaje", "Saliste del círculo.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo completar la acción: " + e.getMessage());
        }

        return "redirect:/comunidad/grupos/" + id;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLICAR EN EL MURO — POST /comunidad/grupos/{id}/publicar
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/publicar")
    public String publicar(@PathVariable Long id,
                           @RequestParam String contenido,
                           RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.publicarPost(id, usuarioId, contenido);
            redirectAttrs.addFlashAttribute("mensaje", "¡Publicación enviada!");
        } catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo publicar. Intenta de nuevo.");
        }

        return "redirect:/comunidad/grupos/" + id;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EDITAR POST — POST /comunidad/grupos/post/{postId}/editar
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/post/{postId}/editar")
    public String editarPost(@PathVariable Long postId,
                             @RequestParam String contenido,
                             @RequestParam Long grupoId,
                             RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.editarPost(postId, usuarioId, contenido);
            redirectAttrs.addFlashAttribute("mensaje", "Publicación actualizada.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo editar la publicación.");
        }

        return "redirect:/comunidad/grupos/" + grupoId;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ELIMINAR POST — POST /comunidad/grupos/post/{postId}/eliminar
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/post/{postId}/eliminar")
    public String eliminarPost(@PathVariable Long postId,
                               @RequestParam Long grupoId,
                               RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.eliminarPost(postId, usuarioId);
            redirectAttrs.addFlashAttribute("mensaje", "Publicación eliminada.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo eliminar la publicación.");
        }

        return "redirect:/comunidad/grupos/" + grupoId;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DAR LIKE / QUITAR LIKE (TOGGLE) — POST /comunidad/grupos/post/{postId}/like
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/post/{postId}/like")
    public String toggleLike(@PathVariable Long postId,
                             @RequestParam Long grupoId,
                             RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";
        try {
            boolean liked = grupoService.toggleLike(postId, usuarioId);
            redirectAttrs.addFlashAttribute("mensaje", liked ? "Like agregado" : "Like eliminado");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo procesar el like.");
        }
        return "redirect:/comunidad/grupos/" + grupoId;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GUARDAR / QUITAR GUARDADO (TOGGLE) — POST /comunidad/grupos/post/{postId}/guardar
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/post/{postId}/guardar")
    public String toggleGuardado(@PathVariable Long postId,
                                 @RequestParam Long grupoId,
                                 RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";
        try {
            boolean guardado = grupoService.toggleGuardado(postId, usuarioId);
            redirectAttrs.addFlashAttribute("mensaje", guardado ? "Publicación guardada" : "Publicación eliminada de guardados");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo procesar la acción.");
        }
        return "redirect:/comunidad/grupos/" + grupoId;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COMENTAR EN UN POST — POST /comunidad/grupos/post/{postId}/comentar
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/post/{postId}/comentar")
    public String comentar(@PathVariable Long postId,
                           @RequestParam String contenido,
                           @RequestParam Long grupoId,
                           RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.crearComentario(postId, usuarioId, contenido);
            redirectAttrs.addFlashAttribute("mensaje", "Comentario agregado.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo agregar el comentario.");
        }

        return "redirect:/comunidad/grupos/" + grupoId;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ELIMINAR COMENTARIO — POST /comunidad/grupos/comentario/{comentarioId}/eliminar
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/comentario/{comentarioId}/eliminar")
    public String eliminarComentario(@PathVariable Long comentarioId,
                                     @RequestParam Long grupoId,
                                     RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.eliminarComentario(comentarioId, usuarioId);
            redirectAttrs.addFlashAttribute("mensaje", "Comentario eliminado.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo eliminar el comentario.");
        }

        return "redirect:/comunidad/grupos/" + grupoId;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INVITAR USUARIO — POST /comunidad/grupos/{id}/invitar
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/invitar")
    public String invitar(@PathVariable Long id,
                          @RequestParam Long invitadoId,
                          RedirectAttributes redirectAttrs) {
        Long usuarioId = getUsuarioId();
        if (usuarioId == null) return "redirect:/login";

        try {
            grupoService.invitarUsuario(id, usuarioId, invitadoId);
            redirectAttrs.addFlashAttribute("mensaje", "Invitación enviada exitosamente.");
        } catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo enviar la invitación.");
        }

        return "redirect:/comunidad/grupos/" + id;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BUSCAR USUARIOS — GET /comunidad/grupos/{id}/buscar-usuarios?q=...
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/buscar-usuarios")
    @ResponseBody
    public List<Map<String, Object>> buscarUsuarios(@PathVariable Long id,
                                                     @RequestParam String q) {
        return grupoService.buscarUsuarios(q, id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────────────────

    private Long getUsuarioId() {
        Usuario usuario = getUsuario();
        return usuario != null ? usuario.getId() : null;
    }

    private Usuario getUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails customUser) {
            return customUser.getUsuario();
        }
        return null;
    }
}