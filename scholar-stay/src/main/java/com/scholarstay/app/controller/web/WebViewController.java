package com.scholarstay.app.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @GetMapping("/")
    public String index() {
        return "explorarResidencias"; // vista por defecto
    }

    @GetMapping("/login")
    public String loginPage() {
        return "iniciarSesion";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "CrearNuevaCuenta";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "explorarResidencias";
    }

    @GetMapping("/accommodation/detail")
    public String accommodationDetail() {
        return "detalleAlojamiento";
    }

    @GetMapping("/profile")
    public String profile() {
        return "editarperfilEscolar";
    }
    
    @GetMapping("/reviews")
    public String reviews() {
        return "ver_Todas_las_Resenas";
    }
}
