package com.scholarstay.app.controller.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.UsuarioRepository;
import com.scholarstay.app.service.AdminDashboardService;
import com.scholarstay.app.service.AlojamientoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminDashboardService adminDashboardService;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoService alojamientoService;

    public AdminController(AdminDashboardService adminDashboardService,
                           UsuarioRepository usuarioRepository,
                           AlojamientoService alojamientoService) {
        this.adminDashboardService = adminDashboardService;
        this.usuarioRepository = usuarioRepository;
        this.alojamientoService = alojamientoService;
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
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("stats", adminDashboardService.getDashboardStats());
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/dashboard";
    }

    @GetMapping("/residentes")
    public String residentes(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("vm", adminDashboardService.getResidentesStats());
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/residentes";
    }

    @GetMapping("/residentes/{id}")
    public String verResidente(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return "redirect:/admin/residentes";
        model.addAttribute("residente", usuario);
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/residente_detalle";
    }

    @GetMapping("/residentes/{id}/editar")
    public String editarResidenteForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return "redirect:/admin/residentes";
        model.addAttribute("residente", usuario);
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/residente_editar";
    }

    @PostMapping("/residentes/{id}/editar")
    public String guardarEdicionResidente(@PathVariable Long id,
                                          @RequestParam String nombre,
                                          @RequestParam String email,
                                          HttpSession session,
                                          RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setNombre(nombre);
            u.setEmail(email);
            usuarioRepository.save(u);
        });
        ra.addFlashAttribute("exito", "Residente actualizado correctamente.");
        return "redirect:/admin/residentes";
    }

    @PostMapping("/residentes/{id}/eliminar")
    public String eliminarResidente(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
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

    // ============================================================
    // HELPER: guardar imagen subida
    // ============================================================
    private String guardarImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) return null;
        try {
            // Carpeta uploads en la raíz del proyecto (fuera de src y target)
            Path carpeta = Paths.get("uploads/images");
            if (!Files.exists(carpeta)) Files.createDirectories(carpeta);

            String extension = "";
            String original = archivo.getOriginalFilename();
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf("."));
            }
            String nombreArchivo = UUID.randomUUID().toString().substring(0, 8) + extension;

            Files.copy(archivo.getInputStream(),
                       carpeta.resolve(nombreArchivo),
                       StandardCopyOption.REPLACE_EXISTING);

            return nombreArchivo;
        } catch (IOException e) {
            return null;
        }
    }

    // ============================================================
    // PROPIEDADES
    // ============================================================

    @GetMapping("/propiedades")
    public String propiedades(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("vm", adminDashboardService.getPropiedadesStats());
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/propiedades";
    }

    /** Formulario para AÑADIR una nueva propiedad */
    @GetMapping("/propiedades/nueva")
    public String nuevaPropiedadForm(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        model.addAttribute("propiedad", new Alojamiento());
        model.addAttribute("modoEdicion", false);
        return "admin/propiedad_form";
    }

    /** Guardar NUEVA propiedad en la BD */
    @PostMapping("/propiedades/nueva")
    public String guardarNuevaPropiedad(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam Double precioMensual,
            @RequestParam String ubicacion,
            @RequestParam(required = false) Integer habitaciones,
            @RequestParam(required = false) Integer banos,
            @RequestParam(required = false) MultipartFile imagenArchivo,
            @RequestParam(required = false) String servicios,
            @RequestParam(required = false) String reglas,
            HttpSession session,
            RedirectAttributes ra) {

        if (!esAdmin(session)) return "redirect:/login";

        Alojamiento a = new Alojamiento();
        a.setTitulo(titulo.trim());
        a.setDescripcion(descripcion.trim());
        a.setPrecioMensual(precioMensual);
        a.setUbicacion(ubicacion.trim());
        a.setHabitaciones(habitaciones != null ? habitaciones : 1);
        a.setBanos(banos != null ? banos : 1);
        a.setCalificacionPromedio(0.0);

        // Guardar imagen si se subió
        String nombreImagen = guardarImagen(imagenArchivo);
        if (nombreImagen != null) a.setImagen(nombreImagen);

        if (servicios != null && !servicios.isBlank()) {
            List<String> lista = Arrays.stream(servicios.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            a.setServicios(lista);
        }
        if (reglas != null && !reglas.isBlank()) {
            List<String> lista = Arrays.stream(reglas.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            a.setReglas(lista);
        }

        alojamientoService.save(a);
        ra.addFlashAttribute("exito", "Propiedad \"" + titulo + "\" añadida correctamente.");
        return "redirect:/admin/propiedades";
    }

    /** Ver detalle de una propiedad (vista admin) */
    @GetMapping("/propiedades/{id}")
    public String verPropiedad(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        Alojamiento propiedad = alojamientoService.obtenerPorId(id);
        if (propiedad == null) return "redirect:/admin/propiedades";
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        model.addAttribute("usuario", session.getAttribute("loggedUser"));
        model.addAttribute("alojamiento", propiedad);
        return "admin/detalleAlojamientoAdmin";
    }

    /** Formulario para EDITAR una propiedad existente */
    @GetMapping("/propiedades/{id}/editar")
    public String editarPropiedadForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        Alojamiento propiedad = alojamientoService.obtenerPorId(id);
        if (propiedad == null) return "redirect:/admin/propiedades";

        // Convertir listas a texto separado por comas para el formulario
        String serviciosStr = propiedad.getServicios() != null
                ? String.join(", ", propiedad.getServicios()) : "";
        String reglasStr = propiedad.getReglas() != null
                ? String.join(", ", propiedad.getReglas()) : "";

        model.addAttribute("admin", session.getAttribute("loggedUser"));
        model.addAttribute("propiedad", propiedad);
        model.addAttribute("serviciosStr", serviciosStr);
        model.addAttribute("reglasStr", reglasStr);
        model.addAttribute("modoEdicion", true);
        return "admin/propiedad_form";
    }

    /** Guardar EDICIÓN de una propiedad existente */
    @PostMapping("/propiedades/{id}/editar")
    public String guardarEdicionPropiedad(
            @PathVariable Long id,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam Double precioMensual,
            @RequestParam String ubicacion,
            @RequestParam(required = false) Integer habitaciones,
            @RequestParam(required = false) Integer banos,
            @RequestParam(required = false) MultipartFile imagenArchivo,
            @RequestParam(required = false) String servicios,
            @RequestParam(required = false) String reglas,
            HttpSession session,
            RedirectAttributes ra) {

        if (!esAdmin(session)) return "redirect:/login";

        Alojamiento a = alojamientoService.obtenerPorId(id);
        if (a == null) return "redirect:/admin/propiedades";

        a.setTitulo(titulo.trim());
        a.setDescripcion(descripcion.trim());
        a.setPrecioMensual(precioMensual);
        a.setUbicacion(ubicacion.trim());
        if (habitaciones != null) a.setHabitaciones(habitaciones);
        if (banos != null) a.setBanos(banos);

        // Solo actualiza imagen si se subió una nueva
        String nombreImagen = guardarImagen(imagenArchivo);
        if (nombreImagen != null) a.setImagen(nombreImagen);

        if (servicios != null && !servicios.isBlank()) {
            List<String> lista = Arrays.stream(servicios.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            a.setServicios(lista);
        }
        if (reglas != null && !reglas.isBlank()) {
            List<String> lista = Arrays.stream(reglas.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            a.setReglas(lista);
        }

        alojamientoService.save(a);
        ra.addFlashAttribute("exito", "Propiedad \"" + titulo + "\" actualizada correctamente.");
        return "redirect:/admin/propiedades";
    }

    /** Eliminar propiedad */
    @PostMapping("/propiedades/{id}/eliminar")
    public String eliminarPropiedad(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        Alojamiento a = alojamientoService.obtenerPorId(id);
        if (a != null) {
            // Nota: si tiene reservas activas, Hibernate lanzará error por FK.
            // Podrías añadir lógica extra aquí si lo necesitas.
            try {
                alojamientoService.eliminar(id);
                ra.addFlashAttribute("exito", "Propiedad eliminada.");
            } catch (Exception e) {
                ra.addFlashAttribute("error", "No se puede eliminar: la propiedad tiene reservas asociadas.");
            }
        }
        return "redirect:/admin/propiedades";
    }

    // ============================================================
    // FINANZAS
    // ============================================================

    @GetMapping("/finanzas")
    public String finanzas(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("vm", adminDashboardService.getFinanzasStats());
        model.addAttribute("admin", session.getAttribute("loggedUser"));
        return "admin/finanzas";
    }
}