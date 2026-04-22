package com.scholarstay.app.controller.api;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.service.AlojamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
public class AlojamientoRestController {

    private final AlojamientoService alojamientoService;

    public AlojamientoRestController(AlojamientoService alojamientoService) {
        this.alojamientoService = alojamientoService;
    }

    @GetMapping
    public List<Alojamiento> getAll() {
        return alojamientoService.getAllAlojamientos();
    }

    @PostMapping
    public ResponseEntity<Alojamiento> create(@RequestBody Alojamiento alojamiento) {
        Alojamiento saved = alojamientoService.save(alojamiento);
        return ResponseEntity.ok(saved);
    }
}
