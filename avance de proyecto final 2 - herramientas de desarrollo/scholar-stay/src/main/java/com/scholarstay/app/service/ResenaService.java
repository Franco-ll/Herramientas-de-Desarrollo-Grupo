package com.scholarstay.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Resena;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import com.scholarstay.app.repository.ResenaRepository;

import com.scholarstay.app.repository.ReservaRepository;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ReservaRepository reservaRepository;
    private final NotificacionService notificacionService;

    public ResenaService(ResenaRepository resenaRepository, ReservaRepository reservaRepository, NotificacionService notificacionService) {
        this.resenaRepository = resenaRepository;
        this.reservaRepository = reservaRepository;
        this.notificacionService = notificacionService;
    }

    public Resena crearResena(Resena resena, Usuario autor) {
        // Validación de campos nulos
        if (resena.getCalificacion() == null) {
            throw new IllegalArgumentException("La calificación es obligatoria.");
        }
        
        // Validación de rango de calificación
        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5.");
        }

        // Validación de comentario en blanco o vacío
        if (resena.getComentario() == null || resena.getComentario().trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }
        
        // Validación de longitud del comentario para evitar spam masivo o insuficiente info
        if (resena.getComentario().trim().length() < 10 || resena.getComentario().trim().length() > 1000) {
            throw new IllegalArgumentException("El comentario debe tener entre 10 y 1000 caracteres.");
        }

        // Validación de lógica de negocio: solo usuarios que han tenido reserva confirmada pueden comentar
        boolean tieneReserva = reservaRepository.existsByUsuarioIdAndAlojamientoIdAndEstado(
            autor.getId(), resena.getAlojamiento().getId(), "CONFIRMADA"
        );
        if (!tieneReserva) {
            throw new IllegalStateException("Solo puedes dejar una reseña si has completado una reserva en este alojamiento.");
        }

        // Sanitización básica del input para prevenir XSS simple
        // En lugar de guardar scripts, reemplazamos los caracteres especiales HTML
        String comentarioSanitizado = resena.getComentario().trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;");
        resena.setComentario(comentarioSanitizado);

        resena.setUsuario(autor);
        Resena guardada = resenaRepository.save(resena);
        
        // Notificar al anfitrión del alojamiento
        Usuario anfitrion = resena.getAlojamiento().getAnfitrion();
        if (anfitrion != null) {
            notificacionService.crearNotificacion(anfitrion, "Has recibido una nueva reseña en tu alojamiento: " + resena.getAlojamiento().getTitulo(), NotificationType.RESENA, NotificationPriority.INFO, resena.getAlojamiento().getId());
        }

        return guardada;
    }

    public List<Resena> obtenerResenasPorAlojamiento(Long id){
        return resenaRepository.findByAlojamientoIdOrderByFechaPublicacionDesc(id);
    }
    
    // Agregamos un método para obtener reseñas con paginación
    // Esto optimiza la carga cuando hay cientos de comentarios
    public org.springframework.data.domain.Page<Resena> obtenerResenasPaginadasPorAlojamiento(Long id, org.springframework.data.domain.Pageable pageable) {
        return resenaRepository.findByAlojamientoIdOrderByFechaPublicacionDesc(id, pageable);
    }
}
