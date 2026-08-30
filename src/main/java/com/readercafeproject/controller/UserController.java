package com.readercafeproject.controller;

import com.readercafeproject.model.Book;
import com.readercafeproject.model.User;

import com.readercafeproject.model.BookRequest;
import com.readercafeproject.model.FavoriteBook;
import com.readercafeproject.model.PremiumRequest;
import com.readercafeproject.model.Comment;

import com.readercafeproject.repository.BookRepository;
import com.readercafeproject.repository.BookRequestRepository;
import com.readercafeproject.repository.CommentRepository;
import com.readercafeproject.repository.FavoriteBookRepository;
import com.readercafeproject.repository.PremiumRequestRepository;
import com.readercafeproject.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor // <--- final field များအတွက်
public class UserController {

    private final BookRepository bookRepository;
    private final BookRequestRepository requestRepository;

    private final FavoriteBookRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PremiumRequestRepository premiumRequestRepository;

    private final CommentRepository commentRepository;

    @GetMapping("/user-dashboard")
    public String userHome(@RequestParam(value = "keyword", required = false) String keyword, Model model,
            Principal principal, HttpSession session, RedirectAttributes redirectAttributes) {
        // Login အောင်မြင်ပြီး သစ်လွင်သော Session ဖြစ်ပါက Toast ပြရန်
        if (principal != null && session.getAttribute("WELCOME_TOAST_SHOWN") == null) {

            userRepository.findByEmail(principal.getName()).ifPresent(user -> {

                // User ရဲ့ Name သို့မဟုတ် Email ကို ယူရန်
                String displayName = user.getName() != null ? user.getName() : user.getEmail();
                redirectAttributes.addFlashAttribute("successToast", "Welcome " + displayName + "!");
            });

            // Login တစ်ကြိမ် ဝင်လျှင် Toast တစ်ကြိမ်သာ ပေါ်စေရန် Session မှတ်ထားခြင်း
            session.setAttribute("WELCOME_TOAST_SHOWN", true);
            return "redirect:/user/user-dashboard"; // Flash attribute အလုပ်လုပ်ရန် redirect ပြန်ပေးရပါမည်
        }
        // Premium user
        if (principal != null) {
            userRepository.findByEmail(principal.getName()).ifPresent(user -> {
                model.addAttribute("user", user);// ဒါက login ဝင်ထားတဲ့ ယူဆာ ကိုရှာတာ

            });
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("books", bookRepository
                    .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCaseOrderByIdDesc(
                            keyword, keyword, keyword));
        } else {
            model.addAttribute("books", bookRepository.findAllByOrderByDownloadCountDesc().stream().limit(3).toList());

        }

        return "user/user-dashboard";
    }

    @GetMapping("/home")
    public String homePage(@RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model,
            Principal principal) { // <--- Principal ပါဝင်ရမည်

        long totalEbooks = bookRepository.count();
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

        // 🔴 Favorite ပြုလုပ်ထားသော Book ID များကို ဆွဲထုတ်ခြင်း
        java.util.Set<Long> favoriteBookIds = new java.util.HashSet<>();
        if (principal != null) {
            userRepository.findByEmail(principal.getName()).ifPresent(user -> {
                model.addAttribute("user", user);// ဒါက login ဝင်ထားတဲ့ ယူဆာ ကိုရှာတာ
                favoriteBookIds.addAll(
                        favoriteRepository.findByUser(user)
                                .stream()
                                .map(fav -> fav.getBook().getId())
                                .toList());
            });
        }

        model.addAttribute("totalEbooks", totalEbooks);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("favoriteBookIds", favoriteBookIds); // <--- Model ထဲထည့်ပေးခြင်း

        return "user/home3";
    }

    // စာအုပ် detail
    // @GetMapping("/books/detail/{id}")
    // public String detail(@PathVariable Long id, Model model) {
    // Book book = bookRepository.findById(id).orElseThrow();

    // model.addAttribute("book", book);
    // return "user/book-detail";
    // }
    // Line 142 ဝန်းကျင်မှာ ရှိတဲ့ detail method ကို ဒီလိုပဲ ပြင်လိုက်ပါ
    @GetMapping("/books/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElseThrow();

        // Comment List များကို ဆွဲထုတ်ခြင်း
        List<Comment> comments = commentRepository.findByBookOrderByIdDesc(book);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);

        return "user/book-detail";
    }

    // 2. Comment အသစ် ရေးသားပါက Save မည့် PostMapping Method
    @PostMapping("/books/comment/{id}")
    public String addComment(@PathVariable Long id,
            @RequestParam("content") String content,
            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (content == null || content.trim().isEmpty()) {
            return "redirect:/user/books/detail/" + id;
        }

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(id).orElseThrow();

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setBook(book);
        comment.setContent(content.trim());

        commentRepository.save(comment);

        return "redirect:/user/books/detail/" + id;
    }

    // စာအုပ်ဖတ်
    // @GetMapping("/books/read/{id}")
    // public String readPdf(@PathVariable Long id, Principal principal, Model
    // model) {
    // Book book = bookRepository.findById(id).orElseThrow();
    // User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    // // 🔒 စာအုပ်က Premium ဖြစ်ပြီး User က Premium မဟုတ်ပါက တားဆီးမည်
    // if (book.isPremium() && !user.isPremium()) {
    // return "redirect:/user/books/detail/" + id + "?error=premium_required";
    // }
    // // ViewCount က null ဖြစ်နေရင် NullPointerException မတက်အောင် စစ်ပေးထားခြင်း
    // book.setViewCount(book.getViewCount() + 1);
    // bookRepository.save(book);

    // model.addAttribute("book", book);
    // return "user/read-pdf";
    // }
    // စာအုပ်ဖတ် (View Count Spam တားဆီးထားသည်)
    @GetMapping("/books/read/{id}")
    public String readPdf(@PathVariable Long id, Principal principal, Model model, HttpSession session) {
        Book book = bookRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        // 🔒 စာအုပ်က Premium ဖြစ်ပြီး User က Premium မဟုတ်ပါက တားဆီးမည်
        if (book.isPremium() && !user.isPremium()) {
            return "redirect:/user/books/detail/" + id + "?error=premium_required";
        }

        // 🛡️ View Count Duplicate Spam တားဆီးခြင်း (Session-based)
        @SuppressWarnings("unchecked")
        java.util.Set<Long> viewedBookIds = (java.util.Set<Long>) session.getAttribute("VIEWED_BOOKS");
        if (viewedBookIds == null) {
            viewedBookIds = new java.util.HashSet<>();
        }

        // ဒီ Session ထဲမှာ ဒီ စာအုပ်ကို မဖတ်ရသေးရင်မှ View Count +1 တိုးမည်
        if (!viewedBookIds.contains(id)) {
            // ✅ အောက်ပါအတိုင်း တိုက်ရိုက် +1 တိုးပေးလိုက်ပါ
            book.setViewCount(book.getViewCount() + 1);
            bookRepository.save(book);

            viewedBookIds.add(id);
            session.setAttribute("VIEWED_BOOKS", viewedBookIds);
        }

        model.addAttribute("book", book);
        return "user/read-pdf";
    }

    // @GetMapping("/books/download/{id}")
    // public ResponseEntity<Resource> downloadPdf(@PathVariable("id") Long id) {
    // // 1. Database ထဲမှာ Download Count ကို +1 တိုးမည်
    // bookRepository.incrementDownloadCount(id);

    // // 2. Book Entity ကို ရှာမည်
    // Book book = bookRepository.findById(id)
    // .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));

    // try {
    // String pdfUrl = book.getPdfFileUrl();

    // if (pdfUrl == null || pdfUrl.trim().isEmpty()) {
    // throw new RuntimeException("PDF file URL is empty!");
    // }

    // // DB ထဲက "/uploads/abc.pdf" သို့မဟုတ် "abc.pdf" ကို File Path သို့
    // // ပြောင်းလဲခြင်း
    // String fileName = pdfUrl.startsWith("/uploads/") ? pdfUrl.substring(9) :
    // pdfUrl;
    // if (fileName.startsWith("/")) {
    // fileName = fileName.substring(1);
    // }

    // // Project ရဲ့ Static uploads Folder လမ်းကြောင်းအတိအကျကို ယူ
    // Path filePath =
    // Paths.get("src/main/resources/static/uploads").resolve(fileName).toAbsolutePath()
    // .normalize();

    // if (!Files.exists(filePath)) {
    // throw new RuntimeException("File not found on server at: " +
    // filePath.toString());
    // }

    // Resource resource = new UrlResource(filePath.toUri());

    // // Original Filename ထုတ်ယူရန် (Timestamp အနောက်က ဖိုင်နာမည်စစ်)
    // String downloadFileName = fileName.contains("_") ?
    // fileName.substring(fileName.indexOf("_") + 1) : fileName;

    // // 3. File ကို Download ချပေးမည့် Response ပြန်ပေးမည်
    // return ResponseEntity.ok()
    // .contentType(MediaType.APPLICATION_PDF)
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
    // downloadFileName + "\"")
    // .body(resource);

    // } catch (Exception e) {
    // e.printStackTrace();
    // throw new RuntimeException("Could not download file: " + e.getMessage());
    // }
    // }
    // စာအုပ် Download ချခြင်း (Download Count Spam တားဆီးထားသည်)
    @GetMapping("/books/download/{id}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable("id") Long id, HttpSession session) {

        // 🛡️ Download Count Duplicate Spam တားဆီးခြင်း (Session-based)
        @SuppressWarnings("unchecked")
        java.util.Set<Long> downloadedBookIds = (java.util.Set<Long>) session.getAttribute("DOWNLOADED_BOOKS");
        if (downloadedBookIds == null) {
            downloadedBookIds = new java.util.HashSet<>();
        }

        // ဒီ Session ထဲမှာ ဒီ စာအုပ်ကို မဒေါင်းရသေးရင်မှ DB ထဲက Download Count ကို +1
        // တိုးမည်
        if (!downloadedBookIds.contains(id)) {
            bookRepository.incrementDownloadCount(id);
            downloadedBookIds.add(id);
            session.setAttribute("DOWNLOADED_BOOKS", downloadedBookIds);
        }

        // 2. Book Entity ကို ရှာမည်
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));

        try {
            String pdfUrl = book.getPdfFileUrl();

            if (pdfUrl == null || pdfUrl.trim().isEmpty()) {
                throw new RuntimeException("PDF file URL is empty!");
            }

            // DB ထဲက "/uploads/abc.pdf" သို့မဟုတ် "abc.pdf" ကို File Path သို့
            // ပြောင်းလဲခြင်း
            String fileName = pdfUrl.startsWith("/uploads/") ? pdfUrl.substring(9) : pdfUrl;
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }

            // Project ရဲ့ Static uploads Folder လမ်းကြောင်းအတိအကျကို ယူ
            Path filePath = Paths.get("src/main/resources/static/uploads").resolve(fileName).toAbsolutePath()
                    .normalize();

            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found on server at: " + filePath.toString());
            }

            Resource resource = new UrlResource(filePath.toUri());

            // Original Filename ထုတ်ယူရန် (Timestamp အနောက်က ဖိုင်နာမည်စစ်)
            String downloadFileName = fileName.contains("_") ? fileName.substring(fileName.indexOf("_") + 1) : fileName;

            // 3. File ကို Download ချပေးမည့် Response ပြန်ပေးမည်
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not download file: " + e.getMessage());
        }
    }

    @GetMapping("/request")
    public String showRequestForm(Model model) {
        model.addAttribute("bookRequest", new BookRequest());

        return "user/request-book";
    }

    @PostMapping("/request/save")
    public String saveRequest(@ModelAttribute BookRequest bookRequest, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        bookRequest.setUser(user);
        requestRepository.save(bookRequest);
        return "redirect:/user/home?success";
    }
    // @PostMapping("/request/save")
    // public String saveRequest(@ModelAttribute BookRequest bookRequest) {
    // requestRepository.save(bookRequest);
    // return "redirect:/user/home?success";
    // }

    // Favorite Book
    // 1. Favorite ထည့်ရန် သို့မဟုတ် ပြန်ဖြုတ်ရန် (Toggle Favorite)
    // @PostMapping("/books/favorite/{id}")
    // public String toggleFavorite(@PathVariable Long id, Principal principal) {
    // if (principal == null) {
    // return "redirect:/login";
    // }

    // User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    // Book book = bookRepository.findById(id).orElseThrow();

    // Optional<FavoriteBook> existingFav =
    // favoriteRepository.findByUserAndBook(user, book);

    // if (existingFav.isPresent()) {
    // // ရှိပြီးသားဆိုရင် ပြန်ဖြုတ်မည် (Unfavorite)
    // favoriteRepository.delete(existingFav.get());
    // } else {
    // // မရှိသေးရင် Favorite အဖြစ် သိမ်းမည်
    // FavoriteBook favorite = new FavoriteBook();
    // favorite.setUser(user);
    // favorite.setBook(book);
    // favoriteRepository.save(favorite);
    // }

    // return "redirect:/user/home";
    // }
    // 1. Favorite ထည့်ရန် သို့မဟုတ် ပြန်ဖြုတ်ရန် (Toggle Favorite - AJAX)
    @PostMapping("/books/favorite/{id}")
    @ResponseBody // 👈 Page Reload မဖြစ်အောင် @ResponseBody သုံး
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long id, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(id).orElseThrow();

        Optional<FavoriteBook> existingFav = favoriteRepository.findByUserAndBook(user, book);

        boolean isFav;
        if (existingFav.isPresent()) {
            // ရှိပြီးသားဆိုရင် ပြန်ဖြုတ် (Unfavorite)
            favoriteRepository.delete(existingFav.get());
            isFav = false;
        } else {
            // မရှိရင် Favorite အဖြစ် သိမ်း
            FavoriteBook favorite = new FavoriteBook();
            favorite.setUser(user);
            favorite.setBook(book);
            favoriteRepository.save(favorite);
            isFav = true;
        }

        response.put("success", true);
        response.put("isFavorite", isFav);
        return ResponseEntity.ok(response);
    }

    // 2. Favorite လုပ်ထားသော စာအုပ်များ စာရင်းကြည့်ရန် Page
    @GetMapping("/favorites")
    public String viewFavorites(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<FavoriteBook> favoriteList = favoriteRepository.findByUserOrderByIdDesc(user);

        model.addAttribute("favorites", favoriteList);
        return "user/favorites";
    }

    @GetMapping("/aboutus")
    public String aboutus(Model model, Principal principal) {

        return "user/aboutus";
    }

    // User ဘက်မှ Premium Request လုပ်သည့် အပိုင်း
    @PostMapping("/request-premium")
    public String requestPremium(@RequestParam("months") int months,
            @RequestParam("paymentMethod") String paymentMethod, // 👈 Payment Method လက်ခံမည်
            @RequestParam("paymentProof") MultipartFile file,
            Principal principal) throws IOException {

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        // File Upload Logic
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("src/main/resources/static/uploads/payments");
        if (!Files.exists(uploadPath))
            Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        PremiumRequest request = new PremiumRequest();
        request.setUser(user);
        request.setMonths(months);
        request.setPaymentMethod(paymentMethod); // 👈 Save လုပ်ပေးမည်
        request.setPaymentProofUrl("/uploads/payments/" + fileName);
        request.setStatus("PENDING");

        premiumRequestRepository.save(request);

        return "redirect:/user/home?success=requested";
    }

    @GetMapping("/request-premium")
    public String requestPremium(Model model) {
        return "user/request-premium";
    }

}