package com.scholarstay.app.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.service.AuthService;

@Controller
public class WebAuthController {

    private final AuthService authService;

    public WebAuthController(AuthService authService) {
        this.authService = authService;
    }

    // Spring Security maneja automáticamente el POST /login y /logout
    // Solo necesitamos los controladores de vista GET para mostrar los formularios (están en WebViewController)

    @PostMapping("/register")
    public String performRegister(@ModelAttribute Usuario usuario, Model model) {
        try {
            // AuthService ahora se encarga de cifrar la contraseña con BCrypt
            authService.register(usuario);
            // Redirigimos al login con mensaje de éxito para que inicie sesión con seguridad
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la cuenta. Intente con otro email.");
            return "CrearNuevaCuenta";
        }
    }
}
