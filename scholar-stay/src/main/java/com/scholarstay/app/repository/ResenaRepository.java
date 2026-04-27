package com.scholarstay.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
List<Resena> findByAlojamientoIdOrderByFechaPublicacionDesc(Long alojamientoId);}
