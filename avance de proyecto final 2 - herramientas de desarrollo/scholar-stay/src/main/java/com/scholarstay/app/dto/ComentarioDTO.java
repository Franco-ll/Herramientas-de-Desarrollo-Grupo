package com.scholarstay.app.dto;

public class ComentarioDTO {

    private Long id;
    private Long autorId;
    private String autorNombre;
    private String autorAvatar;
    private String contenido;
    private String fechaRelativa;

    public ComentarioDTO() {}

    public ComentarioDTO(Long id, Long autorId, String autorNombre, String autorAvatar,
                         String contenido, String fechaRelativa) {
        this.id = id;
        this.autorId = autorId;
        this.autorNombre = autorNombre;
        this.autorAvatar = autorAvatar;
        this.contenido = contenido;
        this.fechaRelativa = fechaRelativa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAutorId() { return autorId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }

    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }

    public String getAutorAvatar() { return autorAvatar; }
    public void setAutorAvatar(String autorAvatar) { this.autorAvatar = autorAvatar; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getFechaRelativa() { return fechaRelativa; }
    public void setFechaRelativa(String fechaRelativa) { this.fechaRelativa = fechaRelativa; }
}
