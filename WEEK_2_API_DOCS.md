# 🔐 Week 2 - Authentication API Documentation

## Overview
Week 2 implements a **passwordless authentication system** using OTP (One-Time Password) sent via email and JWT (JSON Web Token) for session management.

---

## 🚀 Authentication Flow

```
1. User enters email & name → POST /api/auth/send-otp
2. System generates 6-digit OTP
3. OTP sent to user's email via SendGrid
4. User enters OTP → POST /api/auth/verify-otp
5. System validates OTP
6. System returns JWT token
7. User uses JWT token in Authorization header for protected APIs
```

---

## 📡 API Endpoints

### 1. Send OTP (Register/Login)

**Endpoint:** `POST /api/auth/send-otp`

**Description:** Send OTP to user's email. Creates new user if doesn't exist.

**Request Body:**
```json
{
  "email": "user@example.com",
  "name": "John Doe"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "OTP sent successfully to user@example.com",
  "data": null
}
```

**Response (Error - 500):**
```json
{
  "success": false,
  "message": "Failed to send OTP email. Please try again.",
  "data": null
}
```

---

### 2. Verify OTP & Login

**Endpoint:** `POST /api/auth/verify-otp`

**Description:** Verify OTP and return JWT token

**Request Body:**
```json
{
  "email": "user@example.com",
  "otpCode": "123456"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "user@example.com",
    "name": "John Doe",
    "role": "ADOPTER",
    "userId": 1,
    "message": "Login successful"
  }
}
```

**Response (Error - 401):**
```json
{
  "success": false,
  "message": "Invalid or expired OTP",
  "data": null
}
```

---

### 3. Test Public Endpoint

**Endpoint:** `GET /api/test/public`

**Description:** Test endpoint (no authentication required)

**Response:**
```json
{
  "success": true,
  "message": "Public endpoint - no authentication required",
  "data": null
}
```

---

### 4. Test Protected Endpoint

**Endpoint:** `GET /api/test/protected`

**Description:** Protected endpoint (requires JWT token)

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Protected endpoint - you are authenticated!",
  "data": {
    "email": "user@example.com",
    "authorities": "[ROLE_ADOPTER]"
  }
}
```

**Response (No Token - 403):**
```json
{
  "timestamp": "2026-02-03T22:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/test/protected"
}
```

---

### 5. Test Admin Endpoint

**Endpoint:** `GET /api/test/admin`

**Description:** Admin-only endpoint (requires ADMIN role)

**Headers:**
```
Authorization: Bearer <admin-jwt-token>
```

**Response (Admin user):**
```json
{
  "success": true,
  "message": "Admin endpoint - only admins can access",
  "data": null
}
```

**Response (Non-admin - 403):**
```json
{
  "timestamp": "2026-02-03T22:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

---

## 🧪 Testing with Postman/cURL

### Test 1: Send OTP
```bash
curl -X POST http://localhost:8080/api/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-email@gmail.com",
    "name": "Your Name"
  }'
```

### Test 2: Check Your Email
- Check your inbox for OTP (6-digit code)
- Check spam folder if not in inbox
- OTP expires in 5 minutes

### Test 3: Verify OTP
```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-email@gmail.com",
    "otpCode": "123456"
  }'
```

### Test 4: Save JWT Token
Copy the token from response

### Test 5: Test Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## 🔑 JWT Token Structure

The JWT token contains:
```json
{
  "sub": "user@example.com",
  "role": "ADOPTER",
  "userId": 1,
  "iat": 1706995200,
  "exp": 1707081600
}
```

**Token is valid for 24 hours**

---

## 👥 User Roles

| Role | Description | Default |
|------|-------------|---------|
| `ADOPTER` | Regular user looking to adopt | ✅ Default |
| `LISTER` | User who can list animals | Manual |
| `ADMIN` | Full system access | Manual |

---

## 🔒 Security Features

1. **Passwordless Authentication**
   - No password storage
   - OTP-based verification
   - Reduced security risks

2. **JWT Token**
   - Stateless authentication
   - 24-hour expiration
   - Contains user role for authorization

3. **OTP Security**
   - 6-digit random code
   - 5-minute expiration
   - One-time use only
   - Auto-invalidated after use

4. **Email Verification**
   - Users marked as verified after first successful OTP
   - SendGrid integration for reliable delivery

5. **Role-Based Access Control**
   - Different endpoints for different roles
   - Spring Security annotations
   - Easy to extend

---

## 🛠️ Configuration

### application.yml Settings

```yaml
jwt:
  secret: your-secret-key-should-be-at-least-256-bits-long
  expiration: 86400000 # 24 hours

sendgrid:
  api-key: YOUR_SENDGRID_API_KEY
  from-email: adoptmenoreply7@gmail.com

otp:
  expiration: 300000 # 5 minutes
  length: 6
```

---

## 🐛 Troubleshooting

### OTP Email Not Received
1. Check spam folder
2. Verify SendGrid API key is correct
3. Verify sender email is verified in SendGrid
4. Check application logs for errors

### Invalid JWT Token
1. Token expired (24 hours)
2. Token format incorrect (must be `Bearer <token>`)
3. User was blocked/deleted

### OTP Expired
1. OTP is valid for only 5 minutes
2. Request new OTP using `/send-otp`

---

## ✅ Week 2 Achievements

- ✅ OTP generation service
- ✅ SendGrid email integration
- ✅ OTP verification
- ✅ JWT token generation
- ✅ JWT authentication filter
- ✅ Spring Security configuration
- ✅ Role-based access control
- ✅ Exception handling
- ✅ API documentation

---

## 🎯 Next Steps (Week 3)

Week 3 will implement:
- Animal listing APIs (CRUD operations)
- Image upload support
- Search & filter functionality
- Only listers can add animals
- Only lister/admin can update adoption status

---

**Ready to test? Follow the testing steps above!** 🚀
