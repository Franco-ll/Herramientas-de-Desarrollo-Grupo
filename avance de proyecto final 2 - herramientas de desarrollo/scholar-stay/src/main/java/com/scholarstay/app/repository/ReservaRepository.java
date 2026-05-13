package com.scholarstay.app.repository;

import com.scholarstay.app.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);

    // Verificamos si existe alguna reserva confirmada para el alojamiento en las fechas solicitadas
    // Esto previene que dos usuarios reserven el mismo lugar al mismo tiempo
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r " +
           "WHERE r.alojamiento.id = :alojamientoId " +
           "AND r.estado = 'CONFIRMADA' " +
           "AND (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio)")
    boolean existeSuperposicion(
            @Param("alojamientoId") Long alojamientoId, 
            @Param("fechaInicio") LocalDate fechaInicio, 
            @Param("fechaFin") LocalDate fechaFin);

    // Método para validar que un usuario haya tenido una reserva confirmada en un alojamiento
    // Se usa antes de permitirle publicar una reseña
    boolean existsByUsuarioIdAndAlojamientoIdAndEstado(Long usuarioId, Long alojamientoId, String estado);
}
