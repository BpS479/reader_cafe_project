package com.readercafeproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String caption; // User ရေးချင်တဲ့ စာ

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Mention ခေါ်ထားတဲ့ Book (ဖျက်/ပြင် လို့မရအောင် Mandatory ချိတ်ထားသည်)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "book_id", nullable = false)
    // private Book book;
    // စာအုပ်မပါဘဲလည်း တင်လို့ရအောင် nullable = true ပြောင်းထားသည်
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = true) // nullable = true ဖြစ်ရပါမည်
    private Book book;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}