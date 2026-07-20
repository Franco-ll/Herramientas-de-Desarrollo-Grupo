package com.scholarstay.app.model;

import com.scholarstay.app.model.enums.NotificationPriority;
import com.scholarstay.app.model.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;

    private Long entityId;

    private Boolean leido = false;

    private LocalDateTime fecha;

    private LocalDateTime fechaLeido;

    public Notificacion() {
        this.fecha = LocalDateTime.now();
    }

    public Notificacion(Usuario usuario, String mensaje, NotificationType type, NotificationPriority priority, Long entityId) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.type = type;
        this.priority = priority;
        this.entityId = entityId;
        this.leido = false;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public Boolean getLeido() { return leido; }
    public void setLeido(Boolean leido) { this.leido = leido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public LocalDateTime getFechaLeido() { return fechaLeido; }
    public void setFechaLeido(LocalDateTime fechaLeido) { this.fechaLeido = fechaLeido; }
}
