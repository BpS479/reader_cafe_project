package com.readercafeproject.repository;

import com.readercafeproject.model.Post;
import com.readercafeproject.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;

 
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    // Parent မရှိသော Main Comments များကိုသာ ရှာယူရန်
    List<PostComment> findByPostAndParentIsNullOrderByIdDesc(Post post);
}