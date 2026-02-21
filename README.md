# FlowTrack – Secure Financial Backend Platform

FlowTrack is a Spring Boot REST API designed as the foundation for a secure, scalable financial tracking system.

This project demonstrates production-oriented backend architecture, authentication design, and database-driven security implementation.

It serves as:

- A backend portfolio project
- A scalable authentication foundation
- The base of a future expense tracking system

---

## 🚀 Project Purpose

FlowTrack is being built as a modular backend system capable of evolving into a SaaS-ready financial platform.

The goal is to structure the system as if it were deployable to production, even while remaining open-source for learning and portfolio purposes.

---

## 🏗 Current Features

### 🔐 Authentication System
- User registration
- BCrypt password hashing
- Database-backed authentication
- Custom `UserDetailsService`
- HTTP Basic authentication
- Protected endpoints

### 🧱 Architecture
- Layered structure (Controller → Service → Repository)
- Spring Security configuration
- JPA entity modeling
- PostgreSQL persistence
- Environment-based configuration support

---

## 🔄 Authentication Flow

1. Client sends request with Basic Authentication header
2. Spring Security intercepts the request
3. `CustomUserDetailsService` loads user from database
4. BCrypt verifies password
5. Authentication object is created
6. Request proceeds to protected endpoint

Passwords are never stored in plain text.

---

## 📡 Current API Endpoints

### 1️⃣ Register User

**POST** `/auth/register`

#### Request Body

```json
{
  "username": "jacob",
  "password": "test123",
  "role": "USER"
}
```

#### Response

```
User registered successfully
```

---

### 2️⃣ Authentication Test

**GET** `/auth/test`

Requires Basic Authentication header.

#### Response

```
You are now authenticated.
```

---

## 🛠 Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

---

## 📊 Project Maturity Level

**Current Stage:** Secure Backend Foundation (MVP Phase 1)

### ✅ Completed
- Authentication infrastructure
- Password encryption
- Database persistence
- Secure endpoint protection
- Clean architectural layering

### 🔜 Planned
- JWT-based authentication
- Role-based authorization
- Expense domain modeling
- DTO layer implementation
- Global exception handling
- API documentation (Swagger)
- Dockerization
- CI/CD pipeline

---

## 🗺 Roadmap

### Phase 1 – Authentication Upgrade
- Replace Basic Auth with JWT
- Implement stateless authentication
- Add token expiration and refresh tokens

### Phase 2 – Authorization
- Role-based endpoint restrictions
- Admin-only routes
- User-specific data isolation

### Phase 3 – Expense Domain
- Expense entity implementation
- CRUD operations
- Category filtering
- Date filtering
- Financial summaries

### Phase 4 – Production Hardening
- DTO separation
- Validation layer
- Centralized exception handling
- Swagger documentation
- Docker containerization
- Environment profiles
- Deployment readiness

---

## ⚙️ Setup Instructions

### 1️⃣ Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/flowtrack.git
cd flowtrack
```

### 2️⃣ Configure Environment Variables

#### Windows (PowerShell)

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="yourpassword"
```

#### Mac/Linux

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
```

### 3️⃣ Run Application

```bash
./mvnw spring-boot:run
```

Application runs at:

```
http://localhost:8080
```

---

## 🧠 Design Philosophy

FlowTrack is intentionally structured as a scalable backend platform rather than a simple authentication demo.

The architecture emphasizes:

- Separation of concerns
- Secure authentication practices
- Clean layering
- Future scalability
- Production-oriented structure

---

## 📌 Status

Active development.

Authentication layer complete.  
Expense tracking domain implementation in progress.