package com.readercafeproject.controller;

import com.readercafeproject.model.*;
import com.readercafeproject.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor // Lombok constructor injection သုံးထားပါသည်
@RequestMapping("/user/feed")
public class SocialFeedController {

    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final FavoriteBookRepository favoriteBookRepository;
    private final PostCommentRepository postCommentRepository;

    // 1. Feed Main Page
    @GetMapping
    public String showFeed(Model model, Principal principal) {
        List<Post> posts = postRepository.findAllByOrderByIdDesc();
        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        // Long currentUserId = currentUser.getId();
        model.addAttribute("posts", posts);
        model.addAttribute("usernow",currentUser);

        Set<Long> likedPostIds = new HashSet<>();

        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            model.addAttribute("currentUser", user);

            if (user != null) {
                // User ရဲ့ Favorite Books ယူခြင်း
                List<Book> favoriteBooks = favoriteBookRepository.findByUser(user)
                        .stream().map(FavoriteBook::getBook).collect(Collectors.toList());
                model.addAttribute("favoriteBooks", favoriteBooks);

                // User Like ပေးထားသည့် Post ID များအားလုံးကို ရှာဖွေပြီး Set ထဲထည့်ခြင်း
                List<PostLike> userLikes = postLikeRepository.findByUser(user);
                likedPostIds = userLikes.stream()
                        .map(like -> like.getPost().getId())
                        .collect(Collectors.toSet());
            }
        }

        // HTML ဘက်သို့ likedPostIds ကို ပို့ပေးမည်
        model.addAttribute("likedPostIds", likedPostIds);

        return "user/feed";
    }

    // 2. Post တင်ခြင်း
    @PostMapping("/create")
    public String createPost(@RequestParam(value = "bookId", required = false) Long bookId,
                             @RequestParam("caption") String caption,
                             Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Post post = new Post();
        post.setUser(user);
        post.setCaption(caption);

        if (bookId != null) {
            Book book = bookRepository.findById(bookId).orElse(null);
            post.setBook(book);
        }

        postRepository.save(post);
        return "redirect:/user/feed";
    }

    // 3. Discussion Detail Page (Post အသီးသန့် + Comment & Reply)
    @GetMapping("/discussion/{postId}")
    public String showDiscussion(@PathVariable Long postId, Model model) {
        Post post = postRepository.findById(postId).orElseThrow();
        List<PostComment> mainComments = postCommentRepository.findByPostAndParentIsNullOrderByIdDesc(post);

        model.addAttribute("post", post);
        model.addAttribute("comments", mainComments);
        return "user/discussion-detail";
    }

    // 4. Comment / Reply ပို့ခြင်း
    @PostMapping("/comment/add")
    public String addComment(@RequestParam("postId") Long postId,
                             @RequestParam(value = "parentId", required = false) Long parentId,
                             @RequestParam("content") String content,
                             Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        PostComment comment = new PostComment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);

        if (parentId != null) {
            PostComment parent = postCommentRepository.findById(parentId).orElse(null);
            comment.setParent(parent);
        }

        postCommentRepository.save(comment);
        return "redirect:/user/feed/discussion/" + postId;
    }

    // Like / Unlike Toggle လုပ်သည့် Method
   // Fetch API အတွက် Response ပြန်ပေးမည့် DTO (သို့မဟုတ် Record)
    public record LikeResponse(boolean liked, int count) {}

    @PostMapping("/like/{postId}")
    @ResponseBody // HTML Page ပြန်မညွှန်းဘဲ JSON data သာ ပြန်ပို့ရန်
    public ResponseEntity<LikeResponse> toggleLike(@PathVariable Long postId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);
        boolean isLiked;

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            postLikeRepository.save(like);
            isLiked = true;
        }

        // Like အရေအတွက် အသစ်ကို တွက်ချက်ခြင်း
        int currentLikesCount = postLikeRepository.countByPost(post);

        return ResponseEntity.ok(new LikeResponse(isLiked, currentLikesCount));
    }
}