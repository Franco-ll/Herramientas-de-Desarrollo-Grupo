package com.scholarstay.app.repository;

import com.scholarstay.app.model.PerfilAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilAcademicoRepository extends JpaRepository<PerfilAcademico, Long> {
    PerfilAcademico findByUsuarioId(Long usuarioId);
}
