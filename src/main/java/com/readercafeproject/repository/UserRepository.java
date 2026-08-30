package com.readercafeproject.repository;

import com.readercafeproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  
       

        Optional<User> findByEmail(String email);
         
//email မထပ်အောင်စစ်ဖို့
        boolean existsByEmail(String email);

//username မထပ်အောင်စစ်ဖို့
        Optional<User> findByUsername(String username);
        boolean existsByUsername(String username);

        // Role အလိုက် User များကို ရှာရန် (ဥပမာ - "ROLE_USER" သို့မဟုတ် "USER")
        List<User> findByRole(String role);

        // နာမည် သို့မဟုတ် Email ဖြင့် Search ပေးရန်
        List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByIdDesc(String name, String email);

        // // နာမည် သို့မဟုတ် Email or Department ဖြင့် Search ပေးရန်
        // List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByIdDesc(
        //                 String name, String email, String department);

        // User အားလုံးကို အသစ်ဆုံး အကောင့်ဖွင့်ထားသည့်အတိုင်း စီပေးမည်
        List<User> findAllByOrderByIdDesc();

        // Pagination နှင့် အသစ်ဆုံး စာအုပ်များကို ခေါ်ယူခြင်း
        Page<User> findAllByOrderByIdDesc(Pageable pageable);

        // Search လုပ်ထားချိန်တွင် Pagination ဖြင့် ခေါ်ယူခြင်း
        Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRoleContainingIgnoreCaseOrderByIdDesc(
                        String name, String email, String role, Pageable pageable);


}