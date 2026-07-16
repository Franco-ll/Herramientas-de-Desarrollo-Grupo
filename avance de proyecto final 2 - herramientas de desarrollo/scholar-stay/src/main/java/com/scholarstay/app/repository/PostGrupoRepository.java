package com.scholarstay.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.PostGrupo;

@Repository
public interface PostGrupoRepository extends JpaRepository<PostGrupo, Long> {

    // Posts recientes — para el tab "Recientes" del muro
    @Query("SELECT p FROM PostGrupo p JOIN FETCH p.autor WHERE p.grupo.id = :grupoId ORDER BY p.fechaPublicacion DESC")
    List<PostGrupo> findByGrupoIdOrderByFechaPublicacionDesc(@Param("grupoId") Long grupoId);

    // Posts populares — para el tab "Populares" ordenados por likes
    @Query("SELECT p FROM PostGrupo p JOIN FETCH p.autor WHERE p.grupo.id = :grupoId ORDER BY p.likes DESC, p.fechaPublicacion DESC")
    List<PostGrupo> findByGrupoIdOrderByLikesDescFechaPublicacionDesc(@Param("grupoId") Long grupoId);

    // Cuenta posts publicados hoy para las métricas del header
    @Query("SELECT COUNT(p) FROM PostGrupo p WHERE p.grupo.id = :grupoId AND p.fechaPublicacion >= :desde")
    long contarPostsDesdeFecha(@Param("grupoId") Long grupoId, @Param("desde") LocalDateTime desde);
}
