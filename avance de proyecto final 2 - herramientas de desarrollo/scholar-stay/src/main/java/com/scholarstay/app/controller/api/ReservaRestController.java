package com.scholarstay.app.controller.api;

import com.scholarstay.app.dto.ReservaDTO;
import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
public class ReservaRestController {

    private final ReservaService reservaService;

    public ReservaRestController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<Reserva> reservas = reservaService.obtenerReservasPorUsuario(usuarioId);
        List<ReservaDTO> dtos = reservas.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/confirmar/{id}")
    public ResponseEntity<ReservaDTO> confirmar(@PathVariable Long id) {
        try {
            Reserva actualizada = reservaService.confirmarReserva(id);
            return ResponseEntity.ok(mapToDTO(actualizada));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ReservaDTO mapToDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setTituloAlojamiento(reserva.getAlojamiento().getTitulo());
        dto.setFechaInicio(reserva.getFechaInicio());
        dto.setFechaFin(reserva.getFechaFin());
        dto.setEstado(reserva.getEstado());
        return dto;
    }
}
