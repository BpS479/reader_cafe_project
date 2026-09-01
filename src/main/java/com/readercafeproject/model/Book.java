package com.readercafeproject.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;

    @Column(length = 2000)
    private String description;
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    private String pdfFileUrl;

    private int viewCount = 0;
    
    
    private Integer downloadCount = 0; 

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isPremium = false; // true ဆိုရင် Premium Book, false ဆိုရင် Free Book

    public boolean isPremium() { 
        return isPremium;
    }
    public void setPremium(boolean isPremium) { 
        this.isPremium = isPremium;
    }

    // FavoriteBook Entity နှင့် OneToMany ချိတ်ဆက်ထား
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteBook> favoriteBooks = new ArrayList<>();

    // Comments အတွက် Cascade ထည့်ရန်
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    // Posts အတွက် Cascade
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    // --- Getter and Setter ---
    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }
     
}
