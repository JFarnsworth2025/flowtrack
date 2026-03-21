# FlowTrack — Expense Tracking SaaS Backend

FlowTrack is a production-style backend application for managing shared expenses across teams and workspaces. It is designed to demonstrate scalable architecture, clean code practices, and real-world backend features used in SaaS applications.

Built as a portfolio project to demonstrate backend engineering skills and real-world SaaS architecture.

---

## 🚀 Tech Stack

* **Java 17**
* **Spring Boot 3**
* **Spring Security (JWT Authentication)**
* **Spring Data JPA**
* **PostgreSQL**
* **Lombok**

---

## 🧱 Architecture

The application follows a **modular service-oriented architecture**, separating responsibilities into distinct domains:

```
auth
workspace
member
expense
budget
security
common
```

### Expense Module (Example)

```
expense
 ├── controller
 ├── repository
 ├── mapper
 ├── specification
 ├── service
 └── comments
```

Services are further split into focused responsibilities:

* `ExpenseCommandService` — create/update/delete
* `ExpenseQueryService` — dashboards & analytics
* `ExpenseSearchService` — filtering (Specifications)
* `ExpenseApprovalService` — approval workflows
* `ExpenseActivityService` — audit logging
* `ExpenseAnalyticsService` — summaries
* `ExpenseAuthorizationService` — permission checks

---

## 🔐 Authentication & Security

* JWT-based authentication
* Custom `UserDetails` implementation
* Role-based access control (OWNER, ADMIN, USER)
* Workspace-level authorization

### Endpoints

```
POST /auth/register
POST /auth/login
GET  /auth/me
```

---

## 🏢 Workspace System

* Users create and join workspaces
* Role-based permissions:

    * OWNER
    * ADMIN
    * USER
* Workspace membership enforced across all operations

---

## 💸 Expense System

### Features

* Create, update, delete expenses
* Expense statuses:

    * PENDING
    * APPROVED
    * REJECTED
* Activity tracking (audit trail)
* Pagination and filtering

### Search Example

```
GET /expenses/search
?workspaceId=1
&keyword=uber
&category=travel
&status=APPROVED
&min=10
&max=200
```

---

## 🧾 Receipt Uploads

* Multipart file upload support
* File storage abstraction (local filesystem)
* Secure file handling (path validation)
* Download endpoint for receipts

---

## 💬 Expense Comments

* Add comments to expenses
* Threaded replies (parent-child comments)
* Workspace-level access control

---

## 📊 Budget System

* Category-based monthly budgets per workspace
* Real-time spending calculation
* Budget status tracking:

Example:

```json
{
  "category": "TRAVEL",
  "limit": 500,
  "spent": 620,
  "remaining": -120,
  "exceeded": true
}
```

---

## ⚠️ Error Handling

* Global exception handling via `@RestControllerAdvice`
* Custom exceptions:

    * `BadRequestException`
    * `UnauthorizedException`
* Consistent API response format

---

## 📦 API Response Structure

All responses follow a consistent format:

```json
{
  "success": true,
  "data": {...}
}
```

Error responses:

```json
{
  "success": false,
  "message": "Error message"
}
```

---

## 🧠 Key Concepts Demonstrated

* Multi-tenant architecture (workspace isolation)
* Modular service design
* DTO-based API structure
* Dynamic filtering with JPA Specifications
* File upload & storage abstraction
* Role-based authorization
* Clean exception handling

---

## 🚀 Future Improvements

* Notification system (event-driven architecture)
* CSV import for bulk expenses
* Cloud storage (AWS S3)
* WebSocket real-time updates
* Expense timeline aggregation

---

## 📌 Purpose

This project was built to simulate a **real-world SaaS backend** and demonstrate readiness for backend engineering roles, focusing on clean architecture, scalability, and production-level patterns.
