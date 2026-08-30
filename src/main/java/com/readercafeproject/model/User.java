package com.readercafeproject.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String username;

     

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role = "ROLE_USER"; // Default အနေဖြင့် USER ဟု သတ်မှတ်မည်

    @Column(length = 150)
    private String bio;

    private LocalDate birthday;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Status Field အသစ်ထည့်ရန် (ACTIVE / SUSPENDED)
    private String status = "ACTIVE"; 

    private boolean isPremium = false;
    private LocalDate premiumExpiryDate; // Premium သက်တမ်းကုန်မည့်ရက်

    // Getter and Setters
    public boolean isPremium() {
        // သက်တမ်းကုန်သွားပါက အလိုအလျောက် false ဖြစ်စေရန် Logic
        if (isPremium && premiumExpiryDate != null && LocalDate.now().isAfter(premiumExpiryDate)) {
            this.isPremium = false;
        }
        return isPremium;
    }
    public void setPremium(boolean isPremium) { this.isPremium = isPremium; }
    public LocalDate getPremiumExpiryDate() { return premiumExpiryDate; }
    public void setPremiumExpiryDate(LocalDate premiumExpiryDate) { this.premiumExpiryDate = premiumExpiryDate; }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getters and Setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}