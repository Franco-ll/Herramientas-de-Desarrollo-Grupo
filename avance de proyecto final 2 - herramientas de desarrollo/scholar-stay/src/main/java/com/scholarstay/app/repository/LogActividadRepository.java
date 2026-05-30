package com.scholarstay.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.LogActividad;

@Repository
public interface LogActividadRepository extends JpaRepository<LogActividad, Long> {
    List<LogActividad> findAllByOrderByFechaDesc();
}