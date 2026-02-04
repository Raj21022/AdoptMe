# 🐾 AdoptMe - Pet Adoption Platform

A full-stack pet adoption platform with real-time chat, built with Spring Boot and React Native.

## 📋 Project Progress

### ✅ Week 1 - Foundation & Backend Skeleton (COMPLETE)
- [x] Spring Boot project setup with all dependencies
- [x] PostgreSQL database configuration
- [x] Complete folder structure
- [x] All entity classes created
- [x] JPA repositories for all entities
- [x] Docker Compose for PostgreSQL

### ✅ Week 2 - Authentication (OTP + JWT) (COMPLETE)
- [x] OTP generation service
- [x] SendGrid email integration (adoptmenoreply7@gmail.com)
- [x] OTP verification endpoint
- [x] JWT token generation & validation
- [x] JWT authentication filter
- [x] Spring Security configuration
- [x] Role-based access control (ADOPTER, LISTER, ADMIN)
- [x] Authentication APIs (send-otp, verify-otp)
- [x] Test endpoints for protected routes

### 🛠️ Tech Stack
**Backend:**
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- WebSocket
- JWT Authentication
- SendGrid Email Service
- Lombok

**Frontend (Coming in Week 6):**
- React Native with Expo
- React Native Web for website

### 📁 Project Structure
```
adoptme-backend/
├── src/
│   └── main/
│       ├── java/com/raj/adoptme/
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── Animal.java
│       │   │   ├── Otp.java
│       │   │   └── Message.java
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   ├── AnimalRepository.java
│       │   │   ├── OtpRepository.java
│       │   │   └── MessageRepository.java
│       │   └── AdoptMeApplication.java
│       └── resources/
│           └── application.yml
├── docker-compose.yml
├── pom.xml
└── README.md
```

### 🚀 Getting Started

#### Prerequisites
- Java 17 or higher
- PostgreSQL (or Docker)
- Maven
- Your favorite IDE (IntelliJ IDEA recommended)

#### Option 1: Using Docker (Recommended)
```bash
# Start PostgreSQL
docker-compose up -d

# Run the application
./mvnw spring-boot:run
```

#### Option 2: Using Local PostgreSQL
1. Create database:
```sql
CREATE DATABASE adoptme_db;
```

2. Update `application.yml` with your credentials

3. Run the application:
```bash
./mvnw spring-boot:run
```

### ✅ Verification
After running, you should see:
- Application starts on `http://localhost:8080`
- No errors in console
- Tables auto-created in PostgreSQL:
  - `users`
  - `animals`
  - `otps`
  - `messages`
  - `animal_images`

### 🗄️ Database Schema Overview

**Users Table:**
- Stores user information
- Roles: ADOPTER, LISTER, ADMIN
- Email verification and blocking support

**Animals Table:**
- Pet details (name, species, breed, age, gender)
- Adoption status tracking
- Multiple image support
- Links to lister (User)

**OTPs Table:**
- One-Time Password for authentication
- Expiration and usage tracking
- Links to user

**Messages Table:**
- Real-time chat between users
- Read status tracking
- Animal context support

### 🔐 Security Features
- JWT-based authentication (coming in Week 2)
- OTP email verification
- Role-based access control
- Password-less login

### 📝 Next Steps (Week 3)
- [ ] Create Animal APIs (CRUD operations)
- [ ] Image upload support
- [ ] Search & filter functionality
- [ ] Adoption status management
- [ ] Only listers can add animals
- [ ] Only lister/admin can update status

### 🤝 Contributing
This is a learning project following a 10-week roadmap. Check the main README for the complete plan.

### 📧 Contact
GitHub: [@Raj21022](https://github.com/Raj21022)

---
**Week 1 Status:** ✅ COMPLETE - Foundation is solid!
