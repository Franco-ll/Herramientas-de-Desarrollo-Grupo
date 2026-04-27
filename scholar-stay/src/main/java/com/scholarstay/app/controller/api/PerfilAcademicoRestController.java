package com.scholarstay.app.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scholarstay.app.dto.PerfilAcademicoDTO;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.service.PerfilAcademicoService;

@RestController
@RequestMapping("/api/perfil-academico")
public class PerfilAcademicoRestController {

    private final PerfilAcademicoService perfilAcademicoService;

    public PerfilAcademicoRestController(PerfilAcademicoService perfilAcademicoService) {
        this.perfilAcademicoService = perfilAcademicoService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<PerfilAcademicoDTO> obtenerPerfil(@PathVariable Long usuarioId) {
        PerfilAcademico perfil = perfilAcademicoService.obtenerPorUsuario(usuarioId);
        if (perfil != null) {
            PerfilAcademicoDTO dto = mapToDTO(perfil);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<PerfilAcademicoDTO> actualizarPerfil(@PathVariable Long usuarioId, @RequestBody PerfilAcademicoDTO dto) {
        try {
            PerfilAcademico actualizado = perfilAcademicoService.actualizarPerfil(usuarioId, dto);
            return ResponseEntity.ok(mapToDTO(actualizado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private PerfilAcademicoDTO mapToDTO(PerfilAcademico perfil) {
        PerfilAcademicoDTO dto = new PerfilAcademicoDTO();
        dto.setCarrera(perfil.getCarrera());
        dto.setHorarioEstudio(perfil.getHorarioEstudio());
        dto.setHabitosRuido(perfil.getHabitosRuido());
        dto.setHabitosSueno(perfil.getHabitosSueno());
        dto.setNivelTolerancia(perfil.getNivelTolerancia());
        return dto;
    }
}