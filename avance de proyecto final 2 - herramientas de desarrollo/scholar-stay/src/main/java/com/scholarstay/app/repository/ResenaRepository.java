package com.scholarstay.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.Resena;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    // Usamos Pageable para permitir cargar las reseñas de manera paginada,
    // mejorando el rendimiento en alojamientos con cientos de reseñas
    Page<Resena> findByAlojamientoIdOrderByFechaPublicacionDesc(Long alojamientoId, Pageable pageable);
    
    // Mantenemos el método antiguo por si se necesita para listas pequeñas sin paginar
    List<Resena> findByAlojamientoIdOrderByFechaPublicacionDesc(Long alojamientoId);
}
