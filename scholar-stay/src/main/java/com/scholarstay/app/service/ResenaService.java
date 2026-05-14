package com.scholarstay.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Resena;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.ResenaRepository;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final NotificacionService notificacionService;

    public ResenaService(ResenaRepository resenaRepository, NotificacionService notificacionService) {
        this.resenaRepository = resenaRepository;
        this.notificacionService = notificacionService;
    }

    public Resena crearResena(Resena resena, Usuario autor) {
        resena.setUsuario(autor);
        Resena guardada = resenaRepository.save(resena);
        
        // Notificar al anfitrión del alojamiento
        Usuario anfitrion = resena.getAlojamiento().getAnfitrion();
        if (anfitrion != null) {
            notificacionService.crearNotificacion(anfitrion, "Has recibido una nueva reseña en tu alojamiento: " + resena.getAlojamiento().getTitulo(), "RESENA");
        }

        return guardada;
    }

    public List<Resena> obtenerResenasPorAlojamiento(Long id){
    return resenaRepository.findByAlojamientoIdOrderByFechaPublicacionDesc(id);
}
}
