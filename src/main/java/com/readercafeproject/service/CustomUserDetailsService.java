package com.readercafeproject.service;

import com.readercafeproject.model.User;
import com.readercafeproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // <--- final field အတွက် Constructor အလိုအလျောက် ဆောက်ပေးပါမည်
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // <--- @Autowired အစား private final ပြောင်းလိုက်ပါ

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        // User status က SUSPENDED ဖြစ်နေလား စစ်ဆေးခြင်း
        boolean isSuspended = "SUSPENDED".equalsIgnoreCase(user.getStatus());
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", ""))
                .disabled(isSuspended)       // SUSPENDED ဖြစ်နေရင် account ကို disabled (false) လုပ်မည်
                .accountLocked(isSuspended)  // သို့မဟုတ် account locked လုပ်ထားမည်
                .build();
    }
}