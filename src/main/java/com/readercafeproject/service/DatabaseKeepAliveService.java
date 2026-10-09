package com.readercafeproject.service;

import com.readercafeproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseKeepAliveService {

    private final UserRepository userRepository;

    // 5 မိနစ် (300,000 milliseconds) လျှင် တစ်ကြိမ် အလိုအလျောက် အလုပ်လုပ်မည်
    @Scheduled(fixedRate = 300000)
    public void keepDatabaseAlive() {
        try {
            // Database ထဲက user အရေအတွက်ကို query တစ်ခု သွားမေးခြင်းဖြင့် DB connection ကို နိုးကြားစေပါသည်
            long userCount = userRepository.count();
            log.info("Keep-alive ping sent to Aiven DB. Total users: {}", userCount);
        } catch (Exception e) {
            log.error("Database keep-alive ping failed: {}", e.getMessage());
        }
    }
}
