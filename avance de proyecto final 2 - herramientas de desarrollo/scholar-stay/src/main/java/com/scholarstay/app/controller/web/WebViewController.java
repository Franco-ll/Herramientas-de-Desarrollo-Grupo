package com.scholarstay.app.controller.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.Evento;
import com.scholarstay.app.model.Notificacion;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.service.AlojamientoService;
import com.scholarstay.app.service.EventoService;
import com.scholarstay.app.service.InscripcionEventoService;
import com.scholarstay.app.service.NotificacionService;
import com.scholarstay.app.service.PerfilAcademicoService;
import com.scholarstay.app.service.ResenaService;
import com.scholarstay.app.service.ReservaService;
import com.scholarstay.app.service.UsuarioService;
import com.scholarstay.app.service.EventoService;
import com.scholarstay.app.service.InscripcionEventoService;
import com.scholarstay.app.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Controller
public class WebViewController {

    private final AlojamientoService alojamientoService;
    private final EventoService eventoService;
    private final InscripcionEventoService inscripcionEventoService;
    private final NotificacionService notificacionService;
    private final PerfilAcademicoService perfilAcademicoService;
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;
    private final ResenaService resenaService;

    public WebViewController(
            AlojamientoService alojamientoService,
            NotificacionService notificacionService,
            PerfilAcademicoService perfilAcademicoService,
            UsuarioService usuarioService,
            ReservaService reservaService,
            ResenaService resenaService,
            EventoService eventoService,
            InscripcionEventoService inscripcionEventoService) {

        this.alojamientoService = alojamientoService;
        this.notificacionService = notificacionService;
        this.perfilAcademicoService = perfilAcademicoService;
        this.usuarioService = usuarioService;
        this.reservaService = reservaService;
        this.resenaService = resenaService;
        this.eventoService = eventoService;
        this.inscripcionEventoService = inscripcionEventoService;
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
        model.addAttribute("noLeidas", notificacionService.obtenerNoLeidas(usuario.getId()).size());
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
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

        // Reload user to get fresh data
        usuario = usuarioService.obtenerPorId(usuario.getId()).orElse(null);
        session.setAttribute("loggedUser", usuario);

        PerfilAcademico perfil = perfilAcademicoService.obtenerPorUsuario(usuario.getId());
        if (perfil == null)
            perfil = new PerfilAcademico();

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
        Usuario usuario = getLoggedUser();
        if (usuario == null)
            return "redirect:/login";

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

    @PostMapping("/perfil/avatar")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            HttpSession session) {
        Usuario usuario = getLoggedUser();
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Archivo vacío"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Solo se permiten imágenes"));
        }
        try {
            Path uploadDir = Paths.get("uploads/images").toAbsolutePath();
            Files.createDirectories(uploadDir);
            String extension = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                    ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
                    : ".jpg";
            String filename = "avatar_" + usuario.getId() + "_" + UUID.randomUUID().toString().substring(0, 8)
                    + extension;
            Path destination = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            usuario.setAvatar(filename);
            usuarioService.guardar(usuario);
            return ResponseEntity.ok(Map.of("avatarUrl", "/images/" + filename));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al guardar la imagen"));
        }
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

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute(
                "notificaciones",
                notificacionService.obtenerNotificaciones(usuario.getId()));
        model.addAttribute(
                "noLeidas",
                notificacionService.obtenerNoLeidas(usuario.getId()).size());

        return "notificaciones";
    }

    @GetMapping("/notificaciones/{id}")
    public String verNotificacion(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        Usuario usuario = getLoggedUser();

        if (usuario == null) {
            return "redirect:/login";
        }

        Notificacion notificacion = notificacionService.obtenerPorId(id);

        if (notificacion == null) {
            return "redirect:/notificaciones";
        }

        if (!notificacion.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/notificaciones";
        }

        if (!notificacion.getLeido()) {
            notificacionService.marcarComoLeida(id);
            notificacion.setLeido(true);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("notificacion", notificacion);

        return "detalle-notificacion";
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
        if (usuario == null)
            return "redirect:/login";

        Alojamiento alojamiento = alojamientoService.obtenerPorId(alojamientoId);
        if (alojamiento == null)
            return "redirect:/dashboard";

        try {
            // Validamos que el formato de fechas no rompa el sistema con un error 500
            java.time.LocalDate inicio = java.time.LocalDate.parse(fechaInicio);
            java.time.LocalDate fin = java.time.LocalDate.parse(fechaFin);

            com.scholarstay.app.model.Reserva reserva = new com.scholarstay.app.model.Reserva();
            reserva.setAlojamiento(alojamiento);
            reserva.setFechaInicio(inicio);
            reserva.setFechaFin(fin);

            // Pasamos la reserva al servicio, él se encargará de validar reglas de negocio
            // y calcular el total REAL
            com.scholarstay.app.model.Reserva guardada = reservaService.crearReserva(reserva, usuario);
            session.setAttribute("lastReservaId", guardada.getId());

            return "redirect:/reservaConfirmada";
        } catch (java.time.format.DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "El formato de fecha ingresado no es válido.");
            return "redirect:/pasarelaPagos?alojamientoId=" + alojamientoId + "&fechaInicio=" + fechaInicio
                    + "&fechaFin=" + fechaFin + "&total=0.0";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Capturamos las excepciones de validación que vienen del servicio
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pasarelaPagos?alojamientoId=" + alojamientoId + "&fechaInicio=" + fechaInicio
                    + "&fechaFin=" + fechaFin + "&total=0.0";
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
        org.springframework.data.domain.Page<com.scholarstay.app.model.Resena> paginaResenas = resenaService
                .obtenerResenasPaginadasPorAlojamiento(alojamientoId, pageable);

        model.addAttribute("usuario", usuario);
        model.addAttribute("alojamiento", alojamiento);
        model.addAttribute("resenas", paginaResenas.getContent()); // Enviamos solo las reseñas de esta página
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaResenas.getTotalPages());
        model.addAttribute("haySiguiente", paginaResenas.hasNext());
        model.addAttribute("hayAnterior", paginaResenas.hasPrevious());

        return "ver_todas_las_resenas";
    }

    @GetMapping("/eventos")
    public String eventos(HttpSession session, Model model) {

        Usuario usuario = getLoggedUser();

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("eventos", eventoService.listar());
        model.addAttribute(
                "misEventos",
                inscripcionEventoService.obtenerIdsEventosInscritos(usuario));

        return "eventos";
    }

    @GetMapping("/evento/accion/{id}")
    public String accionEvento(
            @PathVariable Long id,
            HttpSession session) {

        Usuario usuario = getLoggedUser();

        if (usuario == null) {
            return "redirect:/login";
        }

        Evento evento = eventoService.obtenerPorId(id);

        if (evento == null) {
            return "redirect:/eventos";
        }

        if (inscripcionEventoService.estaInscrito(usuario, evento)) {
            inscripcionEventoService.desinscribir(usuario, evento);
        } else {
            inscripcionEventoService.inscribir(usuario, evento);
        }

        return "redirect:/eventos";
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
