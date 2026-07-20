package com.scholarstay.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.PostGuardado;

@Repository
public interface PostGuardadoRepository extends JpaRepository<PostGuardado, Long> {

    boolean existsByPostIdAndUsuarioId(Long postId, Long usuarioId);

    void deleteByPostIdAndUsuarioId(Long postId, Long usuarioId);

    List<PostGuardado> findByUsuarioIdAndPostIdIn(Long usuarioId, List<Long> postIds);

    void deleteByPostId(Long postId);
}
