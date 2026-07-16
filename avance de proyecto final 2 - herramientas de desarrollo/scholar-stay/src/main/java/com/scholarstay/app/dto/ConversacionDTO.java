package com.scholarstay.app.dto;

import java.time.LocalDateTime;

public class ConversacionDTO {

    private Long id;
    private Long otroUsuarioId;
    private String otroUsuarioNombre;
    private String otroUsuarioAvatar;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private long noLeidos;

    public ConversacionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOtroUsuarioId() { return otroUsuarioId; }
    public void setOtroUsuarioId(Long otroUsuarioId) { this.otroUsuarioId = otroUsuarioId; }

    public String getOtroUsuarioNombre() { return otroUsuarioNombre; }
    public void setOtroUsuarioNombre(String otroUsuarioNombre) { this.otroUsuarioNombre = otroUsuarioNombre; }

    public String getOtroUsuarioAvatar() { return otroUsuarioAvatar; }
    public void setOtroUsuarioAvatar(String otroUsuarioAvatar) { this.otroUsuarioAvatar = otroUsuarioAvatar; }

    public String getUltimoMensaje() { return ultimoMensaje; }
    public void setUltimoMensaje(String ultimoMensaje) { this.ultimoMensaje = ultimoMensaje; }

    public LocalDateTime getFechaUltimoMensaje() { return fechaUltimoMensaje; }
    public void setFechaUltimoMensaje(LocalDateTime fechaUltimoMensaje) { this.fechaUltimoMensaje = fechaUltimoMensaje; }

    public long getNoLeidos() { return noLeidos; }
    public void setNoLeidos(long noLeidos) { this.noLeidos = noLeidos; }
}
