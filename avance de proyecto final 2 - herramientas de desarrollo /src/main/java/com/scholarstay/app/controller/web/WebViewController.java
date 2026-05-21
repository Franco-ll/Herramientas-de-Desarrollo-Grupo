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
import com.scholarstay.app.service.ResenaService;
import com.scholarstay.app.service.ReservaService;
import com.scholarstay.app.service.UsuarioService;
import com.scholarstay.app.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebViewController {

    private final AlojamientoService alojamientoService;
    private final NotificacionService notificacionService;
    private final PerfilAcademicoService perfilAcademicoService;
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;
    private final ResenaService resenaService;

    public WebViewController(AlojamientoService alojamientoService, NotificacionService notificacionService,
            PerfilAcademicoService perfilAcademicoService, UsuarioService usuarioService,
            ReservaService reservaService, ResenaService resenaService) {
        this.alojamientoService = alojamientoService;
        this.notificacionService = notificacionService;
        this.perfilAcademicoService = perfilAcademicoService;
        this.usuarioService = usuarioService;
        this.reservaService = reservaService;
        this.resenaService = resenaService;
    }

    private Usuario getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUsuario();
        }
        return null;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loggedUser") != null)
            return "redirect:/dashboard";
        return "iniciarSesion";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("loggedUser") != null)
            return "redirect:/dashboard";
        return "CrearNuevaCuenta";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

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
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        model.addAttribute("usuario", usuario);
        Alojamiento alojamiento = alojamientoService.obtenerPorId(id);
        if (alojamiento == null)
            return "redirect:/dashboard";

        model.addAttribute("alojamiento", alojamiento);
        return "detalleAlojamiento";
    }

   @GetMapping("/perfil")
public String profile(HttpSession session, Model model) {
    Usuario usuario = (Usuario) session.getAttribute("loggedUser");

    if (usuario == null) {
        return "redirect:/login";
    }

    usuario = usuarioService.obtenerPorId(usuario.getId()).orElse(null);
    session.setAttribute("loggedUser", usuario);

    PerfilAcademico perfil = perfilAcademicoService.obtenerPorUsuario(usuario.getId());

    if (perfil == null) {
        perfil = new PerfilAcademico();
    }

    model.addAttribute("usuario", usuario);
    model.addAttribute("perfil", perfil);

    // Vista bonita del perfil
    return "perfil";
}


@GetMapping("/editar-perfil-escolar")
public String editarPerfilEscolar(HttpSession session, Model model) {
    Usuario usuario = (Usuario) session.getAttribute("loggedUser");

    if (usuario == null) {
        return "redirect:/login";
    }

    usuario = usuarioService.obtenerPorId(usuario.getId()).orElse(null);
    session.setAttribute("loggedUser", usuario);

    PerfilAcademico perfil = perfilAcademicoService.obtenerPorUsuario(usuario.getId());

    if (perfil == null) {
        perfil = new PerfilAcademico();
    }

    model.addAttribute("usuario", usuario);
    model.addAttribute("perfil", perfil);

    // Formulario para editar
    return "editarperfilEscolar";
}


@PostMapping("/perfil")
public String updateProfile(
        HttpSession session,
        @ModelAttribute com.scholarstay.app.dto.PerfilAcademicoDTO dto,
        @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile
) {
    Usuario usuario = (Usuario) session.getAttribute("loggedUser");

    if (usuario == null) {
        return "redirect:/login";
    }

    if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()
            || dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
        return "redirect:/editar-perfil-escolar?error=missing_fields";
    }

    perfilAcademicoService.actualizarPerfil(usuario.getId(), dto, avatarFile);

    Usuario updatedUser = usuarioService.obtenerPorId(usuario.getId()).orElse(null);
    session.setAttribute("loggedUser", updatedUser);

    return "redirect:/perfil?success=true";
}

    @GetMapping("/resenas")
    public String reviews(HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        model.addAttribute("usuario", usuario);
        return "ver_todas_las_resenas";
    }

    @GetMapping("/notificaciones")
    public String notifications(HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("notificaciones", notificacionService.obtenerNotificaciones(usuario.getId()));
        return "notificaciones";
    }

    // --- Booking Flow ---

    @GetMapping("/confirmar-reserva-escolar")
    public String confirmReservation(@org.springframework.web.bind.annotation.RequestParam Long alojamientoId,
            HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        Alojamiento alojamiento = alojamientoService.obtenerPorId(alojamientoId);
        if (alojamiento == null)
            return "redirect:/dashboard";

        model.addAttribute("usuario", usuario);
        model.addAttribute("alojamiento", alojamiento);
        return "confirmar_reserva_escolar";
    }

    @GetMapping("/pasarelaPagos")
    public String paymentGateway(@org.springframework.web.bind.annotation.RequestParam Long alojamientoId,
            @org.springframework.web.bind.annotation.RequestParam String fechaInicio,
            @org.springframework.web.bind.annotation.RequestParam String fechaFin,
            @org.springframework.web.bind.annotation.RequestParam Double total,
            HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        Alojamiento alojamiento = alojamientoService.obtenerPorId(alojamientoId);
        if (alojamiento == null)
            return "redirect:/dashboard";

        model.addAttribute("usuario", usuario);
        model.addAttribute("alojamiento", alojamiento);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("total", total);
        return "pasareladePago";
    }

    @PostMapping("/procesar-pago")
    public String processPayment(@org.springframework.web.bind.annotation.RequestParam Long alojamientoId,
            @org.springframework.web.bind.annotation.RequestParam String fechaInicio,
            @org.springframework.web.bind.annotation.RequestParam String fechaFin,
            // Ignoramos el total que envía el cliente para prevenir Parameter Tampering
            // @org.springframework.web.bind.annotation.RequestParam Double total,
            HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        Usuario usuario = getLoggedUser();
        if (usuario == null) return "redirect:/login";

        Alojamiento alojamiento = alojamientoService.obtenerPorId(alojamientoId);
        if (alojamiento == null) return "redirect:/dashboard";

        try {
            // Validamos que el formato de fechas no rompa el sistema con un error 500
            java.time.LocalDate inicio = java.time.LocalDate.parse(fechaInicio);
            java.time.LocalDate fin = java.time.LocalDate.parse(fechaFin);

            com.scholarstay.app.model.Reserva reserva = new com.scholarstay.app.model.Reserva();
            reserva.setAlojamiento(alojamiento);
            reserva.setFechaInicio(inicio);
            reserva.setFechaFin(fin);
            
            // Pasamos la reserva al servicio, él se encargará de validar reglas de negocio y calcular el total REAL
            com.scholarstay.app.model.Reserva guardada = reservaService.crearReserva(reserva, usuario);
            session.setAttribute("lastReservaId", guardada.getId());

            return "redirect:/reservaConfirmada";
        } catch (java.time.format.DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "El formato de fecha ingresado no es válido.");
            return "redirect:/pasarelaPagos?alojamientoId=" + alojamientoId + "&fechaInicio=" + fechaInicio + "&fechaFin=" + fechaFin + "&total=0.0";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Capturamos las excepciones de validación que vienen del servicio
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pasarelaPagos?alojamientoId=" + alojamientoId + "&fechaInicio=" + fechaInicio + "&fechaFin=" + fechaFin + "&total=0.0";
        }
    }

    @GetMapping("/reservaConfirmada")
    public String reservationConfirmed(HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        Long lastReservaId = (Long) session.getAttribute("lastReservaId");
        if (lastReservaId == null)
            return "redirect:/dashboard";

        com.scholarstay.app.model.Reserva reserva = reservaService.obtenerReservasPorUsuario(usuario.getId())
                .stream().filter(r -> r.getId().equals(lastReservaId)).findFirst().orElse(null);

        model.addAttribute("usuario", usuario);
        model.addAttribute("reserva", reserva);
        return "reservaConfirmada";
    }

    @GetMapping("/reciboDigital")
    public String digitalReceipt(HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        Long lastReservaId = (Long) session.getAttribute("lastReservaId");
        if (lastReservaId == null)
            return "redirect:/dashboard";

        com.scholarstay.app.model.Reserva reserva = reservaService.obtenerReservasPorUsuario(usuario.getId())
                .stream().filter(r -> r.getId().equals(lastReservaId)).findFirst().orElse(null);

        model.addAttribute("usuario", usuario);
        model.addAttribute("reserva", reserva);
        return "reciboDigital";
    }

    // --- Reviews ---

    @GetMapping("/ver_todas_las_resenas")
    public String viewAllReviews(@org.springframework.web.bind.annotation.RequestParam Long alojamientoId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size,
            HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        Alojamiento alojamiento = alojamientoService.obtenerPorId(alojamientoId);
        if (alojamiento == null)
            return "redirect:/dashboard";

        // Usamos PageRequest para manejar la paginación de las reseñas
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<com.scholarstay.app.model.Resena> paginaResenas = resenaService.obtenerResenasPaginadasPorAlojamiento(alojamientoId, pageable);

        model.addAttribute("usuario", usuario);
        model.addAttribute("alojamiento", alojamiento);
        model.addAttribute("resenas", paginaResenas.getContent()); // Enviamos solo las reseñas de esta página
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaResenas.getTotalPages());
        model.addAttribute("haySiguiente", paginaResenas.hasNext());
        model.addAttribute("hayAnterior", paginaResenas.hasPrevious());
        
        return "ver_todas_las_resenas";
    }

    @GetMapping("/resenas_academicas")
    public String writeReviewPage(@org.springframework.web.bind.annotation.RequestParam Long alojamientoId,
            HttpSession session, Model model) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        Alojamiento alojamiento = alojamientoService.obtenerPorId(alojamientoId);
        if (alojamiento == null)
            return "redirect:/dashboard";

        model.addAttribute("usuario", usuario);
        model.addAttribute("alojamiento", alojamiento);
        return "resenasAcademicas";
    }

    @PostMapping("/publicar_resena")
    public String publishReview(@org.springframework.web.bind.annotation.RequestParam Long alojamientoId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer calificacion,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String comentario,
            HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        Alojamiento alojamiento = alojamientoService.obtenerPorId(alojamientoId);

        try {
            com.scholarstay.app.model.Resena resena = new com.scholarstay.app.model.Resena();
            resena.setAlojamiento(alojamiento);
            resena.setCalificacion(calificacion);
            resena.setComentario(comentario);
            
            // ResenaService se encargará de las validaciones de negocio e integridad
            resenaService.crearResena(resena, usuario);

            redirectAttributes.addFlashAttribute("success", "Tu reseña se publicó exitosamente.");
            return "redirect:/alojamiento/" + alojamientoId;
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/resenas_academicas?alojamientoId=" + alojamientoId;
        }
    }
}
