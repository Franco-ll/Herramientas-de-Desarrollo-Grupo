package com.scholarstay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.Alojamiento;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {
}
