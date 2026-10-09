package com.readercafeproject.controller;

import com.readercafeproject.dto.UserRegistrationDto;
import com.readercafeproject.model.Book;
import com.readercafeproject.model.User;
import com.readercafeproject.repository.BookRepository;
import com.readercafeproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Controller
@RequiredArgsConstructor // <--- final field များကို အလိုအလျောက် Inject လုပ်ပေးပါမည်
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    // Login Page
    // @GetMapping("/login")
    // public String showLoginPage() {
    // return "login";
    // }
   @GetMapping("/")
   public String showIndexPage() {
        return "redirect:/login";
   }
    // @GetMapping("/home")
    // public String showHomePage() {
    //     return "home";
    // }

    // for student login
    // @GetMapping("/login")
    // public String showLoginPage() {
    //     return "home";
    // }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model,
            Principal principal) { // <--- Principal ပါဝင်ရမည်
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // တကယ်လို့ User က Anonymous မဟုတ်ဘေ Login ဝင်ထားပြီးသားဆိုရင် Dashboard ကို
        // တန်းပို့မယ်
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/admin-dashboard";
            }
            return "redirect:/user/user-dashboard";
        }

        // long totalEbooks = bookRepository.count();
        int pageSize = 8;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Book> bookPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            bookPage = bookRepository
                    .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCaseOrderByIdDesc(
                            keyword, keyword, keyword, pageable);
        } else {
            bookPage = bookRepository.findAllByOrderByIdDesc(pageable);
        }

       

        // model.addAttribute("totalEbooks", totalEbooks);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        

        return "home";
    }

    // for admin login
    @GetMapping("/admin-login")
    public String showAdminLoginPage() {
        return "admin-login";
    }

    // Register Page Form Show
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register";
    }

    // Register Submit
    // @PostMapping("/register")
    // public String registerUser(@ModelAttribute("userDto") UserRegistrationDto
    // dto, Model model) {

    // if (!dto.getPassword().equals(dto.getConfirmPassword())) {
    // model.addAttribute("error", "Passwords do not match!");
    // return "register";
    // }

    // if (userRepository.existsByEmail(dto.getEmail())) {
    // model.addAttribute("error", "Email is already registered!");
    // return "register";
    // }

    // User user = new User();
    // user.setName(dto.getName());
    // user.setEmail(dto.getEmail());
    // user.setPassword(passwordEncoder.encode(dto.getPassword()));
    // user.setRole("ROLE_USER");

    // userRepository.save(user);

    // return "redirect:/login?registered";
    // }
    @PostMapping("/register")
public String registerUser(@ModelAttribute("userDto") UserRegistrationDto dto, Model model) {

    if (userRepository.existsByUsername(dto.getUsername())) {
        model.addAttribute("userNameError", "User Name is already registered!");
        return "home"; // <-- Modal ပြန်ပွင့်အောင် home ကို ပြန်လှည့်ပေးပါ
    }
    if (userRepository.existsByEmail(dto.getEmail())) {
        model.addAttribute("emailError", "Email is already registered!");
        return "home"; 
    }

    if (dto.getPassword() == null || !dto.getPassword().equals(dto.getConfirmPassword())) {
        model.addAttribute("comfirmPasswordError", "Passwords do not match!");
        return "home"; 
    }         

    User user = new User();
    user.setName(dto.getName());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRole("ROLE_USER");

    userRepository.save(user);

    return "redirect:/login?registered";
}
}
