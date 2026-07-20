package com.scholarstay.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.PostComentario;

@Repository
public interface PostComentarioRepository extends JpaRepository<PostComentario, Long> {

    List<PostComentario> findByPostIdOrderByFechaCreacionAsc(Long postId);

    long countByPostId(Long postId);

    List<PostComentario> findByPostIdIn(List<Long> postIds);

    void deleteByPostId(Long postId);
}
