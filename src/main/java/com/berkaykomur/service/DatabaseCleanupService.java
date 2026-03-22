package com.berkaykomur.service;

import com.berkaykomur.config.DataInitializer;
import com.berkaykomur.repository.BookRepository;
import com.berkaykomur.repository.LoanRepository;
import com.berkaykomur.repository.RefreshTokenRepository;
import com.berkaykomur.repository.UserRepository;
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
    private final DataInitializer dataInitializer;

    @Scheduled(cron = "0 */30 * * * *", zone = "Europe/Istanbul")
    @Transactional
    public void executeMidnightCleanup() {

        try {
            loanRepository.deleteAllInBatch();
            refreshTokenRepository.deleteAllInBatch();
            bookRepository.deleteAllInBatch();
            userRepository.deleteAllInBatch();

            dataInitializer.run();
            System.out.println("Soft delete kısıtlamaları aşılarak veritabanı sıfırlandı.");


        } catch (Exception e) {
            System.out.println("Hata: "+e.getMessage());
            e.printStackTrace();

        }
    }
}
