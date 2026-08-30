package com.readercafeproject.repository;

import com.readercafeproject.model.Post;
import com.readercafeproject.model.PostLike;
import com.readercafeproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

 
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);

    List<PostLike> findByUser(User user);
    int countByPost(Post post);
}