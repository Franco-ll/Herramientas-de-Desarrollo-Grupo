package com.scholarstay.app.repository;

import com.scholarstay.app.model.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {

    @Query("SELECT c FROM Conversacion c WHERE " +
           "(c.usuario1.id = :uid1 AND c.usuario2.id = :uid2) OR " +
           "(c.usuario1.id = :uid2 AND c.usuario2.id = :uid1)")
    Optional<Conversacion> findByUsuarios(@Param("uid1") Long uid1, @Param("uid2") Long uid2);

    @Query("SELECT c FROM Conversacion c WHERE " +
           "(c.usuario1.id = :userId AND c.eliminadoPorUsuario1 = false) OR " +
           "(c.usuario2.id = :userId AND c.eliminadoPorUsuario2 = false) " +
           "ORDER BY c.fechaUltimoMensaje DESC NULLS LAST")
    List<Conversacion> findActivasByUsuarioId(@Param("userId") Long userId);
}
