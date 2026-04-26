package com.scholarstay.app.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.service.AlojamientoService;
import com.scholarstay.app.service.NotificacionService;
import com.scholarstay.app.service.PerfilAcademicoService;
import com.scholarstay.app.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebViewController {

    private final AlojamientoService alojamientoService;
    private final NotificacionService notificacionService;
    private final PerfilAcademicoService perfilAcademicoService;
    private final UsuarioService usuarioService;

    public WebViewController(AlojamientoService alojamientoService, NotificacionService notificacionService, PerfilAcademicoService perfilAcademicoService, UsuarioService usuarioService) {
        this.alojamientoService = alojamientoService;
        this.notificacionService = notificacionService;
        this.perfilAcademicoService = perfilAcademicoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard"; 
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loggedUser") != null) return "redirect:/dashboard";
        return "iniciarSesion";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("loggedUser") != null) return "redirect:/dashboard";
        return "CrearNuevaCuenta";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("loggedUser");
        if (usuario == null) return "redirect:/login";
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("alojamientos", alojamientoService.listar());
        model.addAttribute("notificaciones", notificacionService.obtenerNoLeidas(usuario.getId()));
        return "explorarResidencias";
    }

    @GetMapping("/explorar")
    public String explorar(HttpSession session, Model model) {
        return dashboard(session, model);
    }

    @GetMapping("/explorar-residencias")
    public String explorarResidencias(HttpSession session, Model model) {
        return dashboard(session, model);
    }

    @GetMapping("/alojamiento/{id}")
    public String accommodationDetail(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("loggedUser");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        Alojamiento alojamiento = alojamientoService.obtenerPorId(id);
        if (alojamiento == null) return "redirect:/dashboard";
        
        model.addAttribute("alojamiento", alojamiento);
        return "detalleAlojamiento";
    }

    @GetMapping("/perfil")
    public String profile(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("loggedUser");
        if (usuario == null) return "redirect:/login";
        
        // Reload user to get fresh data
        usuario = usuarioService.obtenerPorId(usuario.getId()).orElse(null);
        session.setAttribute("loggedUser", usuario);

        PerfilAcademico perfil = perfilAcademicoService.obtenerPorUsuario(usuario.getId());
        if(perfil == null) perfil = new PerfilAcademico();
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", perfil);
        return "editarperfilEscolar";
    }

    @GetMapping("/editar-perfil-escolar")
    public String editarPerfilEscolar(HttpSession session, Model model) {
        return profile(session, model);
    }
    
    @PostMapping("/perfil")
    public String updateProfile(HttpSession session, @ModelAttribute com.scholarstay.app.dto.PerfilAcademicoDTO dto) {
        Usuario usuario = (Usuario) session.getAttribute("loggedUser");
        if (usuario == null) return "redirect:/login";
        
        // Basic validation
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty() || 
            dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            return "redirect:/perfil?error=missing_fields";
        }

        perfilAcademicoService.actualizarPerfil(usuario.getId(), dto);
        
        // Update session user
        Usuario updatedUser = usuarioService.obtenerPorId(usuario.getId()).orElse(null);
        session.setAttribute("loggedUser", updatedUser);
        
        return "redirect:/perfil?success=true";
    }
    
    @GetMapping("/resenas")
    public String reviews(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("loggedUser");
        if (usuario == null) return "redirect:/login";
        
        model.addAttribute("usuario", usuario);
        return "ver_todas_las_resenas";
    }

    @GetMapping("/notificaciones")
    public String notifications(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("loggedUser");
        if (usuario == null) return "redirect:/login";
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("notificaciones", notificacionService.obtenerNotificaciones(usuario.getId()));
        return "notificaciones";
    }
}
