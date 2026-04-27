package com.scholarstay.app.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scholarstay.app.dto.CompatibilidadDTO;
import com.scholarstay.app.model.Compatibilidad;
import com.scholarstay.app.service.CompatibilidadService;

@RestController
@RequestMapping("/api/compatibilidad")
public class CompatibilidadRestController {

    private final CompatibilidadService compatibilidadService;

    public CompatibilidadRestController(CompatibilidadService compatibilidadService) {
        this.compatibilidadService = compatibilidadService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CompatibilidadDTO>> obtenerCompatibilidades(@PathVariable Long usuarioId) {
        List<Compatibilidad> compatibilidades = compatibilidadService.obtenerCompatibilidades(usuarioId);
        
        List<CompatibilidadDTO> dtos = compatibilidades.stream().map(c -> {
            CompatibilidadDTO dto = new CompatibilidadDTO();
            dto.setUsuarioId(c.getUsuario2().getId());
            dto.setNombreUsuario(c.getUsuario2().getNombre()); // Corregido: getName -> getNombre
            dto.setPorcentaje(c.getPorcentaje());
            dto.setCriteriosEvaluados(c.getCriteriosEvaluados());
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
}
