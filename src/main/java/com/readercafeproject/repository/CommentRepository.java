package com.readercafeproject.repository;

import com.readercafeproject.model.Book;
import com.readercafeproject.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
 

import java.util.List;

 
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Book အလိုက် Comment များကို အသစ်ဆုံး ပထမဦးစားပေး ဆွဲထုတ်ရန်
    List<Comment> findByBookOrderByIdDesc(Book book);
}