package com.scholarstay.app.dto;

import java.time.LocalDateTime;

public class MensajeDTO {

    private Long id;
    private Long conversacionId;
    private Long remitenteId;
    private String contenido;
    private LocalDateTime fecha;
    private boolean leido;
    private boolean esMio;

    public MensajeDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConversacionId() { return conversacionId; }
    public void setConversacionId(Long conversacionId) { this.conversacionId = conversacionId; }

    public Long getRemitenteId() { return remitenteId; }
    public void setRemitenteId(Long remitenteId) { this.remitenteId = remitenteId; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }

    public boolean isEsMio() { return esMio; }
    public void setEsMio(boolean esMio) { this.esMio = esMio; }
}
