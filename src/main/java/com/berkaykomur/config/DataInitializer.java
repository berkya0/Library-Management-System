package com.berkaykomur.config;

import com.berkaykomur.enums.Role;
import com.berkaykomur.model.Book;
import com.berkaykomur.model.Category;
import com.berkaykomur.model.Member;
import com.berkaykomur.model.User;
import com.berkaykomur.repository.BookRepository;
import com.berkaykomur.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setCreateTime(new Date());
            admin.setRole(Role.ADMIN);

            Member member = new Member();
            member.setEmail("admin@gmail.com");
            member.setFullName("Admin Berkay");
            member.setPhoneNumber("05055055555");
            member.setUser(admin);
            admin.setMember(member);

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setCreateTime(new Date());
            user.setRole(Role.USER);

            Member member2 = new Member();
            member2.setEmail("user@gmail.com");
            member2.setFullName("User Berkay");
            member2.setPhoneNumber("05055055550");
            member2.setUser(user);
            user.setMember(member2);
            userRepository.saveAll(List.of(admin,user));
        }

        if (bookRepository.count() == 0) {
            Book book1 = new Book();
            book1.setTitle("Sefiller");
            book1.setAuthor("Victor Hugo");
            book1.setIsbnNo("9786053320227");
            book1.setAvailable(true);
            book1.setActive(true);
            book1.setCategory(Category.ROMAN);

            Book book2 = new Book();
            book2.setTitle("Suç ve Ceza");
            book2.setAuthor("Dostoyevski");
            book2.setIsbnNo("9786053320234");
            book2.setAvailable(true);
            book2.setActive(true);
            book2.setCategory(Category.ROMAN);

            Book book3 = new Book();
            book3.setTitle("Kuru Fasulyenin Faydaları");
            book3.setAuthor("Yeşil Fasulye");
            book3.setIsbnNo("9786053320138");
            book3.setAvailable(true);
            book3.setActive(true);
            book3.setCategory(Category.TARİH);

            bookRepository.saveAll(List.of(book1, book3,book2));
        }

    }
}
