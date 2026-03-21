package com.berkaykomur.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .version("1.0.0")
                        .contact(new Contact().name("Berkay Kömür").url("https://github.com/berkya0"))
                        .description("### 🚀 Proje Hakkında\n" +
                                "Bu API, modern bir kütüphane yönetim sisteminin backend gereksinimlerini karşılamak üzere **Spring Boot** ile geliştirilmiştir.\n\n" +
                                "### 🔐 Güvenlik ve Yetkilendirme\n" +
                                "* **JWT & Refresh Token:** Sistem stateless bir yapıda çalışır. Login sonrası alınan Access Token ile işlem yapılır.\n" +
                                "* **Authorize Kullanımı:** Sağ üstteki **Authorize** butonuna tıklayıp `Bearer <token>` formatında token girerek yetkili istek atabilirsiniz.\n" +
                                "* **Role-Based Access Control (RBAC):**\n" +
                                "  - `ADMIN`: Kitap ekleme/silme, tüm üyeleri listeleme ve rol güncelleme yetkisine sahiptir.\n" +
                                "  - `USER`: Kitap ödünç alma, kendi ödünç kayıtlarını görme ve profilini güncelleme yetkisine sahiptir.\n" +
                                "* **Ownership Check:** Kullanıcılar sadece kendi ID'lerine ait olan (admin değilse) kayıtlar üzerinde işlem yapabilir.\n\n" +
                                "### 🛠 Hızlı Test Bilgileri\n" +
                                "Veritabanı her gece **00:00'da (GMT+3)** otomatik olarak sıfırlanmakta ve aşağıdaki hesaplar tanımlanmaktadır:\n" +
                                "| Rol | Username | Password |\n" +
                                "| :--- | :--- | :--- |\n" +
                                "| **Admin** | `admin` | `admin123` |\n" +
                                "| **User** | `user` | `user123` |\n\n" +
                                "### 📦 Temel Modüller\n" +
                                "1. **Auth:** Kayıt, giriş ve token yenileme işlemleri.\n" +
                                "2. **Book:** Kitap envanter yönetimi (CRUD).\n" +
                                "3. **Loan:** Kitap ödünç alma ve iade süreci.\n" +
                                "4. **Member:** Kullanıcı profili ve üyelik bilgileri yönetimi.\n" +
                                "5. **User:** Şifre güncelleme hesabı silme işlemleri."))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
