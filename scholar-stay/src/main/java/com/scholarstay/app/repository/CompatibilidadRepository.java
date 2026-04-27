package com.scholarstay.app.repository;

import com.scholarstay.app.model.Compatibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompatibilidadRepository extends JpaRepository<Compatibilidad, Long> {
    List<Compatibilidad> findByUsuario1Id(Long usuario1Id);
    Compatibilidad findByUsuario1IdAndUsuario2Id(Long usuario1Id, Long usuario2Id);
}
