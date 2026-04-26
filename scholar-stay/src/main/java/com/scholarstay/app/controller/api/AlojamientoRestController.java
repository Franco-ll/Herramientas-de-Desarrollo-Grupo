package com.scholarstay.app.controller.api;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.service.AlojamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoRestController {

    private final AlojamientoService alojamientoService;

    public AlojamientoRestController(AlojamientoService alojamientoService) {
        this.alojamientoService = alojamientoService;
    }

    @GetMapping
    public ResponseEntity<List<Alojamiento>> listar() {
        return ResponseEntity.ok(alojamientoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alojamiento> obtener(@PathVariable Long id) {
        Alojamiento alojamiento = alojamientoService.obtenerPorId(id);
        if (alojamiento != null) {
            return ResponseEntity.ok(alojamiento);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Alojamiento> crear(@RequestBody Alojamiento alojamiento) {
        Alojamiento guardado = alojamientoService.save(alojamiento);
        return ResponseEntity.ok(guardado);
    }
}
