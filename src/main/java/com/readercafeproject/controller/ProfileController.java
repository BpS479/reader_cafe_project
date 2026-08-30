package com.readercafeproject.controller;

import com.readercafeproject.model.Post;
import com.readercafeproject.model.User;
import com.readercafeproject.repository.PostRepository;
import com.readercafeproject.repository.UserRepository;
 import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user/profile")
public class ProfileController {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    ProfileController(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    // Profile Page ပြသခြင်း
    @GetMapping
    public String showProfile(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<Post> userPosts = postRepository.findByUserOrderByIdDesc(user);

        // User တင်ထားသော Post များ၏ Like စုစုပေါင်း တွက်ချက်ခြင်း
        int totalLikesReceived = userPosts.stream()
                .mapToInt(post -> post.getLikes() != null ? post.getLikes().size() : 0)
                .sum();

        model.addAttribute("user", user);
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("totalLikesReceived", totalLikesReceived);

        return "user/profile";
    }
    // အခြား User များ၏ Public Profile ကြည့်ရှုခြင်း (Path Variable သုံးထားသည်)
    @GetMapping("/{id}")
    public String showPublicProfile(@PathVariable("id") Long targetUserId, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        // မိမိ ကိုယ်ပိုင် Profile ID ကို နှိပ်မိပါက မိမိ Profile သို့ Redirect ပြန်မည်
        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (currentUser.getId().equals(targetUserId)) {
            return "redirect:/user/profile";
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

        return "user/public-profile"; // Public Profile View
    }

    // Profile Info Update ပြုလုပ်ခြင်း
    @PostMapping("/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam(value = "birthday", required = false) String birthdayStr,
                                @RequestParam(value = "bio", required = false) String bio,
                                @RequestParam(value = "profileImage", required = false) MultipartFile multipartFile,
                                Principal principal) throws IOException {

        if (principal == null) return "redirect:/login";

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        user.setName(name);
        user.setBio(bio);

        // Birthday Optional Check
        if (birthdayStr != null && !birthdayStr.trim().isEmpty()) {
            user.setBirthday(LocalDate.parse(birthdayStr));
        } else {
            user.setBirthday(null);
        }

        // Profile Image Upload Handling (src/main/resources/static/uploads ထဲသို့ သိမ်းမည်)
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String uniqueFileName = user.getId() + "_" + System.currentTimeMillis() + "_" + fileName;

            // WebConfig တွင် သတ်မှတ်ထားသော Path အတိုင်း static/uploads/ သို့ ပို့မည်
            String uploadDir = "src/main/resources/static/uploads/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Database ထဲသို့ /uploads/filename.ext အဖြစ် သိမ်းဆည်းမည်
                user.setProfileImageUrl("/uploads/" + uniqueFileName);
            }
        }

        userRepository.save(user);
        return "redirect:/user/profile";
    }

    // Post ဖျက်ခြင်း (Own Post Check)
    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Post post = postRepository.findById(id).orElseThrow();

        // မိမိပိုင်သော Post ဖြစ်မှသာ ဖျက်ခွင့်ပေးမည်
        if (post.getUser().getId().equals(user.getId())) {
            postRepository.delete(post);
        }

        return "redirect:/user/profile";
    }
}