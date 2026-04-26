package com.scholarstay.app.repository;

import com.scholarstay.app.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByAlojamientoId(Long alojamientoId);
}
