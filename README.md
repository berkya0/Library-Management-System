# 📚 Library Management System (Spring Boot)
A secure and production-ready **Library Management System** built with Spring Boot.
This project demonstrates **JWT-based authentication**, **role-based authorization**, and a clean **layered REST API architecture**.

---

## 🚀 Features
* 🔐 JWT-based Authentication & Authorization
* 👥 Role-based Access Control (ADMIN, USER)
* 📖 Book Management (CRUD operations)
* 🧑 Member Management
* 🔄 Loan System (Borrow / Return books)
* ♻️ Refresh Token Mechanism
* 🧩 Global Exception Handling
* 🏗 Clean Layered Architecture (Controller → Service → Repository)
* 📦 DTO-based Data Transfer
* 🛡 Spring Security Integration

### 🧪 Testing
* ✅ **Unit Tests** → Business logic tested with JUnit 5 & Mockito
* 🔐 **Integration Tests** → End-to-end testing of secured API endpoints
---

## 🏛 Project Architecture
The project follows a clean and maintainable layered architecture:

<img src="images/architecture.png" alt="Architecture" width="700">

---

## 📂 Package Structure

* `config` → Security & application configuration
* `controller` → REST API endpoints
* `dto` → Data Transfer Objects
* `enums` → Role definitions
* `exception` → Custom exception classes
* `handler` → Global exception handling
* `jwt` → JWT filter & token service
* `mapper` → DTO ↔ Entity mapping
* `model` → Entity classes
* `repository` → JPA repositories
* `service` → Business logic layer

---

## 🔐 Authentication Flow

1. User registers
2. User logs in
3. Server returns:

   * Access Token (JWT)
   * Refresh Token
4. Access token is used for protected endpoints
5. When expired → Refresh token generates a new access token

Security configuration includes:

* `SecurityConfig`
* `JwtAuthenticationFilter`
* `JwtService`
* `CustomUserDetails`

---

## 🧠 Roles & Authorization

### ADMIN

* Full access to almost all endpoints

### USER

* Limited access
* Can only access their own data

**Example:**

```java
@PreAuthorize("hasRole('ADMIN') or #request.memberId == authentication.principal.memberId")
```

---

## 📚 Core Modules

### 📖 Book
* Create book
* Update book
* Delete book
* List all books

### 🧑 Member
* Register member
* Update member
* List members

### 🔄 Loan
* Borrow book
* Return book
* Track active loans

---

## 🛠 Technologies Used
* Java 17+
* Spring Boot
* Spring Security
* Spring Data JPA
* JWT
* Maven
* PostgreSQL (configurable)

---

## ⚙️ How to Run

### 1️⃣ Clone the repository
```bash
git clone https://github.com/berkya0/Library-Management-System.git
```

### 2️⃣ Configure Database
Update `application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/library
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3️⃣ Run the project
```
./mvnw spring-boot:run
```
or
```
mvn spring-boot:run
```

---

## 🧪 API Testing
You can test the API using:

* **Postman** → For sending HTTP requests (GET, POST, PUT, DELETE)
* **Browser** → For basic authentication flows
  
## 📸 API Usage Examples

### 🔐 Authentication – Login
Authenticate the user and retrieve JWT tokens.
**Request**

```json
{
  "username": "berkya",
  "password": "password123"
}
```
**Response** <img src="images/authResponse.png" width="600">


---
### 🔒 Borrow Book (Protected)
Borror book using a valid access token.
**Request**
```json
{
   "bookId": 7,
   "memberId": 1
}
```
**Response** <img src="images/loanResponse.png" width="600">

---

### ❌ Error Handling Example

Example of validation or authorization error response.

**Request**

```json
{
  "memberId": null,
  "bookId": 5
}
```

**Response** <img src="images/loanMemberIdError.png" width="600">


**Request**
```json
{
  "memberId": 5,
  "bookId": 5
}
```

**Response** <img src="images/unAuthError.png" width="600">

### Book
- GET /books
- POST /books (ADMIN)
- DELETE /books/{id} (ADMIN)

### Loan
- POST /loans/borrow
- POST /loans/return

📸 Example Requests:

<img src="images/get-book-postman.png" width="400">
<img src="images/update-member.png" width="400">
<img src="images/update-member-error.png" width="400">

---

## 🎯 What I Practiced

* Designing a secure REST API architecture
* Implementing JWT authentication from scratch
* Building a refresh token mechanism
* Applying role-based & ownership-based authorization
* Structuring scalable layered architecture
* Implementing global exception handling
* Using DTOs for clean API design
* Securing endpoints with method-level authorization

---

## 👨‍💻 Author

**Berkay Kömür**
Computer Engineering Student | Java & Spring Boot Developer 🚀
