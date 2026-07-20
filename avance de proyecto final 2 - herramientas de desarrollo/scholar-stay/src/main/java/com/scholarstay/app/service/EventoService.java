package com.scholarstay.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.scholarstay.app.dto.EventoDTO;
import com.scholarstay.app.model.Evento;
import com.scholarstay.app.repository.EventoRepository;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> listar() {
        return eventoRepository.findAll();
    }

    public Evento obtenerPorId(Long id) {
        Optional<Evento> evento = eventoRepository.findById(id);
        return evento.orElse(null);
    }

    public Evento crear(EventoDTO dto) {

        Evento evento = new Evento();

        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFecha(dto.getFecha());
        evento.setHora(dto.getHora());
        evento.setUbicacion(dto.getUbicacion());
        evento.setCategoria(dto.getCategoria());
        evento.setPlazas(dto.getPlazas());
        evento.setImagen(dto.getImagen());

        return eventoRepository.save(evento);
    }

    public Evento actualizar(Long id, EventoDTO dto) {

        Evento evento = obtenerPorId(id);

        if (evento == null) {
            throw new RuntimeException("Evento no encontrado");
        }

        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFecha(dto.getFecha());
        evento.setHora(dto.getHora());
        evento.setUbicacion(dto.getUbicacion());
        evento.setCategoria(dto.getCategoria());
        evento.setPlazas(dto.getPlazas());
        evento.setImagen(dto.getImagen());

        return eventoRepository.save(evento);
    }

    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }
}