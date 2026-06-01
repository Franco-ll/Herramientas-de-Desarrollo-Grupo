package com.scholarstay.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    List<Grupo> findByCarreraContainingIgnoreCase(String carrera);
    List<Grupo> findByInteresesContainingIgnoreCase(String interes);
}
