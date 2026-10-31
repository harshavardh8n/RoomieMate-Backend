# RoomieMate Backend (Spring Boot 3 + Java 17/21)

A production-grade, highly scalable backend service for roommate expense management, room invitations, automated peer-to-peer balance calculation, and **Min-Cash-Flow Debt Simplification** (Splitwise-style).

---

## 🚀 Key Improvements Over Legacy Node.js Implementation

| Area | Legacy Node.js | Spring Boot 3 + JPA (Current) |
| :--- | :--- | :--- |
| **Financial State** | Mutable `Map<UserId, Number>` directly on User document (vulnerable to race conditions). | **Double-Entry Ledger Approach**: Immutable `Expense`, `ExpenseSplit`, and `Payment` records calculated dynamically. |
| **Monetary Precision** | IEEE 754 Floating-point division (causes penny loss). | **`BigDecimal` with scale 2 & penny distribution**: Indivisible cents distributed evenly so split sums always match 100.00%. |
| **Debt Simplification** | Not implemented (every bilateral debt required a separate transaction). | **Greedy Min-Cash-Flow Algorithm**: Reduces $N \times (N-1)$ debts down to $\le (N-1)$ optimal transactions. |
| **Transactions & Integrity** | Separate uncoordinated `$inc` MongoDB calls without transactions. | **`@Transactional` ACID guarantees**: Automated rollbacks on failure. |
| **API Architecture** | Fat route handlers with DB queries and routes scattered across random files. | Clean **Layered Architecture**: Controller $\rightarrow$ Service $\rightarrow$ Repository $\rightarrow$ Entity / DTO. |
| **Security & Auth** | Tokens logged to `stdout`; multiple unauthenticated routes. | **Spring Security 6 + Stateless JWT**: Centralized `JwtAuthenticationFilter`, BCrypt password hashing. |
| **Input Validation** | Manual `if (!email)` statements. | **`jakarta.validation` (`@NotBlank`, `@Positive`, `@Email`)** with `@RestControllerAdvice` error responses. |
| **Documentation** | None. | **SpringDoc OpenAPI 3 (Swagger UI)** at `/swagger-ui.html` with Bearer auth support. |
| **Database Flexibility** | MongoDB only. | **Dual Profile**: In-Memory H2 for 0-config dev (`dev`) + PostgreSQL for production (`prod`). |

---

## 🛠️ Tech Stack

- **Language**: Java 17 / 21
- **Framework**: Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Spring Security, Spring Validation)
- **Database**: H2 Database (Dev) / PostgreSQL (Production)
- **Security**: JWT (jjwt 0.12.5) + BCrypt
- **API Documentation**: OpenAPI 3 / Swagger UI (`springdoc-openapi`)
- **Containers**: Docker & Docker Compose

---

## 🧠 Core Feature: Min-Cash-Flow Debt Simplification Algorithm

When 3 or more roommates share expenses, bilateral debts quickly become tangled:
- Alice owes Bob \$20
- Bob owes Charlie \$20

Rather than Bob transferring money to Alice and Charlie, our **Min-Cash-Flow algorithm** (in `SettlementService.java`):
1. Calculates each member's net position across all room expenses and payments.
2. Identifies net creditors ($> 0$) and net debtors ($< 0$).
3. Greedily pairs the largest debtor with the largest creditor using Priority Queues.
4. **Result**: Charlie pays Alice \$20 directly in **1 transaction**, and Bob does not have to make any transaction.

---

## 📡 API Endpoints Overview

All protected endpoints require the header: `Authorization: Bearer <your_jwt_token>`

### 1. Authentication (`/api/v1/auth`)
- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Authenticate and obtain JWT token

### 2. Users (`/api/v1/users`)
- `GET /api/v1/users/me` - Current logged-in user profile
- `GET /api/v1/users/search?query=name` - Search users by name or email
- `GET /api/v1/users/idle` - List users not belonging to any room
- `GET /api/v1/users/available-for-room/{roomId}` - List users eligible to be invited to a specific room

### 3. Room Management (`/api/v1/rooms`)
- `POST /api/v1/rooms` - Create a new room and send invitations
- `GET /api/v1/rooms` - List rooms current user is a member of
- `GET /api/v1/rooms/{roomId}` - Get room details and member list
- `POST /api/v1/rooms/{roomId}/members` - Add member directly (Admin only)

### 4. Invitations (`/api/v1/invitations`)
- `POST /api/v1/invitations/rooms/{roomId}` - Send invitation to a user
- `GET /api/v1/invitations` - Get current user's pending invitations
- `POST /api/v1/invitations/{invitationId}/accept` - Accept room invitation
- `POST /api/v1/invitations/{invitationId}/reject` - Reject room invitation

### 5. Expenses & Balances (`/api/v1/rooms/{roomId}`)
- `POST /api/v1/rooms/{roomId}/expenses` - Add expense (equal split or custom split amounts)
- `GET /api/v1/rooms/{roomId}/expenses` - Get paginated expense history (`?page=0&size=20`)
- `GET /api/v1/rooms/{roomId}/expenses/all` - Get all expenses for room
- `GET /api/v1/rooms/{roomId}/balances` - Calculate logged-in user's total net balance and pairwise balances with every roommate
- `GET /api/v1/rooms/{roomId}/analytics` - Statistical summary: total spent, spending by category (`FOOD`, `RENT`, etc.), member breakdown

### 6. Settlements & Payments (`/api/v1/rooms/{roomId}`)
- `POST /api/v1/rooms/{roomId}/payments` - Record a peer-to-peer settlement payment
- `GET /api/v1/rooms/{roomId}/payments` - Historical audit log of all settlement payments
- `GET /api/v1/rooms/{roomId}/settlements/simplified` - Run the **Min-Cash-Flow algorithm** to see minimal transactions

---

## 🏃 Running the Application

### Option A: Local Run (Zero Configuration, In-Memory H2)
Requirements: Java 17+ and Maven.
```bash
mvn spring-boot:run
```
- Server starts on `http://localhost:8080`
- Interactive Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Database Web Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:roomiematedb`, User: `sa`, Password: blank)

### Option B: Docker Compose (Spring Boot + PostgreSQL)
Requirements: Docker & Docker Compose.
```bash
docker-compose up --build
```
This boots up the Spring Boot app and a production PostgreSQL container with health checks and persistent volume storage.

---

## 🧪 Running Automated Tests
```bash
mvn test
```
Runs unit tests for the Min-Cash-Flow algorithm and penny-perfect split division.

