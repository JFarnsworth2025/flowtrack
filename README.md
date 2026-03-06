# FlowTrack – Secure Financial Backend Platform

FlowTrack is a **production-oriented Spring Boot REST API** designed as the backend foundation for a secure and scalable financial tracking platform.

The system uses **SaaS-style architecture**, workspace-based collaboration, role-based permissions, and an expense approval workflow.

This project demonstrates real-world backend design patterns used in **modern financial software systems**.

---

# 🚀 Project Purpose

FlowTrack is built as a modular backend platform capable of evolving into a **full SaaS expense management system**.

The architecture prioritizes:

* Security
* Scalability
* Clean service layering
* Multi-user collaboration
* Financial data integrity

This repository serves both as:

• A **backend engineering portfolio project**
• A **real-world system design exercise**

---

# 🏗 System Features

## 🔐 Secure Authentication

* User registration
* BCrypt password hashing
* Custom `UserDetailsService`
* Spring Security configuration
* Database-backed authentication

Passwords are **never stored in plain text**.

---

## 👥 Workspace System

Users operate inside **Workspaces**, enabling the platform to support both:

• Personal expense tracking
• Business financial collaboration

Each workspace contains:

* Members
* Role-based permissions
* Shared expenses

---

## 👑 Role-Based Permissions

Workspace roles control financial authority.

| Role  | Permissions           |
| ----- | --------------------- |
| OWNER | Full control          |
| ADMIN | Approve user expenses |
| USER  | Submit expenses       |

This structure mirrors **real financial approval chains used in organizations**.

---

## 💰 Expense Management

Users can:

* Submit expenses
* Update expenses
* Delete expenses
* Filter expenses
* Paginate results

Supported filters:

* Category
* Minimum amount
* Maximum amount

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

This creates a **transparent approval history similar to real financial systems**.

Expense actions are logged in an **activity history system**.

Example:

```
Expense Submitted
Expense Approved
Expense Rejected
Expense Updated
```

---

## 📊 Financial Data Queries

The backend supports financial analytics including:

* Total expenses
* Expense count
* Category breakdown
* Monthly summaries
* Filtered queries with pagination

---

# 📡 Example API Endpoints

### Authentication

```
POST /auth/register
POST /auth/login
```

---

### Expense Management

```
POST /expenses
POST /expenses/bulk
GET /expenses
PUT /expenses/{id}
DELETE /expenses/{id}
```

---

### Expense Activity

```
GET /expenses/{id}/activity
```

---

### Expense Analytics

```
GET /expenses/summary
GET /expenses/summary/categories
GET /expenses/summary/monthly
```

---

# 🧱 Backend Architecture

The project follows a **layered architecture**:

```
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

Benefits:

* Clear separation of concerns
* Maintainability
* Testability
* Scalability

---

# 🛠 Tech Stack

Core technologies used:

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* PostgreSQL
* Lombok
* Maven

---

# 📊 Project Maturity

Current Development Stage:

```
Backend Core System
```

### Completed

* Authentication infrastructure
* Workspace architecture
* Role-based permissions
* Expense domain modeling
* Expense approval workflow
* Expense activity tracking
* Filtering and pagination
* Financial summaries
* DTO response system
* Service layer architecture

---

# 🗺 Planned Features

### Authentication Improvements

* JWT authentication
* Token refresh
* Stateless authentication

### SaaS Infrastructure

* Workspace invitations
* Multi-user collaboration
* Workspace dashboards

### Developer Tooling

* Swagger API documentation
* Docker containerization
* CI/CD pipeline
* Production deployment configuration

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

FlowTrack is designed as a **production-oriented backend system** rather than a simple authentication demo.

Key architectural priorities:

* Security-first design
* Role-based financial controls
* Scalable workspace architecture
* Clean service layering
* Financial data integrity

---

# 📌 Status

Active development.

Current focus:

```
Expense system expansion
Workspace analytics
Production hardening
```
