# 🔐 Environment Variables Setup

## Why Environment Variables?

We use environment variables to keep sensitive data (like API keys) OUT of the code repository. This is a security best practice.

---

## 🚀 Quick Setup

### Step 1: Create .env File

Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

### Step 2: Add Your API Keys

Edit `.env` and add your actual SendGrid API key:

```env
SENDGRID_API_KEY=SG.your-actual-api-key-here
SENDGRID_FROM_EMAIL=adoptmenoreply7@gmail.com
```

**⚠️ IMPORTANT:** The `.env` file is in `.gitignore` and will NOT be pushed to GitHub!

---

## 💻 Running the Application

### Option 1: Using PowerShell (Windows)

Set environment variables before running:

```powershell
# Set environment variables
$env:SENDGRID_API_KEY="SG.your-api-key-here"
$env:SENDGRID_FROM_EMAIL="adoptmenoreply7@gmail.com"

# Run application
mvn spring-boot:run
```

### Option 2: Using IntelliJ IDEA

1. Go to Run → Edit Configurations
2. Select your Spring Boot configuration
3. Add Environment Variables:
   ```
   SENDGRID_API_KEY=SG.your-api-key-here
   SENDGRID_FROM_EMAIL=adoptmenoreply7@gmail.com
   ```
4. Click OK and run

### Option 3: Using VS Code

Create `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot",
      "request": "launch",
      "mainClass": "com.raj.adoptme.AdoptMeApplication",
      "env": {
        "SENDGRID_API_KEY": "SG.your-api-key-here",
        "SENDGRID_FROM_EMAIL": "adoptmenoreply7@gmail.com"
      }
    }
  ]
}
```

---

## ✅ Verify It's Working

After setting environment variables and running the app, test:

**Send OTP:**
```bash
curl -X POST http://localhost:8080/api/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email": "your-email@gmail.com", "name": "Your Name"}'
```

You should receive an email with the OTP!

---

## 🔒 Security Notes

1. ✅ **Never commit .env files** - They contain secrets!
2. ✅ **.env is in .gitignore** - Git will ignore it automatically
3. ✅ **Use .env.example** - Shows structure without real keys
4. ✅ **Rotate API keys regularly** - Change them periodically
5. ✅ **Don't share keys** - Each developer should have their own

---

## 🚨 If You Accidentally Committed API Keys

If you accidentally pushed API keys to GitHub:

1. **Revoke the API key immediately** in SendGrid dashboard
2. **Create a new API key**
3. **Update your .env file** with new key
4. **Remove from Git history** (see GIT_SECURITY_FIX.md)

---

## 📝 Production Deployment

For production (Render, Heroku, AWS, etc.):

1. Set environment variables in your hosting platform's dashboard
2. DO NOT use .env files in production
3. Use the platform's secret management

**Example for Render:**
- Go to Environment tab
- Add: `SENDGRID_API_KEY` = your-key
- Add: `SENDGRID_FROM_EMAIL` = your-email

---

**Your API keys are now secure! 🔐**
