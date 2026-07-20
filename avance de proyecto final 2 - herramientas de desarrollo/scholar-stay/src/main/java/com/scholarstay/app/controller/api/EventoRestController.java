package com.scholarstay.app.controller.api;

import com.scholarstay.app.dto.EventoDTO;
import com.scholarstay.app.model.Evento;
import com.scholarstay.app.service.EventoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoRestController {

    private final EventoService eventoService;

    public EventoRestController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(eventoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerEvento(@PathVariable Long id) {

        Evento evento = eventoService.obtenerPorId(id);

        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(evento);
    }

    @PostMapping
    public ResponseEntity<Evento> crearEvento(
            @RequestBody EventoDTO dto) {

        Evento evento = eventoService.crear(dto);

        return ResponseEntity.ok(evento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizarEvento(
            @PathVariable Long id,
            @RequestBody EventoDTO dto) {

        Evento evento = eventoService.actualizar(id, dto);

        return ResponseEntity.ok(evento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(
            @PathVariable Long id) {

        eventoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}