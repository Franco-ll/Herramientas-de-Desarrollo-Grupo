package com.scholarstay.app.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/comunidad")
public class ComunidadController {

    // Se inyectará el ComunidadService cuando se implemente la lógica en la siguiente fase
    // private final ComunidadService comunidadService;

    @GetMapping({"", "/", "/inicio"})
    public String inicio(HttpSession session) {
        // En esta fase solo retornamos la vista estática
        return "comunidad/inicio";
    }

    @GetMapping("/matches")
    public String matches(HttpSession session) {
        return "comunidad/lista_coincidencia";
    }

    @GetMapping("/chat")
    public String chat(HttpSession session) {
        return "comunidad/chat_mensajes";
    }

    @GetMapping("/grupos")
    public String grupos(HttpSession session) {
        return "comunidad/grupo_comunidad";
    }

    @GetMapping("/invitacion")
    public String invitacion(HttpSession session) {
        return "comunidad/invitacion_comunidad";
    }

    @GetMapping("/perfil-verificado")
    public String perfilVerificado(HttpSession session) {
        return "comunidad/perfil_verificado";
    }

    @GetMapping("/recursos")
    public String recursos(HttpSession session) {
        return "comunidad/recursos_comunidad";
    }
}
