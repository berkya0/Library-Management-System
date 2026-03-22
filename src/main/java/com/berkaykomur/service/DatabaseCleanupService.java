package com.berkaykomur.service;

import com.berkaykomur.config.DataInitializer;
import com.berkaykomur.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DatabaseCleanupService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final DataInitializer dataInitializer;

    @Scheduled(cron = "0 */30 * * * *", zone = "Europe/Istanbul")
    @Transactional
    public void executeCleanup() {
        try {
            loanRepository.hardDeleteAll();
            refreshTokenRepository.hardDeleteAll();
            bookRepository.hardDeleteAll();
            memberRepository.hardDeleteAll();
            userRepository.hardDeleteAll();

            dataInitializer.run();
            System.out.println("Soft delete kısıtlamaları aşılarak veritabanı sıfırlandı.");


        } catch (Exception e) {
            System.out.println("Hata: "+e.getMessage());
            e.printStackTrace();

        }
    }
}
