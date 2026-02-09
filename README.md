## AdoptMe
AdoptMe is an animal adoption platform that connects adopters with individual listers and NGOs. It focuses on trust, clarity, and compassionate communication rather than commerce.

### Roles
- `USER`: browse pets, view details, chat with listers.
- `COMMON_LISTER`: list pets and manage their own listings.
- `NGO_LISTER`: list pets and manage their own listings.

### Core Features
- Email OTP verification (SendGrid).
- Pet listing with multi‑image support (Cloudinary).
- Search and filter by location and animal type.
- Chat between adopters and listers.
- Pet status and safety metadata (adoption status, vaccination, stray).
- Location context (landmark + optional maps link).

### Tech Stack
- Backend: Spring Boot, PostgreSQL, JPA/Hibernate, SendGrid, Cloudinary.
- Frontend: React, Vite, Tailwind CSS, Axios, SockJS + STOMP.

---

## Local Setup

### Prerequisites
- Node.js 18+
- Java 17+
- PostgreSQL 13+

### Backend
From `d:\Aadoptme\backend`:
```powershell
mvn -q -DskipTests compile
mvn spring-boot:run
```

### Frontend
From `d:\Aadoptme\frontend`:
```powershell
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`  
Backend runs on `http://localhost:8080`

---

## Configuration
Edit `backend/src/main/resources/application.properties`:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `sendgrid.api.key`
- `sendgrid.from.email`
- `cloudinary.cloud.name`
- `cloudinary.api.key`
- `cloudinary.api.secret`

Security note:
- Do not commit real API keys. Use environment‑specific config or secrets before pushing to GitHub.

---

## Pet Listing Fields
The pet model supports:
- `name`, `age`, `type`, `description`, `contactNumber`
- `location`, `landmark`, `locationLink`
- `vaccinationStatus`, `stray`
- `adoptionStatus` (`AVAILABLE`, `PENDING`, `ADOPTED`)
- `imageUrls` (multiple images)

---

## Common Commands
Backend compile:
```powershell
mvn -q -DskipTests compile
```

Frontend build:
```powershell
npm run build
```

---

## Troubleshooting
- If OTP emails fail: verify SendGrid sender identity and from address.
- If images fail to upload: verify Cloudinary credentials.
- If chat fails: ensure backend is running and WebSocket endpoint is reachable.
