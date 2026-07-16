package com.scholarstay.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.GrupoMiembro;

@Repository
public interface GrupoMiembroRepository extends JpaRepository<GrupoMiembro, Long> {

    // Verifica si un usuario ya es miembro o seguidor de un grupo
    boolean existsByGrupoIdAndUsuarioId(Long grupoId, Long usuarioId);

    // Obtiene la membresía específica para ver el rol
    Optional<GrupoMiembro> findByGrupoIdAndUsuarioId(Long grupoId, Long usuarioId);

    // Lista todos los miembros de un grupo (excluye seguidores si se quiere)
    List<GrupoMiembro> findByGrupoId(Long grupoId);

    // Cuenta solo los MIEMBRO reales (no seguidores) para mostrar en el header
    @Query("SELECT COUNT(gm) FROM GrupoMiembro gm WHERE gm.grupo.id = :grupoId AND gm.rol = 'MIEMBRO'")
    long contarMiembros(@Param("grupoId") Long grupoId);

    // Cuenta seguidores
    @Query("SELECT COUNT(gm) FROM GrupoMiembro gm WHERE gm.grupo.id = :grupoId AND gm.rol = 'SEGUIDOR'")
    long contarSeguidores(@Param("grupoId") Long grupoId);

    // Obtiene los grupos a los que pertenece un usuario
    List<GrupoMiembro> findByUsuarioId(Long usuarioId);
}
