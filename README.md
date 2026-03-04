# FlowTrack – Secure Financial Backend Platform

FlowTrack is a production-oriented Spring Boot REST API designed as the backend foundation for a secure and scalable financial tracking platform.

The system is structured with SaaS-style architecture, workspace-based collaboration, role-based permissions, and an expense approval workflow.

This project demonstrates real-world backend design patterns used in modern financial software systems.

---

# 🚀 Project Purpose

FlowTrack is built as a modular backend platform capable of evolving into a full SaaS expense management system.

The architecture prioritizes:

- Security
- Scalability
- Clean service layering
- Multi-user collaboration
- Financial data integrity

This repository serves both as:

• A backend engineering portfolio project  
• A real-world system design exercise

---

# 🏗 Current System Features

## 🔐 Secure Authentication

- User registration
- BCrypt password hashing
- Custom `UserDetailsService`
- Spring Security configuration
- Database-backed authentication
- Stateless-ready architecture

Passwords are never stored in plain text.

---

## 👥 Workspace System

Users operate inside **Workspaces**, allowing the platform to support both:

• Personal expense tracking  
• Business financial collaboration

Each workspace contains:

- Members
- Role-based permissions
- Shared expenses

---

## 👑 Role-Based Permissions

Workspace roles control financial authority.

| Role | Permissions |
|-----|-----|
| OWNER | Full control |
| ADMIN | Approve user expenses |
| USER | Submit expenses |

This structure mirrors real financial approval chains used in organizations.

---

## 💰 Expense Management

Users can:

- Submit expenses
- Update their expenses
- Delete their expenses
- Filter expenses
- Paginate results

Supported filters:

- Category
- Minimum amount
- Maximum amount

---

## ✔ Expense Approval Workflow

Business workspaces support a financial approval pipeline.

Expense statuses:

```
PENDING
APPROVED
REJECTED
```

Approval rules:

```
OWNER → can approve anyone
ADMIN → can approve USER expenses
USER → cannot approve expenses
```

---

## 📜 Financial Audit Trail

Every expense records:

```
submittedBy
approvedBy
status
timestamps
```

This creates a transparent approval history similar to real financial systems.

---

## 📊 Financial Data Queries

The backend supports financial insights including:

- Total expenses
- Expense count
- Category breakdown
- Monthly summaries
- Filtered queries with pagination

---

# 🧱 Backend Architecture

The project follows a layered architecture:

```
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

This ensures:

- Clear separation of concerns
- Maintainability
- Testability
- Scalability

---

# 🛠 Tech Stack

Core technologies used:

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

---

# 📡 Example API Endpoints

### Register User

```
POST /auth/register
```

Request:

```json
{
  "username": "jacob",
  "password": "test123",
  "role": "USER"
}
```

---

### Create Expense

```
POST /expenses
```

Creates a new expense record.

---

### Get User Expenses

```
GET /expenses
```

Supports filtering and pagination.

---

### Approve Expense

```
POST /admin/expenses/{id}/approve
```

Admin/Owner approval endpoint.

---

### Reject Expense

```
POST /admin/expenses/{id}/reject
```

Admin/Owner rejection endpoint.

---

# 📊 Project Maturity

Current Development Stage:

```
Backend Core System
```

### Completed

- Authentication infrastructure
- Workspace architecture
- Role-based permissions
- Expense domain modeling
- Expense approval workflow
- Filtering and pagination
- Financial summaries
- DTO response system
- Service layer architecture

---

# 🗺 Planned Features

### Authentication Improvements

- JWT authentication
- Token refresh
- Stateless authentication

### SaaS Infrastructure

- Workspace invitations
- Multi-user collaboration
- Workspace dashboards

### Developer Tooling

- Swagger API documentation
- Docker containerization
- CI/CD pipeline
- Production deployment configuration

---

# ⚙️ Setup Instructions

### Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/flowtrack.git
cd flowtrack
```

---

### Configure Environment Variables

Windows (PowerShell)

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="yourpassword"
```

Mac/Linux

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
```

---

### Run Application

```bash
./mvnw spring-boot:run
```

Server runs at:

```
http://localhost:8080
```

---

# 🧠 Design Philosophy

FlowTrack is designed as a production-oriented backend rather than a simple authentication demo.

Key architectural priorities:

- Security-first design
- Role-based financial controls
- Scalable workspace architecture
- Clean service layering
- Financial data integrity

---

# 📌 Status

Active development.

Current focus:

```
Expense system expansion
Workspace analytics
Production hardening
```