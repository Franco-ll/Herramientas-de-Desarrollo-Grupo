package com.scholarstay.app.controller.api;

import com.scholarstay.app.model.Rol;
import com.scholarstay.app.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolRestController {

    private final RolService rolService;

    public RolRestController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(rolService.listar());
    }

    @PostMapping
    public ResponseEntity<Rol> crear(@RequestBody Rol rol) {
        Rol guardado = rolService.crear(rol);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }
}
