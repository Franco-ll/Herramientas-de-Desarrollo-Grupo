package com.scholarstay.app.repository;

import com.scholarstay.app.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findByConversacionIdOrderByFechaAsc(Long conversacionId);

    @Modifying
    @Query("UPDATE Mensaje m SET m.leido = true WHERE m.conversacion.id = :conversacionId AND m.remitente.id != :usuarioId AND m.leido = false")
    int marcarLeidosPorConversacion(@Param("conversacionId") Long conversacionId, @Param("usuarioId") Long usuarioId);

    long countByConversacionIdAndLeidoFalseAndRemitenteIdNot(Long conversacionId, Long remitenteId);

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.conversacion.id IN " +
           "(SELECT c.id FROM Conversacion c WHERE " +
           "(c.usuario1.id = :userId AND c.eliminadoPorUsuario1 = false) OR " +
           "(c.usuario2.id = :userId AND c.eliminadoPorUsuario2 = false)) " +
           "AND m.remitente.id != :userId AND m.leido = false")
    long countNoLeidosByUsuarioId(@Param("userId") Long userId);
}
