package com.scholarstay.app.dto;

public class CompatibilidadDTO {
    private Long usuarioId;
    private String nombreUsuario;
    private Double porcentaje;
    private String criteriosEvaluados;

    public CompatibilidadDTO() {}

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public Double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }

    public String getCriteriosEvaluados() { return criteriosEvaluados; }
    public void setCriteriosEvaluados(String criteriosEvaluados) { this.criteriosEvaluados = criteriosEvaluados; }
}
