package com.scholarstay.app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "alojamientos")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double pricePerMonth;

    private String address;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    private List<Resena> resenas;

    public Alojamiento() {}

    public Alojamiento(String title, String description, Double pricePerMonth, String address) {
        this.title = title;
        this.description = description;
        this.pricePerMonth = pricePerMonth;
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPricePerMonth() { return pricePerMonth; }
    public void setPricePerMonth(Double pricePerMonth) { this.pricePerMonth = pricePerMonth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }

    public List<Resena> getResenas() { return resenas; }
    public void setResenas(List<Resena> resenas) { this.resenas = resenas; }
}
