package com.example.demo.controller;

import com.example.demo.model.Perfil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PerfilController {

    private Perfil perfilGuardado = new Perfil();

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        model.addAttribute("perfil", perfilGuardado);
        return "perfil";
    }

    @PostMapping("/perfil")
    public String guardarPerfil(@ModelAttribute Perfil perfil) {
        this.perfilGuardado = perfil;
        return "redirect:/perfil";
    }
}
