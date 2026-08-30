package com.readercafeproject.controller;

import com.readercafeproject.model.Book;
import com.readercafeproject.model.Post;
import com.readercafeproject.model.PremiumRequest;
import com.readercafeproject.model.User;
import com.readercafeproject.model.Comment;


import com.readercafeproject.repository.BookRepository;
import com.readercafeproject.repository.BookRequestRepository;
import com.readercafeproject.repository.CommentRepository;
import com.readercafeproject.repository.UserRepository; // <--- UserRepository ကို Import လုပ်ပါ
import com.readercafeproject.repository.MessageRepository;
import com.readercafeproject.repository.PremiumRequestRepository;
import com.readercafeproject.repository.PostRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


 import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor // final field များကို အလိုအလျောက် Inject လုပ်ပေးမည်
public class AdminController {

    private final BookRepository bookRepository;
    private final BookRequestRepository requestRepository;
    private final UserRepository userRepository; 
    private final MessageRepository messageRepository;
    private final PremiumRequestRepository premiumRequestRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    // Admin Dashboard - View All Books & Search & View Requests
    @GetMapping("/view-request")
    public String dashboard(Model model) {
        // if (keyword != null && !keyword.trim().isEmpty()) {
        //     model.addAttribute("books", bookRepository
        //             .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCaseOrderByIdDesc(
        //                     keyword, keyword, keyword));
        // } else {
        //     model.addAttribute("books", bookRepository.findAllByOrderByIdDesc());
        // }
        model.addAttribute("requests", requestRepository.findAll());
        return "admin/view-request";
    }
    // Delete Book
    @GetMapping("/view-request/delete/{id}")
    public String deleteRequest(@PathVariable Long id) {
        requestRepository.deleteById(id);
        return "redirect:/admin/view-request";
        // return "redirect:/admin/dashboard";
    }


    // Form - Add Book
    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "admin/add-book";
    }

    // Save Book with Cover Image and PDF Upload
    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Book book,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("pdfFile") MultipartFile pdfFile) throws IOException {

        Path uploadPath = Paths.get("src/main/resources/static/uploads").toAbsolutePath().normalize();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (!imageFile.isEmpty()) {
            String imageName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path targetLocation = uploadPath.resolve(imageName);
            Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            book.setCoverImageUrl("/uploads/" + imageName);
        }

        if (!pdfFile.isEmpty()) {
            String pdfName = System.currentTimeMillis() + "_" + pdfFile.getOriginalFilename();
            Path targetLocation = uploadPath.resolve(pdfName);
            Files.copy(pdfFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            book.setPdfFileUrl("/uploads/" + pdfName);
        }

        bookRepository.save(book);
        return "redirect:/admin/admin-books";
    }

    // 1. Show Edit Book Form (စာအုပ်ပြင်ရန် Form ပြခြင်း)
    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        return "admin/edit-books";
    }
 
    // 2. Update Book (ပြင်ဆင်ထားသော စာအုပ်ကို Save ပြန်လုပ်ခြင်း)
    @PostMapping("/books/update/{id}")
    public String updateBook(@PathVariable Long id,
            @ModelAttribute("book") Book updatedBook,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("pdfFile") MultipartFile pdfFile) throws IOException {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        // စာအုပ်၏ စာသားအချက်အလက်များကို ပြင်ဆင်ခြင်း
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setDescription(updatedBook.getDescription());

        Path uploadPath = Paths.get("src/main/resources/static/uploads").toAbsolutePath().normalize();

        // Cover Image အသစ်တင်ထားမှသာ လဲလှယ်မည်
        if (!imageFile.isEmpty()) {
            String imageName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path targetLocation = uploadPath.resolve(imageName);
            Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            existingBook.setCoverImageUrl("/uploads/" + imageName);
        }

        // PDF File အသစ်တင်ထားမှသာ လဲလှယ်မည်
        if (!pdfFile.isEmpty()) {
            String pdfName = System.currentTimeMillis() + "_" + pdfFile.getOriginalFilename();
            Path targetLocation = uploadPath.resolve(pdfName);
            Files.copy(pdfFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            existingBook.setPdfFileUrl("/uploads/" + pdfName);
        }

        bookRepository.save(existingBook);
        return "redirect:/admin/admin-books";
        // return "redirect:/admin/dashboard";
    }

    // Delete Book
    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/admin/admin-books";
        // return "redirect:/admin/dashboard";
    }

    // 3. View Registered Users (User များ စာရင်းကြည့်ရန်)
    @GetMapping("/users")
    public String viewUsers(@RequestParam(value = "keyword", required = false) String keyword,@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        // model.addAttribute("users", userRepository.findAllByOrderByIdDesc());
        // return "admin/view-user";
        long totalUser = userRepository.count();
        int pageSize = 6;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<User> userPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userRepository
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRoleContainingIgnoreCaseOrderByIdDesc(
                            keyword, keyword,keyword, pageable);
        } else {
            userPage = userRepository.findAllByOrderByIdDesc(pageable);
        }
        model.addAttribute("user",totalUser);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        // model.addAttribute("requests", requestRepository.findAll());
        return "admin/view-user";
    }
    // @GetMapping("/users")
    // public String viewUsers(@RequestParam(value = "keyword", required = false) String keyword,@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
    //     // model.addAttribute("users", userRepository.findAllByOrderByIdDesc());
    //     // return "admin/view-user";
    //     if (keyword != null && !keyword.trim().isEmpty()) {
    //         model.addAttribute("books", bookRepository
    //                 .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCaseOrderByIdDesc(
    //                         keyword, keyword, keyword));
    //     } else {
    //         model.addAttribute("books", bookRepository.findAllByOrderByIdDesc());
    //     }
    //     model.addAttribute("requests", requestRepository.findAll());
    //     return "admin/dashboard";
    // }

    // Overview Console Page
    @GetMapping("/admin-dashboard")
    public String overviewConsole(Model model,Principal principal, HttpSession session,RedirectAttributes redirectAttributes) {
         // Login အောင်မြင်ပြီး သစ်လွင်သော Session ဖြစ်ပါက Toast ပြရန်
        if (principal != null && session.getAttribute("WELCOME_TOAST_SHOWN") == null) {
            userRepository.findByEmail(principal.getName()).ifPresent(user -> {
                // User ရဲ့ Name သို့မဟုတ် Email ကို ယူရန်
                String displayName = user.getName() != null ? user.getName() : user.getEmail();
                redirectAttributes.addFlashAttribute("successToast", "Welcome " + displayName + "!");
            });
        
            // Login တစ်ကြိမ် ဝင်လျှင် Toast တစ်ကြိမ်သာ ပေါ်စေရန် Session မှတ်ထားခြင်း
            session.setAttribute("WELCOME_TOAST_SHOWN", true);
            return "redirect:/admin/admin-dashboard"; // Flash attribute အလုပ်လုပ်ရန် redirect ပြန်ပေးရပါမည်
        }

        Integer sumDownloads = bookRepository.getSumDownload();
        int sum = (sumDownloads != null) ? sumDownloads : 0;

 
        long totalEbooks = bookRepository.count();
        long totalReaders = userRepository.count();
        long totalMessages = messageRepository.count();
        
        //List<Book> allBooks = bookRepository.findAllByOrderByIdDesc();
        List<Book> books = bookRepository.findAllByOrderByIdDesc()
            .stream()
            .limit(5)
            .toList();
        
        List<User> recentReaders = userRepository.findAllByOrderByIdDesc();
                // .stream()
                // .limit(5)
                // .toList();
         model.addAttribute("sumDownload",sum);
        model.addAttribute("totalEbooks", totalEbooks);
        model.addAttribute("totalReaders", totalReaders);
        model.addAttribute("recentReaders", recentReaders);
        model.addAttribute("totalMessages",totalMessages);
        // ⬇️ ဒီ Line လေး ဖြည့်ပေးရပါမယ်
        model.addAttribute("books", books);
        return "admin/admin-dashboard";
    }

    @GetMapping("/admin-books")
    public String bookManagement(@RequestParam(value = "keyword", required = false) String keyword,@RequestParam(value = "page", defaultValue = "0") int page, Model model,Principal principal) {
        List<Book> books = bookRepository.findAllByOrderByIdDesc();
        model.addAttribute("bookCount", books);
       // model.addAttribute("downloadCount",books);
        long totalEbooks = bookRepository.count();
        
       // မူလရေးထားသော စာကြောင်းနေရာတွင် ပြင်ရန်:
Integer totalDownloads = bookRepository.getSumDownload();
int sum = (totalDownloads != null) ? totalDownloads : 0;



        int pageSize = 6;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Book> bookPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            bookPage = bookRepository
                    .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCaseOrderByIdDesc(
                            keyword, keyword, keyword, pageable);
        } else {
            bookPage = bookRepository.findAllByOrderByIdDesc(pageable);
        }
        model.addAttribute("requests", requestRepository.findAll());
        model.addAttribute("totalEbooks",totalEbooks);
        model.addAttribute("sumDownload",sum);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/admin-books";

        // return "admin/bookmanagement";
    }

    // @GetMapping("/bookmanagement")
    // public String bookManagement(@RequestParam(value = "keyword", required = false) String keyword,@RequestParam(value = "page", defaultValue = "0") int page, Model model,Principal principal) {
    //     // List<Book> books = bookRepository.findAllByOrderByIdDesc();
    //     // model.addAttribute("books", books);
    //     long totalEbooks = bookRepository.count();
    //     int pageSize = 6;
    //     Pageable pageable = PageRequest.of(page, pageSize);

    //     Page<Book> bookPage;

    //     if (keyword != null && !keyword.trim().isEmpty()) {
    //         model.addAttribute("books", bookRepository
    //                 .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCaseOrderByIdDesc(
    //                         keyword, keyword, keyword,pageable));
    //     } else {
    //         model.addAttribute("books", bookRepository.findAllByOrderByIdDesc(pageable));
    //     }
    //     model.addAttribute("requests", requestRepository.findAll());
    //     return "admin/admin-books";

    //     // return "admin/bookmanagement";
    // }

    // Admin စာအုပ်ကို ဖတ်ရန် PDF ဖိုင်ကို ဖတ်ရန် Controller Method
   @GetMapping("/books/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElseThrow();

        // Comment List များကို ဆွဲထုတ်ခြင်း
        List<Comment> comments = commentRepository.findByBookOrderByIdDesc(book);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);

        return "admin/admin-book-detail";
    }

    @PostMapping("/users/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElse(null);
        
        if (user != null) {
            if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
                user.setStatus("SUSPENDED");
                redirectAttributes.addFlashAttribute("message", "User has been suspended successfully.");
            } else {
                user.setStatus("ACTIVE");
                redirectAttributes.addFlashAttribute("message", "User has been activated successfully.");
            }
            userRepository.save(user);
        }
        
        return "redirect:/admin/users"; // User List ပြသော လမ်းကြောင်းသို့ ပြန်ပို့ပါ
    }


//     @PostMapping("/premium-requests/approve/{id}")
//     public String approveRequest(@PathVariable Long id) {
//     PremiumRequest request = premiumRequestRepository.findById(id).orElseThrow();
//     User user = request.getUser();

//     // Premium Status ကို True လုပ်မည်
//     user.setPremium(true);

//     // ရက်စွဲ တွက်ချက်မည် (ယခင် သက်တမ်းကျန်နေပါက ထပ်ပေါင်းပေးမည်)
//     LocalDate currentExpiry = user.getPremiumExpiryDate();
//     LocalDate baseDate = (currentExpiry != null && currentExpiry.isAfter(LocalDate.now())) 
//                          ? currentExpiry 
//                          : LocalDate.now();

//     user.setPremiumExpiryDate(baseDate.plusMonths(request.getMonths()));
//     request.setStatus("APPROVED");

//     userRepository.save(user);
//     premiumRequestRepository.save(request);

//     return "redirect:/admin/premium-requests";
// }

// @GetMapping("/request-premium")
// public String viewRequests(@RequestParam(value = "keyword", required = false) String keyword,
//                            @RequestParam(value = "page", defaultValue = "0") int page, 
//                            Model model, 
//                            Principal principal) {
    
//     int pageSize = 6;
//     Pageable pageable = PageRequest.of(page, pageSize);
//     Page<PremiumRequest> premiumPage;

//     if (keyword != null && !keyword.trim().isEmpty()) {
//         premiumPage = premiumRequestRepository
//                 .findByStatusContainingIgnoreCaseOrUserEmailContainingIgnoreCaseOrPaymentMethodContainingIgnoreCaseOrderByIdDesc(
//                         keyword, keyword, keyword, pageable);
//     } else {
//         premiumPage = premiumRequestRepository.findAllByOrderByIdDesc(pageable);
//     }

//     // HTML ထဲတွင် ပြသရန် Data များ ထည့်သွင်းခြင်း
//     model.addAttribute("requests", premiumPage.getContent()); // Table ထဲ Loop ပတ်ရန်
//     model.addAttribute("currentPage", page);
//     model.addAttribute("totalPages", premiumPage.getTotalPages());
//     model.addAttribute("keyword", keyword);

//     return "admin/premium-requests";
// }    
// @PostMapping("/premium-requests/approve/{id}")
// @Transactional // Transaction Safe ဖြစ်စေရန်
// public String approveRequest(@PathVariable Long id) {
//     PremiumRequest request = premiumRequestRepository.findById(id)
//             .orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + id));
            
//     User user = request.getUser();

//     // Premium Status ကို True လုပ်မည်
//     user.setPremium(true);

//     // ရက်စွဲ တွက်ချက်မည် (ယခင် သက်တမ်းကျန်နေပါက ထပ်ပေါင်းပေးမည်)
//     LocalDate currentExpiry = user.getPremiumExpiryDate();
//     LocalDate baseDate = (currentExpiry != null && currentExpiry.isAfter(LocalDate.now())) 
//                          ? currentExpiry 
//                          : LocalDate.now();

//     user.setPremiumExpiryDate(baseDate.plusMonths(request.getMonths()));
//     request.setStatus("APPROVED");

//     userRepository.save(user);
//     premiumRequestRepository.save(request);

//     // Redirect လမ်းကြောင်းကို GetMapping နှင့် ကိုက်ညီအောင် ပြင်ဆင်ခြင်း
//     return "redirect:/admin/request-premium";
// }

// // ❌ Reject လုပ်ရန် Method အသစ် ထည့်သွင်းခြင်း
// @PostMapping("/premium-requests/reject/{id}")
// @Transactional
// public String rejectRequest(@PathVariable Long id) {
//     PremiumRequest request = premiumRequestRepository.findById(id)
//             .orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + id));

//     request.setStatus("REJECTED");
//     premiumRequestRepository.save(request);

//     return "redirect:/admin/request-premium";
// }

@PostMapping("/premium-requests/approve/{id}")
@Transactional
public String approveRequest(@PathVariable Long id) {
    PremiumRequest request = premiumRequestRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + id));

    User user = request.getUser();

    // Premium Status ကို True ပြောင်းမည်
    user.setPremium(true);

    // ရက်စွဲ တွက်ချက်မည် (ယခင် သက်တမ်းကျန်နေပါက ထပ်ပေါင်းပေးမည်)
    LocalDate currentExpiry = user.getPremiumExpiryDate();
    LocalDate baseDate = (currentExpiry != null && currentExpiry.isAfter(LocalDate.now())) 
                         ? currentExpiry 
                         : LocalDate.now();

    user.setPremiumExpiryDate(baseDate.plusMonths(request.getMonths()));
    
    // Status ကို APPROVED သို့ ပြောင်းမည်
    request.setStatus("APPROVED");

    userRepository.save(user);
    premiumRequestRepository.save(request);

    return "redirect:/admin/premium-requests";
}

@PostMapping("/premium-requests/reject/{id}")
@Transactional
public String rejectRequest(@PathVariable Long id) {
    PremiumRequest request = premiumRequestRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + id));

    // Status ကို REJECTED သို့ ပြောင်းမည်
    request.setStatus("REJECTED");
    premiumRequestRepository.save(request);

    return "redirect:/admin/premium-requests";
}

@GetMapping("/premium-requests")
public String viewRequests(@RequestParam(value = "keyword", required = false) String keyword,
                           @RequestParam(value = "page", defaultValue = "0") int page, 
                           Model model, 
                           Principal principal) {
    
    int pageSize = 6;
    Pageable pageable = PageRequest.of(page, pageSize);
    Page<PremiumRequest> premiumPage;

    if (keyword != null && !keyword.trim().isEmpty()) {
        premiumPage = premiumRequestRepository
                .findByStatusContainingIgnoreCaseOrUserEmailContainingIgnoreCaseOrPaymentMethodContainingIgnoreCaseOrderByIdDesc(
                        keyword, keyword, keyword, pageable);
    } else {
        premiumPage = premiumRequestRepository.findAllByOrderByIdDesc(pageable);
    }

    model.addAttribute("requests", premiumPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", premiumPage.getTotalPages());
    model.addAttribute("keyword", keyword);

    return "admin/premium-requests";
}

 // အခြား User များ၏ Public Profile ကြည့်ရှုခြင်း (Path Variable သုံးထားသည်)
    @GetMapping("/user-profile/{id}")
    public String showPublicProfile(@PathVariable("id") Long targetUserId, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        // မိမိ ကိုယ်ပိုင် Profile ID ကို နှိပ်မိပါက မိမိ Profile သို့ Redirect ပြန်မည်
        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (currentUser.getId().equals(targetUserId)) {
            return "redirect:/admin/user-profile";
        }

        // ကြည့်ရှုလိုသော Target User ကို ရှာဖွေခြင်း
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + targetUserId));

        List<Post> targetUserPosts = postRepository.findByUserOrderByIdDesc(targetUser);

        // Target User ရရှိထားသော Total Likes တွက်ချက်ခြင်း
        int totalLikesReceived = targetUserPosts.stream()
                .mapToInt(post -> post.getLikes() != null ? post.getLikes().size() : 0)
                .sum();

        model.addAttribute("targetUser", targetUser);
        model.addAttribute("userPosts", targetUserPosts);
        model.addAttribute("totalLikesReceived", totalLikesReceived);

        return "admin/user-profile"; // Public Profile View
    }



}
