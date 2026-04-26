package com.scholarstay.app.controller.web;

import com.scholarstay.app.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scholarstay.app.model.Usuario;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class WebAuthController {

    private final AuthService authService;

    public WebAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        Usuario usuario = authService.authenticate(email, password);
        if (usuario != null) {
            session.setAttribute("loggedUser", usuario);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "iniciarSesion";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/register")
    public String performRegister(@ModelAttribute Usuario usuario, Model model, HttpSession session) {
        try {
            Usuario guardado = authService.register(usuario);
            session.setAttribute("loggedUser", guardado);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la cuenta: " + e.getMessage());
            return "CrearNuevaCuenta";
        }
    }
}
