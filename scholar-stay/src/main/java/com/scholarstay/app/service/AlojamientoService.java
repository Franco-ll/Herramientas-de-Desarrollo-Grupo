package com.scholarstay.app.service;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.repository.AlojamientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;

    public AlojamientoService(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public List<Alojamiento> listar() {
        return alojamientoRepository.findAll();
    }
    
    public Alojamiento obtenerPorId(Long id) {
        return alojamientoRepository.findById(id).orElse(null);
    }
    
    public Alojamiento save(Alojamiento alojamiento) {
        return alojamientoRepository.save(alojamiento);
    }
}
