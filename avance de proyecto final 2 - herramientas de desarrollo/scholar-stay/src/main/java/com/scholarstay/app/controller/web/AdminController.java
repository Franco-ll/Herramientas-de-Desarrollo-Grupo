package com.scholarstay.app.controller.web;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.UsuarioRepository;
import com.scholarstay.app.security.CustomUserDetails;
import com.scholarstay.app.service.AdminDashboardService;
import com.scholarstay.app.service.AlojamientoService;
import com.scholarstay.app.service.ConfiguracionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminDashboardService adminDashboardService;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoService alojamientoService;
    private final ConfiguracionService configuracionService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminDashboardService adminDashboardService,
                           UsuarioRepository usuarioRepository,
                           AlojamientoService alojamientoService,
                           ConfiguracionService configuracionService,
                           PasswordEncoder passwordEncoder) {
        this.adminDashboardService = adminDashboardService;
        this.usuarioRepository = usuarioRepository;
        this.alojamientoService = alojamientoService;
        this.configuracionService = configuracionService;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario getAdminUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUsuario();
        }
        return null;
    }

    private String guardarImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) return null;
        try {
            Path carpetaSrc = Paths.get("src/main/resources/static/images");
            Path carpetaTarget = Paths.get("target/classes/static/images");
            if (!Files.exists(carpetaSrc)) Files.createDirectories(carpetaSrc);
            if (!Files.exists(carpetaTarget)) Files.createDirectories(carpetaTarget);
            String extension = "";
            String original = archivo.getOriginalFilename();
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf("."));
            }
            String nombreArchivo = UUID.randomUUID().toString().substring(0, 8) + extension;
            Files.copy(archivo.getInputStream(), carpetaSrc.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(archivo.getInputStream(), carpetaTarget.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
            return nombreArchivo;
        } catch (IOException e) {
            return null;
        }
    }

    // ============================================================
    // DASHBOARD
    // ============================================================

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("stats", adminDashboardService.getDashboardStats());
        model.addAttribute("admin", getAdminUser());
        return "admin/dashboard";
    }

    // ============================================================
    // RESIDENTES
    // ============================================================

    @GetMapping("/residentes")
    public String residentes(Model model) {
        model.addAttribute("vm", adminDashboardService.getResidentesStats());
        model.addAttribute("admin", getAdminUser());
        return "admin/residentes";
    }

    @GetMapping("/residentes/{id}")
    public String verResidente(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return "redirect:/admin/residentes";
        model.addAttribute("residente", usuario);
        model.addAttribute("admin", getAdminUser());
        return "admin/residente_detalle";
    }

    @GetMapping("/residentes/{id}/editar")
    public String editarResidenteForm(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return "redirect:/admin/residentes";
        model.addAttribute("residente", usuario);
        model.addAttribute("admin", getAdminUser());
        return "admin/residente_editar";
    }

    @PostMapping("/residentes/{id}/editar")
    public String guardarEdicionResidente(@PathVariable Long id,
                                          @RequestParam String nombre,
                                          @RequestParam String email,
                                          RedirectAttributes ra) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setNombre(nombre);
            u.setEmail(email);
            usuarioRepository.save(u);
        });
        ra.addFlashAttribute("exito", "Residente actualizado correctamente.");
        return "redirect:/admin/residentes";
    }

    @PostMapping("/residentes/{id}/eliminar")
    public String eliminarResidente(@PathVariable Long id, RedirectAttributes ra) {
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
    // PROPIEDADES
    // ============================================================

    @GetMapping("/propiedades")
    public String propiedades(Model model) {
        model.addAttribute("vm", adminDashboardService.getPropiedadesStats());
        model.addAttribute("admin", getAdminUser());
        return "admin/propiedades";
    }

    @GetMapping("/propiedades/{id}")
    public String verPropiedad(@PathVariable Long id, Model model) {
        Alojamiento propiedad = alojamientoService.obtenerPorId(id);
        if (propiedad == null) return "redirect:/admin/propiedades";
        model.addAttribute("admin", getAdminUser());
        model.addAttribute("usuario", getAdminUser());
        model.addAttribute("alojamiento", propiedad);
        return "admin/detalleAlojamientoAdmin";
    }

    @GetMapping("/propiedades/nueva")
    public String nuevaPropiedadForm(Model model) {
        model.addAttribute("admin", getAdminUser());
        model.addAttribute("propiedad", new Alojamiento());
        model.addAttribute("serviciosStr", "");
        model.addAttribute("reglasStr", "");
        model.addAttribute("modoEdicion", false);
        return "admin/propiedad_form";
    }

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
            RedirectAttributes ra) {

        Alojamiento a = new Alojamiento();
        a.setTitulo(titulo.trim());
        a.setDescripcion(descripcion.trim());
        a.setPrecioMensual(precioMensual);
        a.setUbicacion(ubicacion.trim());
        a.setHabitaciones(habitaciones != null ? habitaciones : 1);
        a.setBanos(banos != null ? banos : 1);
        a.setCalificacionPromedio(0.0);

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

    @GetMapping("/propiedades/{id}/editar")
    public String editarPropiedadForm(@PathVariable Long id, Model model) {
        Alojamiento propiedad = alojamientoService.obtenerPorId(id);
        if (propiedad == null) return "redirect:/admin/propiedades";

        String serviciosStr = propiedad.getServicios() != null ? String.join(", ", propiedad.getServicios()) : "";
        String reglasStr = propiedad.getReglas() != null ? String.join(", ", propiedad.getReglas()) : "";

        model.addAttribute("admin", getAdminUser());
        model.addAttribute("propiedad", propiedad);
        model.addAttribute("serviciosStr", serviciosStr);
        model.addAttribute("reglasStr", reglasStr);
        model.addAttribute("modoEdicion", true);
        return "admin/propiedad_form";
    }

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
            RedirectAttributes ra) {

        Alojamiento a = alojamientoService.obtenerPorId(id);
        if (a == null) return "redirect:/admin/propiedades";

        a.setTitulo(titulo.trim());
        a.setDescripcion(descripcion.trim());
        a.setPrecioMensual(precioMensual);
        a.setUbicacion(ubicacion.trim());
        if (habitaciones != null) a.setHabitaciones(habitaciones);
        if (banos != null) a.setBanos(banos);

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

    @PostMapping("/propiedades/{id}/eliminar")
    public String eliminarPropiedad(@PathVariable Long id, RedirectAttributes ra) {
        Alojamiento a = alojamientoService.obtenerPorId(id);
        if (a != null) {
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
    public String finanzas(Model model) {
        model.addAttribute("vm", adminDashboardService.getFinanzasStats());
        model.addAttribute("admin", getAdminUser());
        return "admin/finanzas";
    }

    // ============================================================
    // CONFIGURACION
    // ============================================================

    @GetMapping("/configuracion")
    public String configuracion(Model model) {
        model.addAttribute("admin", getAdminUser());
        model.addAttribute("config", configuracionService.obtener());
        return "admin/configuracion";
    }

    @PostMapping("/configuracion/sistema")
    public String guardarConfiguracionSistema(
            @RequestParam String nombrePlataforma,
            @RequestParam Double precioMinimo,
            @RequestParam Double precioMaximo,
            @RequestParam Integer maxReservasPorUsuario,
            RedirectAttributes ra) {
        configuracionService.guardarConfiguracion(nombrePlataforma, precioMinimo, precioMaximo, maxReservasPorUsuario);
        ra.addFlashAttribute("exito", "Configuracion del sistema actualizada correctamente.");
        return "redirect:/admin/configuracion";
    }

    @PostMapping("/configuracion/perfil")
    public String guardarPerfil(
            @RequestParam String nombre,
            @RequestParam String email,
            RedirectAttributes ra) {
        Usuario admin = getAdminUser();
        if (admin != null) {
            usuarioRepository.findById(admin.getId()).ifPresent(u -> {
                u.setNombre(nombre.trim());
                u.setEmail(email.trim());
                usuarioRepository.save(u);
            });
            ra.addFlashAttribute("exito", "Perfil actualizado correctamente.");
        }
        return "redirect:/admin/configuracion";
    }

    @PostMapping("/configuracion/contrasena")
    public String cambiarContrasena(
            @RequestParam String passwordNueva,
            @RequestParam String passwordConfirmar,
            RedirectAttributes ra) {
        Usuario admin = getAdminUser();
        if (admin == null) return "redirect:/login";

        if (!passwordNueva.equals(passwordConfirmar)) {
            ra.addFlashAttribute("errorContrasena", "Las contrasenas nuevas no coinciden.");
            return "redirect:/admin/configuracion";
        }
        if (passwordNueva.length() < 6) {
            ra.addFlashAttribute("errorContrasena", "La contrasena debe tener al menos 6 caracteres.");
            return "redirect:/admin/configuracion";
        }

        usuarioRepository.findById(admin.getId()).ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(passwordNueva));
            usuarioRepository.save(u);
        });
        ra.addFlashAttribute("exitoContrasena", "Contrasena actualizada correctamente.");
        return "redirect:/admin/configuracion";
    }
}