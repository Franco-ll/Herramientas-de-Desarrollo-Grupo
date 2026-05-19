package com.scholarstay.app.dto;

import java.time.LocalDateTime;

public class NotificacionDTO {
    private Long id;
    private String mensaje;
    private String tipo;
    private Boolean leido;
    private LocalDateTime fecha;

    public NotificacionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Boolean getLeido() { return leido; }
    public void setLeido(Boolean leido) { this.leido = leido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
