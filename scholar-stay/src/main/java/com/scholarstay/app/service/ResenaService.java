package com.scholarstay.app.service;

import com.scholarstay.app.model.Resena;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Resena> obtenerResenasPorAlojamiento(Long alojamientoId) {
        return resenaRepository.findByAlojamientoId(alojamientoId);
    }
}
