# 📚 Kütüphane Yönetim Sistemi (Spring Boot)
Spring Boot ile geliştirilmiş, **güvenli ve production-ready bir Kütüphane Yönetim Sistemi**.  
Bu proje, **JWT tabanlı kimlik doğrulama**, **rol tabanlı yetkilendirme** ve temiz bir **katmanlı REST API mimarisi** sunar.

---

## 🚀 Özellikler
* 🔐 JWT tabanlı Kimlik Doğrulama & Yetkilendirme
* 👥 Rol tabanlı Erişim Kontrolü (ADMIN, USER)
* 📖 Kitap Yönetimi
* 🧑 Üye Yönetimi
* 🔄 Ödünç Sistemi (Kitap Alma / İade)
* ♻️ Refresh Token Mekanizması
* 🧩 Global Exception Handling
* 🏗 Temiz Katmanlı Mimari (Controller → Service → Repository)
* 📦 DTO tabanlı Veri Transferi
* 🛡 Spring Security Entegrasyonu

### 🧪 Testler
* ✅ **Unit Testler** → İş mantığı JUnit 5 & Mockito ile test edildi
* 🔐 **Integration Testler** → Güvenli API endpointlerinin uçtan uca testleri

---

## 🏛 Proje Mimarisi
Proje, temiz ve sürdürülebilir bir katmanlı mimariyi takip eder:

<img src="images/architecture.png" alt="Mimari" width="700">


## 🧠 Roller & Yetkilendirme

### ADMIN
* Neredeyse tüm endpointlere tam erişim

### USER
* Sınırlı erişim
* Sadece kendi verilerine erişebilir

**Örnek:**
```java
@PreAuthorize("hasRole('ADMIN') or #request.memberId == authentication.principal.memberId")
```
---

## 📂 Paket Yapısı

- `config` → Güvenlik ve uygulama yapılandırmaları  
- `controller` → REST API endpoint tanımlamaları  
- `dto` → Veri transfer objeleri  
- `enums` → Rol ve sabit tanımlamalar  
- `exception` → Özel istisna sınıfları  
- `handler` → Global exception handling ve JWT tabanlı hata yönetimi  
- `jwt` → JWT token üretimi ve doğrulama işlemleri  
- `mapper` → Entity ↔ DTO dönüşümleri (MapStruct)  
- `model` → Entity sınıfları  
- `repository` → Veri tabanı operasyonları (JPA)  
- `service` → İş mantığı katmanı


## 🎯 Proje Hakında

- 📚 Aktif ödünç kaydı olan kullanıcı silinemez  
- 🔢 Bir kullanıcı en fazla **5 kitap** ödünç alabilir  
- 📦 Ödünç alınan kitap iade edilene kadar tekrar alınamaz  
- ♻️ Soft delete ile veri kaybı engellenir  
- 🔐 Kullanıcılar sadece kendi verilerine erişebilir  

---

## 🧠 İş Mantığı ve Kısıtlamalar

### 🔄 Ödünç Yönetimi (`LoanServiceImpl`)

```java
private static final int MAX_BOOK_LIMIT = 5;
```
| Kısıtlama        | Açıklama                                                          |
|------------------|-------------------------------------------------------------------|
| Maksimum kitap   | `countByMemberIdAndReturnDateIsNull()` → limit aşılırsa hata      |
| Stok kontrolü    | `book.isAvailable()` → ödünçte olan kitap alınamaz                |
| Stok güncelleme  | `book.setAvailable(false)`                                        |
| İade             | `book.setAvailable(true)` + `loan.setReturnDate(LocalDate.now())` |
| Gecikme kontrolü | `countOverDueLoans()`                                             |


### 📖 Kitap Yönetimi (`BookServiceImpl`)

- 🔐 Yetkilendirme:

```java
@PreAuthorize("hasRole('ADMIN')")
```
- ❌ Ödünçte olan kitaplar (`isAvailable == false`) silinemez  
- ♻️ Soft delete uygulanır: `book.setActive(false);`



### 👤 Kullanıcı Yönetimi (`UserServiceImpl`)

- ❌ Aktif ödünç kaydı varsa kullanıcı silinemez
- 🔗 Kullanıcı silindiğinde:
  - Refresh tokenlar silinir
  - Üyelik pasif hale getirilir (soft delete)

---

### 🔐 Güvenlik Mimarisi (`AuthenticationService`)

- 🔒 Şifreleme: `BCryptPasswordEncoder`
- 🎟 Token yapısı: Access Token + Refresh Token
- 🔁 Refresh token rotation
- ⏱ Dinamik token süresi yönetimi

## 🏗 Mimari

```java
Controller → Security → Service → Repository
```


## 🚀 Canlı API
Aşağıdaki linkten projeyi swagger üzerinden canlı test edebilirsiniz

**Swagger:**  
[Swagger UI](https://library-management-system-1-ej9c.onrender.com/swagger-ui/index.html)

**Health Check:**  
`/api/health → UP`


## ♻️ Sistem Davranışı

- Her 30 dakikada bir → veritabanı sıfırlanır  
- Örnek veriler otomatik yüklenir  

---

## 🛠 Teknolojiler

- Java 17  
- Spring Boot 3  
- Spring Security  
- Spring Data JPA  
- PostgreSQL  
- JWT  
- Maven  

---

## 🧪 Test Kullanıcıları

| Rol    | Username | Password   |
|--------|--------- |------------|
| ADMIN  | admin1   | admin1231  |
| ADMIN  | admin2   | admin1232  |
| ADMIN  | admin3   | admin1233  |
| USER   | user1    | user1231   |
| USER   | user2    | user1232   |
| USER   | user3    | user1233   |


---

## 🎮 Test

1. `/authenticate` endpoint’ine istek at  
2. Token al  
3. Swagger → Authorize  
4. `Bearer <token>` ekle  
5. Test et  

---

## 👨‍💻 Geliştirici

**Berkay Kömür**  [LinkedIn](https://www.linkedin.com/in/berkya)

Aspiring Java & Spring Boot Developer 🚀
