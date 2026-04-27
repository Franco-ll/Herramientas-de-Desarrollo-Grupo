package com.scholarstay.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "compatibilidades")
public class Compatibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario1_id", nullable = false)
    private Usuario usuario1;

    @ManyToOne
    @JoinColumn(name = "usuario2_id", nullable = false)
    private Usuario usuario2;

    private Double porcentaje;
    
    @Column(columnDefinition = "TEXT")
    private String criteriosEvaluados; // e.g. JSON or text listing what matched

    public Compatibilidad() {}

    public Compatibilidad(Usuario usuario1, Usuario usuario2, Double porcentaje, String criteriosEvaluados) {
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.porcentaje = porcentaje;
        this.criteriosEvaluados = criteriosEvaluados;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario1() { return usuario1; }
    public void setUsuario1(Usuario usuario1) { this.usuario1 = usuario1; }

    public Usuario getUsuario2() { return usuario2; }
    public void setUsuario2(Usuario usuario2) { this.usuario2 = usuario2; }

    public Double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }

    public String getCriteriosEvaluados() { return criteriosEvaluados; }
    public void setCriteriosEvaluados(String criteriosEvaluados) { this.criteriosEvaluados = criteriosEvaluados; }
}
