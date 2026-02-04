# 🧪 Week 2 - Testing Guide

## 🎯 What We're Testing

Week 2 implemented a complete authentication system. Let's test every feature!

---

## 🚀 Step-by-Step Testing

### Prerequisites
- ✅ Application running on `http://localhost:8080`
- ✅ Postman installed (or use cURL)
- ✅ Your email ready (you'll receive real OTP)

---

## TEST 1: Health Check ✅

**Purpose:** Verify server is running

**Endpoint:** `GET /api/auth/test`

**Postman:**
1. Method: GET
2. URL: `http://localhost:8080/api/auth/test`
3. Click Send

**Expected Response:**
```json
{
  "success": true,
  "message": "Auth API is working!",
  "data": "Test successful"
}
```

**✅ If you see this, proceed to Test 2**

---

## TEST 2: Send OTP (Register New User) 📧

**Purpose:** Register new user and receive OTP via email

**Endpoint:** `POST /api/auth/send-otp`

**Postman:**
1. Method: POST
2. URL: `http://localhost:8080/api/auth/send-otp`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "email": "YOUR_EMAIL@gmail.com",
  "name": "Your Name"
}
```
5. Click Send

**Expected Response:**
```json
{
  "success": true,
  "message": "OTP sent successfully to YOUR_EMAIL@gmail.com",
  "data": null
}
```

**✅ Actions:**
1. Check your email inbox
2. Look for email from: adoptmenoreply7@gmail.com
3. Subject: "Your AdoptMe Verification Code"
4. Copy the 6-digit OTP code
5. **Check spam folder if not in inbox!**

**Example OTP Email:**
```
Hi Your Name,

Welcome to AdoptMe! Your verification code is:

   1 2 3 4 5 6

This code will expire in 5 minutes.
```

---

## TEST 3: Verify OTP & Get JWT Token 🔑

**Purpose:** Verify OTP and receive JWT token for authentication

**Endpoint:** `POST /api/auth/verify-otp`

**Postman:**
1. Method: POST
2. URL: `http://localhost:8080/api/auth/verify-otp`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "email": "YOUR_EMAIL@gmail.com",
  "otpCode": "123456"
}
```
5. Click Send

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiQURPUFRFUiIsInVzZXJJZCI6MSw...",
    "email": "YOUR_EMAIL@gmail.com",
    "name": "Your Name",
    "role": "ADOPTER",
    "userId": 1,
    "message": "Login successful"
  }
}
```

**✅ IMPORTANT:** 
- Copy the entire `token` value
- Save it in Notepad for next tests
- This token is valid for 24 hours

---

## TEST 4: Access Protected Endpoint 🛡️

**Purpose:** Use JWT token to access protected API

**Endpoint:** `GET /api/test/protected`

**Postman:**
1. Method: GET
2. URL: `http://localhost:8080/api/test/protected`
3. Headers:
   - Key: `Authorization`
   - Value: `Bearer YOUR_JWT_TOKEN_HERE`
   *(Note: Must include "Bearer " before token)*
4. Click Send

**Expected Response:**
```json
{
  "success": true,
  "message": "Protected endpoint - you are authenticated!",
  "data": {
    "email": "YOUR_EMAIL@gmail.com",
    "authorities": "[ROLE_ADOPTER]"
  }
}
```

**✅ Success! You're authenticated!**

---

## TEST 5: Try Without Token (Should Fail) ❌

**Purpose:** Verify security is working

**Endpoint:** `GET /api/test/protected`

**Postman:**
1. Method: GET
2. URL: `http://localhost:8080/api/test/protected`
3. NO Authorization header
4. Click Send

**Expected Response (403 Forbidden):**
```json
{
  "timestamp": "2026-02-03T22:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/test/protected"
}
```

**✅ Perfect! Unauthorized users are blocked!**

---

## TEST 6: Test Role-Based Access 👥

**Purpose:** Verify role-based authorization

### Test Adopter Endpoint
**Endpoint:** `GET /api/test/adopter`

**Postman:**
1. Method: GET
2. URL: `http://localhost:8080/api/test/adopter`
3. Headers: `Authorization: Bearer YOUR_TOKEN`
4. Click Send

**Expected Response (✅ Success):**
```json
{
  "success": true,
  "message": "Adopter endpoint - only adopters can access",
  "data": null
}
```

### Test Admin Endpoint (Should Fail for Adopter)
**Endpoint:** `GET /api/test/admin`

**Postman:**
1. Method: GET
2. URL: `http://localhost:8080/api/test/admin`
3. Headers: `Authorization: Bearer YOUR_TOKEN`
4. Click Send

**Expected Response (❌ 403 Forbidden):**
```json
{
  "timestamp": "2026-02-03T22:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/test/admin"
}
```

**✅ Perfect! Role-based access is working!**

---

## TEST 7: Verify Database Changes 🗄️

**Purpose:** Check data was saved correctly

**PostgreSQL:**
```powershell
# Connect to database
docker exec -it adoptme-postgres psql -U postgres -d adoptme_db
```

**Check Users:**
```sql
SELECT id, email, name, role, is_verified, created_at 
FROM users 
ORDER BY id DESC 
LIMIT 5;
```

**Expected:**
```
 id |        email         |    name     |  role   | is_verified |       created_at
----+----------------------+-------------+---------+-------------+---------------------
  1 | YOUR_EMAIL@gmail.com | Your Name   | ADOPTER | t           | 2026-02-03 22:00:00
```

**Check OTPs:**
```sql
SELECT id, otp_code, is_used, used_at, expires_at 
FROM otps 
ORDER BY id DESC 
LIMIT 5;
```

**Expected:**
```
 id | otp_code | is_used |       used_at       |      expires_at
----+----------+---------+---------------------+---------------------
  1 |  123456  | t       | 2026-02-03 22:01:00 | 2026-02-03 22:05:00
```

**✅ Data is persisted correctly!**

---

## 🎭 Testing Scenarios

### Scenario 1: First-Time User Registration
1. Send OTP with new email ✅
2. Receive email ✅
3. Verify OTP ✅
4. Get JWT token ✅
5. User marked as verified ✅

### Scenario 2: Returning User Login
1. Send OTP with existing email ✅
2. Receive new OTP ✅
3. Old OTPs invalidated ✅
4. Verify new OTP ✅
5. Get new JWT token ✅

### Scenario 3: Invalid OTP
1. Send OTP ✅
2. Try wrong OTP code ❌
3. Get "Invalid or expired OTP" error ✅

### Scenario 4: Expired OTP
1. Send OTP ✅
2. Wait 6 minutes ⏰
3. Try to verify ❌
4. Get "Invalid or expired OTP" error ✅

### Scenario 5: Reusing OTP
1. Send OTP ✅
2. Verify OTP ✅
3. Try same OTP again ❌
4. Get "Invalid or expired OTP" error ✅

---

## 📊 Success Criteria

| Test | Description | Expected Result |
|------|-------------|-----------------|
| ✅ 1 | Health check | API is working |
| ✅ 2 | Send OTP | Email received with 6-digit code |
| ✅ 3 | Verify OTP | JWT token returned |
| ✅ 4 | Protected endpoint | Access granted with token |
| ✅ 5 | No token | Access denied (403) |
| ✅ 6 | Role-based access | Correct permissions enforced |
| ✅ 7 | Database | Data persisted correctly |

**All tests passing? Week 2 is COMPLETE! 🎉**

---

## 🐛 Troubleshooting

### "Failed to send OTP email"
**Cause:** SendGrid issue
**Fix:**
1. Check SendGrid API key in application.yml
2. Verify sender email is verified in SendGrid
3. Check application logs for specific error

### "Invalid or expired OTP"
**Causes:**
1. OTP expired (>5 minutes old)
2. OTP already used
3. Wrong OTP code entered
4. User entered different email

**Fix:** Request new OTP

### Email not received
**Causes:**
1. In spam folder
2. SendGrid rate limit
3. Email typo

**Fix:**
1. Check spam
2. Wait 1 minute and retry
3. Verify email address

### "Access Denied" with valid token
**Causes:**
1. Token format wrong (missing "Bearer ")
2. Token expired (>24 hours)
3. User lacks required role

**Fix:**
1. Check Authorization header format
2. Get new token
3. Check user role

---

## 🎉 Congratulations!

If all tests pass, you now have:
- ✅ Working passwordless authentication
- ✅ Real email delivery via SendGrid
- ✅ Secure JWT-based sessions
- ✅ Role-based access control
- ✅ Production-ready auth system

**Next: Push to GitHub and move to Week 3!** 🚀
