package com.scholarstay.app.controller.web;

import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.UsuarioRepository;
import com.scholarstay.app.service.AdminDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminDashboardService adminDashboardService;
    private final UsuarioRepository usuarioRepository;

    public AdminController(AdminDashboardService adminDashboardService,
            UsuarioRepository usuarioRepository) {
        this.adminDashboardService = adminDashboardService;
        this.usuarioRepository = usuarioRepository;
    }

    // --- Validación manual de acceso admin ---
    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("loggedUser");
        return usuario != null
                && usuario.getRol() != null
                && "ROLE_ADMIN".equals(usuario.getRol().getNombre());
    }

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(HttpSession session, Model model) {
        if (!esAdmin(session))
            return "redirect:/login";
        model.addAttribute("stats", adminDashboardService.getDashboardStats());
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/dashboard";
    }

    @GetMapping("/residentes")
    public String residentes(HttpSession session, Model model) {
        if (!esAdmin(session))
            return "redirect:/login";
        model.addAttribute("vm", adminDashboardService.getResidentesStats());
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/residentes";
    }

    // --- Ver detalle del residente ---
    @GetMapping("/residentes/{id}")
    public String verResidente(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session))
            return "redirect:/login";

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null)
            return "redirect:/admin/residentes";

        model.addAttribute("residente", usuario);
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/residente_detalle";
    }

    // --- Editar residente (formulario) ---
    @GetMapping("/residentes/{id}/editar")
    public String editarResidenteForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session))
            return "redirect:/login";

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null)
            return "redirect:/admin/residentes";

        model.addAttribute("residente", usuario);
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/residente_editar";
    }

    // --- Guardar edición del residente ---
    @PostMapping("/residentes/{id}/editar")
    public String guardarEdicionResidente(@PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String email,
            HttpSession session,
            RedirectAttributes ra) {
        if (!esAdmin(session))
            return "redirect:/login";

        usuarioRepository.findById(id).ifPresent(u -> {
            u.setNombre(nombre);
            u.setEmail(email);
            usuarioRepository.save(u);
        });

        ra.addFlashAttribute("exito", "Residente actualizado correctamente.");
        return "redirect:/admin/residentes";
    }

    // --- Eliminar residente ---
    @PostMapping("/residentes/{id}/eliminar")
    public String eliminarResidente(@PathVariable Long id,
            HttpSession session,
            RedirectAttributes ra) {
        if (!esAdmin(session))
            return "redirect:/login";

        // Verificar que no se elimine a sí mismo ni al admin
        Usuario target = usuarioRepository.findById(id).orElse(null);
        if (target == null) {
            ra.addFlashAttribute("error", "Residente no encontrado.");
            return "redirect:/admin/residentes";
        }
        if (target.getRol() != null && "ROLE_ADMIN".equals(target.getRol().getNombre())) {
            ra.addFlashAttribute("error", "No se puede eliminar una cuenta de administrador.");
            return "redirect:/admin/residentes";
        }

        usuarioRepository.deleteById(id);
        ra.addFlashAttribute("exito", "Residente eliminado correctamente.");
        return "redirect:/admin/residentes";
    }

    @GetMapping("/propiedades")
    public String propiedades(HttpSession session, Model model) {
        if (!esAdmin(session))
            return "redirect:/login";
        model.addAttribute("vm", adminDashboardService.getPropiedadesStats());
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/propiedades";
    }

    @GetMapping("/finanzas")
    public String finanzas(HttpSession session, Model model) {
        if (!esAdmin(session))
            return "redirect:/login";
        model.addAttribute("vm", adminDashboardService.getFinanzasStats());
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/finanzas";
    }
}
