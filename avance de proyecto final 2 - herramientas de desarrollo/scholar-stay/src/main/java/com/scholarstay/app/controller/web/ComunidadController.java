package com.scholarstay.app.controller.web;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scholarstay.app.dto.MatchDTO;
import com.scholarstay.app.dto.PerfilDTO;
import com.scholarstay.app.model.Grupo;
import com.scholarstay.app.security.CustomUserDetails;
import com.scholarstay.app.service.ComunidadService;
import com.scholarstay.app.service.ConversacionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/comunidad")
public class ComunidadController {

    private final ComunidadService comunidadService;
    private final ConversacionService conversacionService;

    public ComunidadController(ComunidadService comunidadService,
                                ConversacionService conversacionService) {
        this.comunidadService = comunidadService;
        this.conversacionService = conversacionService;
    }

    @GetMapping({"", "/", "/inicio"})
    public String inicio(HttpSession session, Model model) {
        Long usuarioId = extractUserId(session);

        model.addAttribute("carreras", comunidadService.getAllCarreras());
        model.addAttribute("intereses", comunidadService.getAllIntereses());

        List<MatchDTO> featured = comunidadService.findMatches(usuarioId, null, null);
        model.addAttribute("featuredMatches", featured.stream().limit(2).toList());

        List<Grupo> grupos = comunidadService.findGrupos(null, null);
        model.addAttribute("gruposDestacados", grupos.stream().limit(2).toList());

        return "comunidad/inicio";
    }

    @GetMapping("/matches")
    public String matches(HttpSession session, Model model,
                          @RequestParam(required = false) String carrera,
                          @RequestParam(required = false) java.util.List<String> interes,
                          @RequestParam(defaultValue = "1") int page) {
        Long usuarioId = extractUserId(session);
        String selectedCarrera = normalizeFilter(carrera);
        java.util.List<String> selectedIntereses = normalizeInteresFilters(interes);

        List<MatchDTO> allMatches = comunidadService.findMatches(usuarioId, selectedCarrera, selectedIntereses);
        int pageSize = 6;
        int totalMatches = allMatches.size();
        int totalPages = totalMatches == 0 ? 1 : (int) Math.ceil((double) totalMatches / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;
        int start = Math.min((page - 1) * pageSize, totalMatches);
        int end = Math.min(start + pageSize, totalMatches);
        List<MatchDTO> pageMatches = start < end ? allMatches.subList(start, end) : List.of();

        model.addAttribute("carreras", comunidadService.getAllCarreras());
        model.addAttribute("intereses", comunidadService.getAllIntereses());
        model.addAttribute("matches", pageMatches);
        model.addAttribute("selectedCarrera", selectedCarrera);
        model.addAttribute("selectedIntereses", selectedIntereses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasPrevious", page > 1);
        model.addAttribute("hasNext", page < totalPages);
        model.addAttribute("totalMatches", totalMatches);
        return "comunidad/lista_coincidencia";
    }

    @GetMapping("/perfil-verificado")
    public String perfilVerificado(HttpSession session) {
        Long usuarioId = extractUserId(session);
        if (usuarioId == null) {
            return "redirect:/comunidad/inicio";
        }
        return "redirect:/comunidad/perfil/" + usuarioId;
    }

    private String normalizeFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Normaliza la lista de intereses recibida como multi-valor en la query string.
     * Elimina nulos, blancos y espacios sobrantes.
     */
    private java.util.List<String> normalizeInteresFilters(java.util.List<String> values) {
        if (values == null || values.isEmpty()) return null;
        java.util.List<String> clean = values.stream()
                .filter(v -> v != null && !v.isBlank())
                .map(String::trim)
                .collect(java.util.stream.Collectors.toList());
        return clean.isEmpty() ? null : clean;
    }

    @GetMapping("/chat")
    public String chat(HttpSession session, Model model,
                       @RequestParam(required = false) Long con) {
        Long usuarioId = extractUserId(session);
        model.addAttribute("con", con);

        if (usuarioId != null) {
            try {
                com.scholarstay.app.dto.ConversacionDTO conversacion = null;
                if (con != null && !con.equals(usuarioId)) {
                    conversacion = conversacionService.obtenerOCrear(usuarioId, con);
                    model.addAttribute("conversacionAbierta", conversacion);
                }
            } catch (Exception e) {

            }
        }

        return "comunidad/chat_mensajes";
    }

    @GetMapping("/grupos")
    public String grupos(HttpSession session, Model model,
                         @RequestParam(required = false) String carrera,
                         @RequestParam(required = false) java.util.List<String> interes) {
        java.util.List<String> interesesFiltro = normalizeInteresFilters(interes);
        model.addAttribute("grupos", comunidadService.findGrupos(carrera, interesesFiltro));
        model.addAttribute("carreras", comunidadService.getAllCarreras());
        model.addAttribute("intereses", comunidadService.getAllIntereses());
        model.addAttribute("selectedCarrera", carrera);
        model.addAttribute("selectedIntereses", interesesFiltro);
        return "comunidad/lista_grupos";
    }

    @GetMapping("/invitacion")
    public String invitacion(HttpSession session) {
        return "comunidad/invitacion_comunidad";
    }

    @GetMapping("/perfil/{id}")
    public String perfilById(@org.springframework.web.bind.annotation.PathVariable Long id, Model model, HttpSession session) {
        Long currentUserId = extractUserId(session);
        
        // Obtener perfil del usuario seleccionado
        PerfilDTO perfil = comunidadService.getPerfilByUsuarioId(id);
        if (perfil == null) {
            return "redirect:/comunidad/inicio";
        }
        
        // Calcular compatibilidad con usuario actual
        if (currentUserId != null && !currentUserId.equals(id)) {
            Double compatibilidad = comunidadService.calcularCompatibilidad(currentUserId, id);
            perfil.setPorcentajeCompatibilidad(compatibilidad);
        }
        
        model.addAttribute("perfil", perfil);
        return "comunidad/perfil_verificado";
    }

    @GetMapping("/api/matches")
    public @org.springframework.web.bind.annotation.ResponseBody java.util.List<MatchDTO> apiMatches(HttpSession session,
                                                                                                      @org.springframework.web.bind.annotation.RequestParam(required = false) String carrera,
                                                                                                      @org.springframework.web.bind.annotation.RequestParam(required = false) java.util.List<String> interes) {
        Long usuarioId = extractUserId(session);
        java.util.List<String> interesesFiltro = normalizeInteresFilters(interes);
        return comunidadService.findMatches(usuarioId, carrera, interesesFiltro).stream().limit(50).toList();
    }

    @GetMapping("/api/grupos")
    public @org.springframework.web.bind.annotation.ResponseBody java.util.List<com.scholarstay.app.model.Grupo> apiGrupos(@org.springframework.web.bind.annotation.RequestParam(required = false) String carrera,
                                                                                                                        @org.springframework.web.bind.annotation.RequestParam(required = false) java.util.List<String> interes) {
        return comunidadService.findGrupos(carrera, normalizeInteresFilters(interes));
    }

    @GetMapping("/recursos")
    public String recursos(HttpSession session) {
        return "comunidad/recursos_comunidad";
    }

    /**
     * Obtiene el ID del usuario autenticado desde el SecurityContext de Spring Security.
     * Este es el único método fiable: el login usa Spring Security (no guarda
     * el usuario manualmente en HttpSession), así que hay que leer desde
     * SecurityContextHolder igual que hace WebViewController.
     */
    private Long extractUserId(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails customUser) {
            return customUser.getUsuario().getId();
        }
        // Fallback legacy por si algún flujo guardó "loggedUser" en sesión
        Object loggedUser = session.getAttribute("loggedUser");
        if (loggedUser instanceof com.scholarstay.app.model.Usuario u) {
            return u.getId();
        }
        return null;
    }
}