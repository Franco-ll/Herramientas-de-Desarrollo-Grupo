package com.scholarstay.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.LogActividad;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.LogActividadRepository;

@Service
public class LogService {

    private final LogActividadRepository logRepository;

    public LogService(LogActividadRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void registrar(Usuario admin, String accion, String detalle) {
        if (admin == null) return;
        LogActividad log = new LogActividad(admin.getNombre(), admin.getEmail(), accion, detalle);
        logRepository.save(log);
    }

    public List<LogActividad> obtenerTodos() {
        return logRepository.findAllByOrderByFechaDesc();
    }
}