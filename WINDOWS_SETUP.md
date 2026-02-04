# 🪟 SIMPLE WINDOWS SETUP - AdoptMe Backend

## ✅ Easy Way: Use Maven Directly

You don't need the Maven wrapper! Just use `mvn` command:

### Step 1: Check if Maven is Installed
```powershell
mvn -version
```

**If you see Maven version:** Great! Continue below.

**If "mvn is not recognized":** Install Maven first (see bottom of this file)

---

### Step 2: Start PostgreSQL

**Using Docker Desktop:**
1. Make sure Docker Desktop is **running** (check system tray)
2. Then run:
```powershell
cd D:\adoptme-backend
docker-compose up -d
```

**OR Using Local PostgreSQL:**
1. Open **pgAdmin 4**
2. Create database: `adoptme_db`
3. Update password in `src\main\resources\application.yml` if needed

---

### Step 3: Build & Run the Application

```powershell
# Navigate to project
cd D:\adoptme-backend

# Build project (first time - takes 2-3 minutes)
mvn clean install

# Run Spring Boot
mvn spring-boot:run
```

**Expected Output:**
```
Started AdoptMeApplication in X.XXX seconds
```

---

### Step 4: Verify Database Tables Created

Open **pgAdmin 4**:
1. Expand Servers → PostgreSQL → Databases → adoptme_db
2. Expand Schemas → public → Tables
3. You should see:
   - users
   - animals
   - otps
   - messages
   - animal_images

---

## 🚀 Alternative: Use IntelliJ IDEA (Easiest!)

### Step 1: Open Project
1. Open **IntelliJ IDEA**
2. File → Open
3. Select `D:\adoptme-backend` folder
4. Wait for Maven to download dependencies (2-3 minutes)

### Step 2: Start PostgreSQL
```powershell
docker-compose up -d
```
(Or use local PostgreSQL with pgAdmin)

### Step 3: Run Application
1. Find `AdoptMeApplication.java` in Project Explorer
2. Right-click it
3. Click **"Run 'AdoptMeApplication'"**

Done! Application will start automatically! ✅

---

## 📦 Install Maven (If Needed)

### Option 1: Using Chocolatey (Recommended)
```powershell
# Install Chocolatey first if you don't have it
# Run PowerShell as Administrator
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Then install Maven
choco install maven -y

# Verify
mvn -version
```

### Option 2: Manual Installation
1. Download: https://maven.apache.org/download.cgi
2. Download `apache-maven-3.9.x-bin.zip`
3. Extract to `C:\Program Files\Apache\maven`
4. Add to PATH:
   - Windows Key → Search "Environment Variables"
   - System Variables → Path → Edit → New
   - Add: `C:\Program Files\Apache\maven\bin`
5. Restart PowerShell/Command Prompt
6. Verify: `mvn -version`

---

## 🎯 Quick Commands Summary

```powershell
# Start database
docker-compose up -d

# Build
mvn clean install

# Run
mvn spring-boot:run

# Stop database
docker-compose down

# View logs
docker-compose logs -f
```

---

## ✅ Success Checklist

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven installed (`mvn -version`)
- [ ] PostgreSQL running (Docker or local)
- [ ] Database `adoptme_db` created
- [ ] Application builds without errors
- [ ] Application runs on http://localhost:8080
- [ ] Tables created in database

---

**Once running successfully, you're ready to push to GitHub!** 🚀
