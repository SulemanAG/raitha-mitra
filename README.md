# 🌾 Raitha Mitra (ರೈತ ಮಿತ್ರ)
> **Multilingual Agricultural Workforce & Machinery Management Ecosystem**

Raitha Mitra is a production-oriented, full-stack digital platform connecting farmers, agricultural labourers, and machinery owners. Designed from real-world field research, the platform solves labour discovery bottlenecks, machinery rental coordination challenges, and linguistic barriers across rural agricultural communities.

---

## 🏗️ Architecture & Deployment

```text
                     GitHub Repository
                            │
               ┌────────────┴────────────┐
               │                         │
               ▼                         ▼
      Render Static Host        Render Web Service
       (React + Vite)           (Spring Boot 3)
               │                         │
               │ HTTPS REST              │ JDBC (SSL)
               ▼                         ▼
      Browser Client ──────────────► Neon PostgreSQL
                                   (Flyway Migrated)
```

| Subsystem | Hosting Platform | Tech Stack | Configuration |
| :--- | :--- | :--- | :--- |
| **Frontend UI** | **Render (Static Site)** | React 18, TypeScript, Vite, i18next | Client SPA Rewrite (`/*` → `/index.html`) |
| **Backend REST API** | **Render (Docker Service)** | Spring Boot 3, Java 17, Docker (Temurin 17 JRE) | Multi-stage Docker build (`backend/Dockerfile`) |
| **Database** | **Neon PostgreSQL** | Serverless PostgreSQL 16 | Flyway Schema Migrations (`V1` to `V6`) |

---

## 🔑 Core Technical Invariants & Engineering Highlights

1. **Authentication Architecture & Real OTP**:
   - Phone-number-based authentication secured by real SMS OTP delivery.
   - Stateless JWT tokens with strict signature validation and expiration.
   - Zero production bypasses or dev shortcuts in production profiles.

2. **Fine-Grained Role-Based Access Control (RBAC)**:
   - Enforces `FARMER`, `LABOURER`, and `MACHINERY_OWNER` roles.
   - Method-level security (`@PreAuthorize`) enforcing strict resource ownership authorization.

3. **Machinery & Rental Transaction Engine**:
   - Multi-role machinery asset management with hourly/daily rental rates.
   - State machine governing rental lifecycle (`PENDING` → `ACCEPTED` / `REJECTED` / `CANCELLED` → `COMPLETED`).
   - Server-side price calculation ensuring financial authority remain on backend.
   - Database pessimistic locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) and overlapping rental check queries to prevent double-booking.
   - Automatic rejection of conflicting pending requests upon rental acceptance.

4. **Geospatial Location & Radius Discovery (Phase 8)**:
   - Clean separation of Profile Location, Device Location, and Machinery Physical Location.
   - Mathematical Haversine radial distance filter (1–100 km radius bounding).
   - Optimistic concurrency control (`@Version`) protecting location update mutations.
   - **Privacy Preservation**: Discovery APIs expose administrative village/district data and calculated approximate distance (e.g. `8.4 km away`), strictly hiding exact latitude/longitude coordinates.

5. **Multilingual i18n & Mobile-First UX**:
   - Complete localized experience across **English**, **Kannada (ಕನ್ನಡ)**, and **Hindi (हिंदी)**.
   - Mobile-responsive design tailored for low-bandwidth rural networks.

---

## 🚀 Environment Configuration

All production secrets and database credentials remain strictly outside Git, configured exclusively via Render Environment Variables.

Refer to [.env.example](file:///c:/Users/Suleman%20Agasimani/OneDrive/Desktop/New%20folder/Spring/Raith-Mitra/.env.example) for required keys:

| Environment Variable | Description | Exposed to Frontend? |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Set to `prod` for Spring Boot | No |
| `OTP_ENABLED` | Set `false` for Demo/Portfolio mode without SMS OTP, `true` for real OTP | No |
| `DATABASE_URL` | Neon PostgreSQL JDBC URL | No |
| `DATABASE_USERNAME` | Neon Database User | No |
| `DATABASE_PASSWORD` | Neon Database Password | No |
| `JWT_SECRET` | 256-bit JWT Signing Secret | No |
| `FRONTEND_ORIGIN` | Allowed CORS Origin (`https://raitha-mitra-frontend.onrender.com`) | No |
| `MSG91_AUTH_KEY` | MSG91 Production Auth Key | No |
| `MSG91_TEMPLATE_ID` | MSG91 Approved DLT Template ID | No |
| `MSG91_SENDER_ID` | MSG91 Approved 6-char Header (e.g. `RAITHA`) | No |
| `VITE_API_BASE_URL` | Backend REST Endpoint (`https://raitha-mitra-backend.onrender.com/api/v1`) | **Yes (Public)** |

### 🔐 Authentication Modes

- **Demo / Portfolio Mode (`OTP_ENABLED=false`)**:
  Allows instant, unblocked evaluation during portfolio reviews and interviewer demos without requiring live SMS gateway onboarding or DLT registration. All Spring Security rules, JWT token issuance, account status checks (rejecting suspended accounts), and RBAC controls remain fully active.
- **Real SMS OTP Mode (`OTP_ENABLED=true`)**:
  Enforces 6-digit SMS OTP challenge via MSG91 HTTPS REST API with SHA-256 local hashing, 30s resend cooldown, 5-minute expiry, and max 3 verification attempts.


---

## 🧪 Local Build & Verification

### Backend Tests
```bash
cd backend
./mvnw clean test
```
*Executes all 39 unit, integration, pessimistic locking, Haversine, and concurrency tests.*

### Frontend Production Build
```bash
cd frontend
npm run build
```
*Executes TypeScript type-checks (`tsc -b`) and Vite production bundle generation.*

---

## 📜 License & Author

- **Author**: Suleman Agasimani
- **Version**: 1.0.0
