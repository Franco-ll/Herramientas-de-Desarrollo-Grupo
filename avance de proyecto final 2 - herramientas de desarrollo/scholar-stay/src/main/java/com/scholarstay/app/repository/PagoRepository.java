package com.scholarstay.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Busca todos los pagos asociados a las reservas de un usuario
    List<Pago> findByReservaUsuarioId(Long usuarioId);

    // Verifica si ya existe un pago para una reserva específica
    // Evita registrar pagos duplicados por la misma reserva
    boolean existsByReservaId(Long reservaId);

    // Busca el pago de una reserva específica
    Optional<Pago> findByReservaId(Long reservaId);
}
