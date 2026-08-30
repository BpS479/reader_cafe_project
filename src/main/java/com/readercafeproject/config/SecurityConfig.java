package com.readercafeproject.config;

// import com.readercafe.service.CustomUserDetailsService;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        // @Autowired
        // private CustomUserDetailsService userDetailsService;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                // PDF ဖတ်သည့် embed/iframe များ အလုပ်လုပ်စေရန် SameOrigin ခွင့်ပြုမည်
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()))

                                .authorizeHttpRequests(auth -> auth
                                                // .requestMatchers( "/","/login","/register","/home",
                                                //                 "/admin-login",
                                                //                 "/css/**", "/js/**",
                                                //                 "/uploads/**")
                                                .requestMatchers( "/login" ,"/register",
                                                                "/admin-login",
                                                                "/css/**", "/js/**",
                                                                "/uploads/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login") // ကိုယ်ပိုင် Custom Login Page Path
                                                // .loginPage("/login") // ကိုယ်ပိုင် Custom Login Page Path
                                                .loginProcessingUrl("/login")  // ← ဒီတစ်ကြောင်း ထည့်ပါ
                                                // .failureUrl("/login?error=true")//login error for error messages
                                                .failureUrl("/login?loginError=true")
                                                .usernameParameter("email") // Username နေရာတွင် Email Input ကို သုံးမည်
                                                .passwordParameter("password")//login error for error messages
                                                .successHandler((request, response, authentication) -> {
                                                        var roles = authentication.getAuthorities();
                                                        for (var role : roles) {
                                                                if (role.getAuthority().equals("ROLE_ADMIN")) {
                                                                        response.sendRedirect("/admin/admin-dashboard");
                                                                        return;
                                                                }
                                                        }
                                                        response.sendRedirect("/user/user-dashboard");
                                                })
                                                .permitAll())
                                // Logout Configuration
                                .logout(logout -> logout
                                                .logoutUrl("/logout") // Logout ထွက်မယ့် URL Path
                                                
                                                // .logoutSuccessUrl("/login?logout") // Logout အောင်မြင်ပြီးရင်Login pageသို့ redirect လုပ်မည် (?logout
                                                // parameter ပါသွားမည်)
                                                .logoutSuccessUrl("/login?logout")                                           
                                                
                                                .invalidateHttpSession(true) // Session ကို ဖျက်မည်
                                                .clearAuthentication(true) // Authentication အချက်အလက်များကို
                                                                           // ရှင်းထုတ်မည်
                                                .deleteCookies("JSESSIONID") // Cookie ကို ဖျက်မည်
                                                .permitAll());
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}