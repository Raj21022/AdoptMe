# 🚀 Week 1 - Git Commit Guide

## Step 1: Navigate to Your Backend Folder

```bash
cd /path/to/your/adoptme-backend
```

## Step 2: Initialize Git (if not already done)

```bash
git init
```

## Step 3: Add Remote Repository

```bash
git remote add origin https://github.com/Raj21022/AdoptMe.git
```

## Step 4: Create Backend Branch

It's good practice to keep backend and frontend in separate branches initially:

```bash
git checkout -b backend-week-1
```

## Step 5: Add All Files

```bash
git add .
```

## Step 6: Commit with Detailed Message

```bash
git commit -m "Week 1: Complete Backend Foundation

✅ Completed Tasks:
- Spring Boot project setup with all dependencies
- PostgreSQL database configuration
- Created all 4 entities (User, Animal, OTP, Message)
- Created all 4 repositories with custom queries
- Docker Compose for PostgreSQL
- Complete documentation and setup scripts

📦 Tech Stack:
- Java 17 + Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- WebSocket for real-time chat
- SendGrid for email
- Lombok for clean code

🗄️ Database Schema:
- Users table with role-based access
- Animals table with adoption tracking
- OTPs table for passwordless auth
- Messages table for real-time chat

🎯 Next: Week 2 - OTP Authentication & JWT"
```

## Step 7: Push to GitHub

```bash
git push -u origin backend-week-1
```

## Step 8: Create Pull Request (Optional)

Go to: https://github.com/Raj21022/AdoptMe
- Click "Compare & pull request"
- Title: "Week 1: Backend Foundation Complete ✅"
- Description: Use the WEEK_1_COMPLETION.md content
- Merge to main when ready

---

## Alternative: Direct Push to Main

If you prefer to push directly to main:

```bash
git checkout -b main
git add .
git commit -m "Week 1: Complete Backend Foundation ✅"
git push -u origin main
```

---

## 📁 Folder Structure to Commit

```
adoptme-backend/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── src/
│   └── main/
│       ├── java/com/raj/adoptme/
│       │   ├── entity/
│       │   │   ├── Animal.java
│       │   │   ├── Message.java
│       │   │   ├── Otp.java
│       │   │   └── User.java
│       │   ├── repository/
│       │   │   ├── AnimalRepository.java
│       │   │   ├── MessageRepository.java
│       │   │   ├── OtpRepository.java
│       │   │   └── UserRepository.java
│       │   └── AdoptMeApplication.java
│       └── resources/
│           └── application.yml
├── .gitignore
├── docker-compose.yml
├── pom.xml
├── README.md
├── setup.sh
├── WEEK_1_COMPLETION.md
└── GIT_COMMIT_GUIDE.md
```

---

## 🔍 Verify Before Pushing

```bash
# Check what will be committed
git status

# Review changes
git diff

# See commit history
git log --oneline
```

---

## 🎉 After Pushing

1. ✅ Check GitHub to see your code
2. ✅ Verify all files are there
3. ✅ Update README if needed
4. ✅ Share the repo link (optional)
5. ✅ Take a screenshot for your portfolio!

---

**You've just completed Week 1 of a production-ready full-stack application! 🔥**
