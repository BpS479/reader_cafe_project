package com.readercafeproject.controller;

import com.readercafeproject.model.Message;
import com.readercafeproject.model.User;
import com.readercafeproject.repository.MessageRepository;
import com.readercafeproject.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // Constructor Injection (Spring Boot က အလိုအလျောက် DI လုပ်ပေးပါမည်)
    public MessageController(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }
//for admin
    @GetMapping("/admin-messages")
    public String chatPageAdmin(@RequestParam(value = "receiverId", required = false) Long receiverId,
            Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<User> chatableUsers = new ArrayList<>();

        // Role စစ်ဆေးခြင်း
        String userRole = currentUser.getRole() != null ? currentUser.getRole().toUpperCase() : "";
        boolean isAdmin = userRole.contains("ADMIN");

        if (isAdmin) {
            // ADMIN ဖြစ်ပါက - မိမိထံ စာလာပို့ဖူးသော User များကိုသာ ပြမည်
            chatableUsers = messageRepository.findUsersWhoMessagedAdmin(currentUser);
        } else {
            // USER ဖြစ်ပါက - System ထဲရှိ Admin များကို သာ ရှာပြမည်
            chatableUsers = userRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && u.getRole().toUpperCase().contains("ADMIN"))
                    .toList();
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", chatableUsers);

        // Active Chat Box အတွက် Logic
        User activeUser = null;

        if (receiverId != null) {
            activeUser = userRepository.findById(receiverId).orElse(null);
        } else if (!chatableUsers.isEmpty()) {
            // receiverId မပါလာပါက ပထမဆုံးလူ၏ Chat ကို အလိုအလျောက် ပွင့်ပေးမည်
            activeUser = chatableUsers.get(0);
        }

        if (activeUser != null) {
            List<Message> chatHistory = messageRepository.findChatHistory(currentUser, activeUser);
            model.addAttribute("activeUser", activeUser);
            model.addAttribute("chatHistory", chatHistory);
        }

        return "admin/admin-messages";
    }

    //for user
    @GetMapping("/user-messages")
    public String chatPageUser(@RequestParam(value = "receiverId", required = false) Long receiverId,
            Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<User> chatableUsers = new ArrayList<>();

        // Role စစ်ဆေးခြင်း
        String userRole = currentUser.getRole() != null ? currentUser.getRole().toUpperCase() : "";
        boolean isAdmin = userRole.contains("ADMIN");

        if (isAdmin) {
            // ADMIN ဖြစ်ပါက - မိမိထံ စာလာပို့ဖူးသော User များကိုသာ ပြမည်
            chatableUsers = messageRepository.findUsersWhoMessagedAdmin(currentUser);
        } else {
            // USER ဖြစ်ပါက - System ထဲရှိ Admin များကို သာ ရှာပြမည်
            chatableUsers = userRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && u.getRole().toUpperCase().contains("ADMIN"))
                    .toList();
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", chatableUsers);

        // Active Chat Box အတွက် Logic
        User activeUser = null;

        if (receiverId != null) {
            activeUser = userRepository.findById(receiverId).orElse(null);
        } else if (!chatableUsers.isEmpty()) {
            // receiverId မပါလာပါက ပထမဆုံးလူ၏ Chat ကို အလိုအလျောက် ပွင့်ပေးမည်
            activeUser = chatableUsers.get(0);
        }

        if (activeUser != null) {
            List<Message> chatHistory = messageRepository.findChatHistory(currentUser, activeUser);
            model.addAttribute("activeUser", activeUser);
            model.addAttribute("chatHistory", chatHistory);
        }

        return "user/user-messages";
    }
//for admin
    @PostMapping("/admin/send")
    public String sendMessageAdmin(@RequestParam("receiverId") Long receiverId,
            @RequestParam("content") String content,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User sender = userRepository.findByEmail(principal.getName()).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        if (content != null && !content.trim().isEmpty()) {
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            messageRepository.save(message);
        }

        return "redirect:/messages/admin-messages?receiverId=" + receiverId;
    }
//for user
    @PostMapping("/user/send")
    public String sendMessage(@RequestParam("receiverId") Long receiverId,
            @RequestParam("content") String content,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User sender = userRepository.findByEmail(principal.getName()).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        if (content != null && !content.trim().isEmpty()) {
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            messageRepository.save(message);
        }

        return "redirect:/messages/user-messages?receiverId=" + receiverId;
    }

    //for admin delete
    @PostMapping("/admin/delete")
    public String deleteChatAdmin(@RequestParam("receiverId") Long receiverId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        User otherUser = userRepository.findById(receiverId).orElse(null);

        if (otherUser != null) {
            // လူနှစ်ဦးကြားက Message စာရင်းတစ်ခုလုံးကို ဖျက်လိုက်မည်
            messageRepository.deleteChatHistory(currentUser, otherUser);
        }

        return "redirect:/messages/admin-messages";
    }
    // for user
    @PostMapping("/user/delete")
    public String deleteChat(@RequestParam("receiverId") Long receiverId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        User otherUser = userRepository.findById(receiverId).orElse(null);

        if (otherUser != null) {
            // လူနှစ်ဦးကြားက Message စာရင်းတစ်ခုလုံးကို ဖျက်လိုက်မည်
            messageRepository.deleteChatHistory(currentUser, otherUser);
        }

        return "redirect:/messages/user-messages";
    }
}