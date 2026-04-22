package com.scholarstay.app.controller.web;

import com.scholarstay.app.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebAuthController {

    private final AuthService authService;

    public WebAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam String email, @RequestParam String password, Model model) {
        if (authService.authenticate(email, password)) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "iniciarSesion";
        }
    }
}
