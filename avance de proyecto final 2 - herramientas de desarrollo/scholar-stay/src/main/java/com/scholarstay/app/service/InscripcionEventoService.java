package com.scholarstay.app.service;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Evento;
import com.scholarstay.app.model.InscripcionEvento;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.InscripcionEventoRepository;

import java.util.List;

@Service
public class InscripcionEventoService {

    private final InscripcionEventoRepository repository;

    private final NotificacionService notificacionService;


    public InscripcionEventoService(
            InscripcionEventoRepository repository,
            NotificacionService notificacionService) {

        this.repository = repository;
        this.notificacionService = notificacionService;

    }



    public InscripcionEvento inscribir(
            Usuario usuario,
            Evento evento) {


        if (repository.existsByUsuarioIdAndEventoId(
                usuario.getId(),
                evento.getId())) {


            throw new RuntimeException(
                    "Ya estás inscrito en este evento"
            );

        }


        InscripcionEvento inscripcion =
                new InscripcionEvento();


        inscripcion.setUsuario(usuario);
        inscripcion.setEvento(evento);



        InscripcionEvento guardada =
                repository.save(inscripcion);



        // CREAR NOTIFICACION
        notificacionService.crearNotificacion(
                usuario,
                "Te inscribiste al evento: "
                + evento.getTitulo(),
                "EVENTO"
        );



        return guardada;

    }



    public void desinscribir(
            Usuario usuario,
            Evento evento) {


        InscripcionEvento inscripcion =
                repository.findByUsuarioIdAndEventoId(
                        usuario.getId(),
                        evento.getId()
                );


        if(inscripcion != null){


            repository.delete(inscripcion);



            // NOTIFICACION AL CANCELAR
            notificacionService.crearNotificacion(
                    usuario,
                    "Cancelaste tu inscripción al evento: "
                    + evento.getTitulo(),
                    "EVENTO"
            );


        }

    }



    public List<Long> obtenerIdsEventosInscritos(
            Usuario usuario) {


        return repository.findIdsEventosByUsuarioId(
                usuario.getId()
        );

    }



    public boolean estaInscrito(
            Usuario usuario,
            Evento evento) {


        return repository.existsByUsuarioIdAndEventoId(
                usuario.getId(),
                evento.getId()
        );

    }

}