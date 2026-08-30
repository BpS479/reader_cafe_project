package com.readercafeproject.repository;

import com.readercafeproject.model.Post;
import com.readercafeproject.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
import java.util.List;

 
public interface PostRepository extends JpaRepository<Post, Long> {
    // Feed မှာ အသစ်ဆုံး Post များကို အပေါ်မှာ ပြရန်
    List<Post> findAllByOrderByIdDesc();
    List<Post> findByUserOrderByIdDesc(User user);
}