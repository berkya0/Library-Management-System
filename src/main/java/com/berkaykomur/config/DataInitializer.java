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
            for(int i=1;i<4;i++) {
                User admin = new User();
                admin.setUsername("admin"+i);
                admin.setPassword(passwordEncoder.encode("admin123"+i));
                admin.setCreateTime(new Date());
                admin.setRole(Role.ADMIN);

                Member member = new Member();
                member.setEmail("admin"+i+"@gmail.com");
                member.setFullName("Admin"+i+" Berkay");
                member.setPhoneNumber("0505505555"+i);
                member.setUser(admin);
                admin.setMember(member);

                User user = new User();
                user.setUsername("user"+i);
                user.setPassword(passwordEncoder.encode("user123"+i));
                user.setCreateTime(new Date());
                user.setRole(Role.USER);

                Member member2 = new Member();
                member2.setEmail("user"+i+"@gmail.com");
                member2.setFullName("User"+i+" Berkay");
                member2.setPhoneNumber("050550555"+(i+10));
                member2.setUser(user);
                user.setMember(member2);
                userRepository.saveAll(List.of(admin,user));
            }
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

            Book book4 = new Book();
            book4.setTitle("Atom Karınca");
            book4.setAuthor("AntMan");
            book4.setIsbnNo("9786053320123");
            book4.setAvailable(true);
            book4.setActive(true);
            book4.setCategory(Category.BİYOGRAFİ);

            Book book5 = new Book();
            book5.setTitle("Ketçap Olmak");
            book5.setAuthor("Tatlı Domatesler");
            book5.setIsbnNo("9786053320543");
            book5.setAvailable(true);
            book5.setActive(true);
            book5.setCategory(Category.TARİH);

            bookRepository.saveAll(List.of(book1, book5,book2,book3,book4));
        }

    }

}
