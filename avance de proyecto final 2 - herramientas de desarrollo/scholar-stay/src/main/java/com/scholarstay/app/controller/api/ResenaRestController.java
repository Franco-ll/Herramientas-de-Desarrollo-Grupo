package com.scholarstay.app.controller.api;

import com.scholarstay.app.dto.ResenaDTO;
import com.scholarstay.app.model.Resena;
import com.scholarstay.app.service.ResenaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resenas")
public class ResenaRestController {

    private final ResenaService resenaService;

    public ResenaRestController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping("/alojamiento/{alojamientoId}")
    public ResponseEntity<List<ResenaDTO>> listarPorAlojamiento(@PathVariable Long alojamientoId) {
        List<Resena> resenas = resenaService.obtenerResenasPorAlojamiento(alojamientoId);
        List<ResenaDTO> dtos = resenas.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private ResenaDTO mapToDTO(Resena resena) {
        ResenaDTO dto = new ResenaDTO();
        dto.setId(resena.getId());
        dto.setNombreUsuario(resena.getUsuario().getNombre());
        dto.setCalificacion(resena.getCalificacion());
        dto.setComentario(resena.getComentario());
        dto.setFechaPublicacion(resena.getFechaPublicacion());
        return dto;
    }
}
