package com.scholarstay.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scholarstay.app.model.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndUsuarioId(Long postId, Long usuarioId);

    void deleteByPostIdAndUsuarioId(Long postId, Long usuarioId);

    long countByPostId(Long postId);

    List<PostLike> findByUsuarioIdAndPostIdIn(Long usuarioId, List<Long> postIds);

    void deleteByPostId(Long postId);
}
