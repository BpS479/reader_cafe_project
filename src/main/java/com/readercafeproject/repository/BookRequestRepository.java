// package com.readercafeproject.repository;

// import com.readercafeproject.model.BookRequest;
// import org.springframework.data.jpa.repository.JpaRepository;

// public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
// }
package com.readercafeproject.repository;

import com.readercafeproject.model.BookRequest;
import com.readercafeproject.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
    List<BookRequest> findByUser(User user);
}