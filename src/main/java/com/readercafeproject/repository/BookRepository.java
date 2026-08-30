package com.readercafeproject.repository;

import com.readercafeproject.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
        // List<Book> findByTitleContainingIgnoreCase(String title);

        // စာအုပ်အားလုံးကို အသစ်ဆုံး တင်ထားသည့်အတိုင်း စီပေးမည်
        List<Book> findAllByOrderByIdDesc();

       //download count order
        List<Book> findAllByOrderByDownloadCountDesc();

        // search with title, author, genre
        List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCaseOrderByIdDesc(
                        String title, String author, String genre);

        // Pagination နှင့် အသစ်ဆုံး စာအုပ်များကို ခေါ်ယူခြင်း
        Page<Book> findAllByOrderByIdDesc(Pageable pageable);

        // Search လုပ်ထားချိန်တွင် Pagination ဖြင့် ခေါ်ယူခြင်း
        Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCaseOrderByIdDesc(
                        String title, String author, String genre, Pageable pageable);

        @Modifying
        @Transactional  
        @Query("UPDATE Book b SET b.downloadCount = COALESCE(b.downloadCount, 0) + 1 WHERE b.id = :bookId")
        void incrementDownloadCount(@Param("bookId") Long bookId);


        //စာအုပ်အားလုံးရဲ့ total sum of downloads
        @Query("SELECT SUM(b.downloadCount) FROM Book b")
        Integer getSumDownload();

}