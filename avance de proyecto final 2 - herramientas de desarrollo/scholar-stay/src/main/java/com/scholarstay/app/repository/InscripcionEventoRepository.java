package com.scholarstay.app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scholarstay.app.model.InscripcionEvento;


import java.util.List;


public interface InscripcionEventoRepository 
extends JpaRepository<InscripcionEvento, Long>{



boolean existsByUsuarioIdAndEventoId(
        Long usuarioId,
        Long eventoId);



InscripcionEvento findByUsuarioIdAndEventoId(
        Long usuarioId,
        Long eventoId);



@Query("""
SELECT i.evento.id
FROM InscripcionEvento i
WHERE i.usuario.id = :usuarioId
""")
List<Long> findIdsEventosByUsuarioId(
        Long usuarioId
);


}