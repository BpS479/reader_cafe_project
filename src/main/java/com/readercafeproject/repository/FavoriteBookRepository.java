package com.readercafeproject.repository;

import com.readercafeproject.model.FavoriteBook;
import com.readercafeproject.model.User;
import com.readercafeproject.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {

    // User မူတည်ပြီး Favorite စာအုပ်များ ခေါ်ယူရန်
    List<FavoriteBook> findByUserOrderByIdDesc(User user);

    

    // User က ဒီစာအုပ်ကို Favorite လုပ်ထားပြီးပြီလား စစ်ရန်
    boolean existsByUserAndBook(User user, Book book);

    // Favorite ဖြုတ်ရန် (Delete)
    Optional<FavoriteBook> findByUserAndBook(User user, Book book);

    List<FavoriteBook> findByUser(User user);
}