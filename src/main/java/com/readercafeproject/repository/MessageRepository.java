package com.readercafeproject.repository;

import com.readercafeproject.model.Message;
import com.readercafeproject.model.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // User A နဲ့ User B ကြားက အပြန်အလှန် စာများကို အချိန်စဉ်အတိုင်း ယူရန်
    // @Query("SELECT m FROM Message m WHERE (m.sender = :u1 AND m.receiver = :u2)
    // OR (m.sender = :u2 AND m.receiver = :u1) ORDER BY m.timestamp ASC")
    // List<Message> findChatHistory(@Param("u1") User u1, @Param("u2") User u2);
    // User A နဲ့ User B ကြား စကားပြောထားသည့် စာများကို အချိန်စဉ်အတိုင်း ဆွဲယူရန်
    @Query("SELECT m FROM Message m WHERE (m.sender = :u1 AND m.receiver = :u2) OR (m.sender = :u2 AND m.receiver = :u1) ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(@Param("u1") User u1, @Param("u2") User u2);


    
    // Admin ထံ စာပို့ထားဖူးသော (သို့မဟုတ်) Admin နှင့် စကားပြောထားဖူးသော User List
    // ကို ဆွဲယူရန်
    @Query("SELECT DISTINCT u FROM User u WHERE u IN " +
            "(SELECT m.sender FROM Message m WHERE m.receiver = :admin) " +
            "OR u IN (SELECT m.receiver FROM Message m WHERE m.sender = :admin)")
    List<User> findUsersWhoMessagedAdmin(@Param("admin") User admin);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE (m.sender = :u1 AND m.receiver = :u2) OR (m.sender = :u2 AND m.receiver = :u1)")
    void deleteChatHistory(@Param("u1") User u1, @Param("u2") User u2);
}