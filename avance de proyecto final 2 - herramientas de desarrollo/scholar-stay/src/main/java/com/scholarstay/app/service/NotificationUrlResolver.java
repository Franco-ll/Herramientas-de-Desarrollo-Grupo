package com.scholarstay.app.service;

import org.springframework.stereotype.Component;

import com.scholarstay.app.model.enums.NotificationType;

@Component
public class NotificationUrlResolver {

    public String resolve(NotificationType type, Long entityId) {
        if (type == null || entityId == null) {
            return "/dashboard";
        }
        return switch (type) {
            case RESERVA          -> "/reciboDigital?reservaId=" + entityId;
            case PAGO             -> "/reciboDigital";
            case RESENA           -> "/alojamiento/" + entityId;
            case GRUPO            -> "/comunidad/grupos/" + entityId;
            case INVITACION_GRUPO -> "/comunidad/grupos/" + entityId;
            case MENSAJE          -> "/comunidad/chat?con=" + entityId;
        };
    }
}
