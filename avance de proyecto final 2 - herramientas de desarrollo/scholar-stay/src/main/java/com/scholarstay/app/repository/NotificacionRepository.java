package com.scholarstay.app.repository;

import com.scholarstay.app.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
    List<Notificacion> findByUsuarioIdAndLeidoFalseOrderByFechaDesc(Long usuarioId);

    long countByUsuarioIdAndLeidoFalse(Long usuarioId);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leido = true, n.fechaLeido = CURRENT_TIMESTAMP WHERE n.usuario.id = :usuarioId AND n.leido = false")
    void marcarTodasComoLeidas(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("DELETE FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.leido = true")
    void eliminarLeidas(@Param("usuarioId") Long usuarioId);
}
