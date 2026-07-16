package com.scholarstay.app.dto;

import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import java.time.LocalDateTime;

public class NotificacionDTO {
    private Long id;
    private String mensaje;
    private NotificationType type;
    private NotificationPriority priority;
    private Long entityId;
    private String redirectUrl;
    private Boolean leido;
    private LocalDateTime fecha;
    private LocalDateTime fechaLeido;
    private String cssClass;

    public NotificacionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public Boolean getLeido() { return leido; }
    public void setLeido(Boolean leido) { this.leido = leido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public LocalDateTime getFechaLeido() { return fechaLeido; }
    public void setFechaLeido(LocalDateTime fechaLeido) { this.fechaLeido = fechaLeido; }

    public String getCssClass() { return cssClass; }
    public void setCssClass(String cssClass) { this.cssClass = cssClass; }
}
