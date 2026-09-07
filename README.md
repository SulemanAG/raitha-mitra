# 🌾 Raitha Mitra (ರೈತ ಮಿತ್ರ)
> **Full-Stack Multilingual Agricultural Workforce & Machinery Sharing Platform**

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![React](https://img.shields.io/badge/React-18-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-007ACC?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-6-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vitejs.dev/)

---

## 📌 Executive Summary

**Raitha Mitra** (Kannada for *"Farmer's Friend"*) is an enterprise-grade agricultural ecosystem designed to bridge critical resource gaps in rural communities. Built to address real-world field dynamics, the platform connects **Farmers**, **Agricultural Labourers**, and **Machinery Owners** into a unified, transparent marketplace.

### 🎯 Key Challenges Addressed
- **Labour Discovery Bottlenecks**: Seasonal labour shortages and lack of localized worker discovery during peak sowing and harvesting periods.
- **Machinery Underutilization & High Capital Costs**: Tractors, harvesters, and tillers sitting idle while smallholder farmers lack affordable mechanization options.
- **Double-Booking & Rental Conflicts**: Managing equipment schedules without operational collisions.
- **Linguistic & Accessibility Barriers**: Overcoming digital exclusion in rural areas through comprehensive multilingual interfaces (**English**, **Kannada / ಕನ್ನಡ**, and **Hindi / हिंदी**).
- **Privacy vs. Discovery**: Enabling mathematical radius-based search (1–100 km) while preserving user privacy by withholding exact home/farm GPS coordinates.

---

## 🏗️ System Architecture

```mermaid
flowchart TD
    subgraph ClientLayer["Frontend Client (SPA)"]
        UI["React 18 + TypeScript + Vite"]
        I18N["i18next (English / ಕನ್ನಡ / हिंदी)"]
        AXIOS["Axios Interceptors (JWT Bearer Auth)"]
    end

    subgraph SecurityLayer["Security & Gateway Layer"]
        AUTH_FILTER["JwtAuthenticationFilter (Stateless)"]
        RBAC["Method Security (@PreAuthorize / RBAC)"]
    end

    subgraph AppLayer["Spring Boot 3.4.3 Backend"]
        AUTH_SVC["Auth Service (Demo Mode / MSG91 OTP)"]
        USER_SVC["User & Multi-Role Profile Service"]
        LABOUR_SVC["Labour Requirement Service"]
        MACHINERY_SVC["Machinery & Rental Transaction Service"]
        GEO_SVC["Haversine Location Discovery Service"]
    end

    subgraph DataLayer["Persistence & Database Layer"]
        POSTGRES[("PostgreSQL 16 Database")]
        FLYWAY["Flyway Schema Migrations (V1–V7)"]
        JPA["Hibernate 6 ORM / Spring Data JPA"]
    end

    UI --> I18N
    UI --> AXIOS
    AXIOS -->|HTTPS REST API| AUTH_FILTER
    AUTH_FILTER --> RBAC
    RBAC --> AUTH_SVC & USER_SVC & LABOUR_SVC & MACHINERY_SVC & GEO_SVC
    AUTH_SVC & USER_SVC & LABOUR_SVC & MACHINERY_SVC & GEO_SVC --> JPA
    FLYWAY -->|Schema Versioning| POSTGRES
    JPA -->|HikariCP Pool (SSL)| POSTGRES
```

---

## 👥 Platform Roles & Capabilities

Raitha Mitra implements strict **Role-Based Access Control (RBAC)** across three distinct primary domain personas:

| Persona / Role | Domain Capabilities |
| :--- | :--- |
| 🧑‍🌾 **FARMER** | • Create & manage detailed farm profiles (acres, preferred crops, location).<br>• Post agricultural labour requirements with skill specifications, dates, and offered wages.<br>• Discover nearby labourers and operational machinery within customizable search radii (1–100 km).<br>• Book machinery rentals with automated cost calculation. |
| 🧑‍🌾 **LABOURER** | • Maintain worker profile with verified daily wage rates, experience, and specialized agricultural skills (Harvesting, Sowing, Tilling, Spraying, Weeding).<br>• Toggle real-time availability status (`AVAILABLE`, `UNAVAILABLE`, `HIRED`).<br>• Browse matching farm job openings in nearby villages and taluks. |
| 🚜 **MACHINERY_OWNER** | • Register agricultural equipment (Tractors, Harvesters, Tillers, Sprayers, Threshers) with horsepower ratings, daily/hourly rates, and physical location coordinates.<br>• Review and manage incoming rental booking requests via a synchronized state machine (`PENDING` → `ACCEPTED` / `REJECTED`).<br>• Prevent scheduling conflicts through backend concurrency controls. |

---

## ⚙️ Core Technical Highlights & Invariants

### 1. 🔐 Dual Authentication Architecture
- **Real SMS OTP Delivery (Production)**:
  - Phone-number-based authentication integrated with **MSG91 DLT-compliant HTTPS REST API**.
  - Secure local hashing of OTP tokens (**SHA-256**) in `otp_metadata` table.
  - Robust brute-force protection: 30-second resend cooldown, 5-minute time-to-live (TTL), and maximum 3 verification attempts.
- **Explicit Demo Authentication Mode (`OTP_ENABLED=false`)**:
  - Designed for portfolio reviewers, interviewers, and local testing without external SMS gateway dependencies.
  - Direct login and registration without SMS OTP dispatch.
  - **Zero Security Compromise**: Full Spring Security filter chain, JJWT stateless token generation, account status validation (e.g. rejecting suspended users), and RBAC method-level security remain 100% active.

### 2. 🛡️ Concurrency Control & Double-Booking Prevention
- **Pessimistic Locking**:
  - Machinery rental booking acceptance leverages `@Lock(LockModeType.PESSIMISTIC_WRITE)` to eliminate race conditions during concurrent reservation attempts.
- **State Machine & Auto-Conflict Resolution**:
  - Rental lifecycle transitions: `PENDING` ➔ `ACCEPTED` / `REJECTED` / `CANCELLED` ➔ `COMPLETED`.
  - Accepting a rental automatically validates date overlaps against existing bookings and auto-rejects conflicting pending requests in a single ACID transaction.
- **Optimistic Locking**:
  - Asset records feature JPA `@Version` timestamps to prevent lost updates during concurrent edits.

### 3. 📍 Geospatial Discovery with Privacy Preservation
- **Mathematical Haversine Engine**:
  - Calculates accurate great-circle radial distances between user coordinates and agricultural resources.
  - Configurable radial filtering between 1 km and 100 km.
- **Privacy-First Data Transfer**:
  - Public discovery endpoints withhold raw latitude/longitude coordinates, returning only administrative area descriptions (Village, Taluk, District) and computed approximate distances (e.g. `12.4 km away`).

### 4. 🗄️ Database Architecture & Versioned Migrations
- Managed with **Flyway** (`V1` through `V7` SQL scripts), ensuring deterministic schema evolution:
  - `V1`: Base schema and audit infrastructure.
  - `V2`: Multi-role user identity, composite profiles (`farmer_profiles`, `labourer_profiles`, `machinery_owner_profiles`).
  - `V3`: Secure OTP metadata lifecycle management.
  - `V4`: Labour requirement postings and relational skill collections.
  - `V5`: Machinery assets and rental state machine tables.
  - `V6` & `V7`: Geospatial coordinate columns (`DOUBLE PRECISION`), spatial indexes, and audit timestamps.

### 5. 🌐 Multilingual Accessibility (i18n)
- Localized frontend interface powered by `react-i18next`.
- Complete multilingual catalogs for English, Kannada (ಕನ್ನಡ), and Hindi (हिंदी).
- Responsive mobile-first design tailored for low-bandwidth rural networks.

---

## 📊 Current Project Status & Roadmap

| Module / Milestone | Status | Details |
| :--- | :---: | :--- |
| **Domain Entities & JPA Repositories** | ✅ Complete | Clean entity models with Lombok (`@Getter`/`@Setter`) and record-based DTOs. |
| **Authentication & RBAC Security** | ✅ Complete | Stateless JWT, Spring Security 6, Demo Mode + MSG91 SMS OTP service. |
| **Profiles & Labour Management** | ✅ Complete | Farmer, Labourer, and Machinery Owner lifecycle management. |
| **Machinery & Rental Transaction Engine** | ✅ Complete | Dynamic rate calculation, double-booking prevention, pessimistic locks. |
| **Location & Haversine Discovery** | ✅ Complete | Geospatial distance calculation with privacy-preserving coordinate redaction. |
| **Automated Test Suite** | ✅ Complete | **52 comprehensive backend unit and integration tests** passing cleanly. |
| **Multilingual Frontend SPA** | ✅ Complete | React 18, TypeScript, Tailwind/Custom CSS, Kannada/Hindi/English i18n. |
| **Dockerization & Deployment Config** | 📦 Prepared | Multi-stage Dockerfile, JRE 17 runtime, Flyway automation, Render blueprints. |
| **Live Cloud Deployment (Render/Neon)** | ⏸️ Paused | Prepared and configured; live hosting currently paused due to free-tier/resource constraints. Runs 100% locally or in Docker. |

---

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.4.3 (Java 17)
- **Security**: Spring Security 6, JJWT (JSON Web Token 0.12.6)
- **Data & ORM**: Spring Data JPA, Hibernate 6.6, PostgreSQL Driver
- **Schema Migrations**: Flyway Core & Flyway PostgreSQL
- **Boilerplate Reduction**: Project Lombok 1.18.36
- **Testing**: JUnit 5, AssertJ, Mockito, Spring Security Test, H2 Database (Test Profile)
- **Build Tool**: Apache Maven 3.9 (Maven Wrapper included)

### Frontend
- **Framework**: React 18 with TypeScript 5
- **Build Tool**: Vite 6
- **State & Routing**: React Router DOM 6, React Context API
- **Internationalization**: i18next & react-i18next
- **Icons & Styling**: Lucide React, Custom CSS Design System

---

## 🚀 Getting Started Locally

### Prerequisites
- **Java Development Kit (JDK)**: JDK 17 or higher
- **Node.js**: v18+ and `npm`
- **PostgreSQL**: PostgreSQL 15+ (or Docker)

---

### 1. Clone the Repository
```bash
git clone https://github.com/SulemanAG/raitha-mitra.git
cd raitha-mitra
```

---

### 2. Backend Setup & Run

1. **Configure Environment / Properties**:
   By default, local runs can use `application.yml` or standard environment variables.
   
   Create a local PostgreSQL database:
   ```sql
   CREATE DATABASE raitha_mitra;
   ```

2. **Run Tests**:
   ```bash
   cd backend
   ./mvnw clean test
   ```
   *Executes all 52 unit and integration tests using the isolated H2 in-memory test slice.*

3. **Start Spring Boot Application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend REST API will start at: `http://localhost:8080` (API base: `http://localhost:8080/api/v1`).

---

### 3. Frontend Setup & Run

1. **Install Dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Start Vite Development Server**:
   ```bash
   npm run dev
   ```
   The frontend application will start at: `http://localhost:5173`.

3. **Build for Production**:
   ```bash
   npm run build
   ```

---

## 📡 REST API Overview

All API routes are prefixed with `/api/v1`:

### 🔐 Authentication & Profile (`/auth`, `/users`, `/profiles`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Public | Register new account with role selection (Demo mode or OTP). |
| `POST` | `/auth/login` | Public | Authenticate user via phone number and receive JWT Bearer token. |
| `POST` | `/auth/send-otp` | Public | Request SMS OTP dispatch to phone number. |
| `POST` | `/auth/verify-otp` | Public | Verify 6-digit OTP token. |
| `GET` | `/users/me` | Authenticated | Retrieve current user profile and assigned roles. |
| `POST` | `/profiles/farmer` | `FARMER` | Create or update Farmer domain profile. |
| `POST` | `/profiles/labourer` | `LABOURER` | Create or update Labourer domain profile with skill sets. |
| `POST` | `/profiles/machinery-owner` | `MACHINERY_OWNER` | Create or update Machinery Owner profile. |

### 🌾 Labour Requirements (`/labour-requirements`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/labour-requirements` | `FARMER` | Create a new agricultural labour requirement posting. |
| `GET` | `/labour-requirements` | Authenticated | List all active labour requirement postings. |
| `GET` | `/labour-requirements/my` | `FARMER` | Retrieve labour requirements created by the calling farmer. |
| `GET` | `/labour-requirements/{id}` | Authenticated | Fetch specific labour requirement details. |
| `PATCH` | `/labour-requirements/{id}/status` | `FARMER` | Update requirement status (`OPEN`, `FILLED`, `CANCELLED`). |

### 🚜 Machinery & Rentals (`/machinery`, `/rentals`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/machinery` | `MACHINERY_OWNER` | Register new agricultural machinery asset. |
| `GET` | `/machinery` | Authenticated | List all active machinery assets with optional category filters. |
| `GET` | `/machinery/my` | `MACHINERY_OWNER` | Fetch all equipment owned by the authenticated owner. |
| `POST` | `/rentals` | `FARMER` | Submit rental booking request with rental unit and date range. |
| `GET` | `/rentals/renter/my` | `FARMER` | List booking requests submitted by the calling renter. |
| `GET` | `/rentals/owner/my` | `MACHINERY_OWNER` | List rental requests received for owned machinery. |
| `PATCH` | `/rentals/{id}/accept` | `MACHINERY_OWNER` | Accept booking (triggers pessimistic lock & auto-rejects collisions). |
| `PATCH` | `/rentals/{id}/reject` | `MACHINERY_OWNER` | Reject booking request. |

### 📍 Location Discovery (`/discovery`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/discovery/labourers` | Authenticated | Search nearby labourers within radial distance (1–100 km). |
| `GET` | `/discovery/machinery` | Authenticated | Search available machinery within radial distance (1–100 km). |

---

## 🔒 Security & Environment Variables

| Variable | Description | Example / Default |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (`dev`, `prod`, `test`) | `dev` |
| `OTP_ENABLED` | Set `false` for Demo authentication, `true` for real MSG91 SMS | `false` |
| `DATABASE_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:5432/raitha_mitra` |
| `DATABASE_USERNAME` | PostgreSQL username | `postgres` |
| `DATABASE_PASSWORD` | PostgreSQL password | `postgres` |
| `JWT_SECRET` | 256-bit secret key for JWT HMAC signing | `[Configured via Environment]` |
| `MSG91_AUTH_KEY` | MSG91 SMS Gateway Auth Key | `[Configured in Production]` |
| `MSG91_TEMPLATE_ID` | Approved DLT SMS Template ID | `[Configured in Production]` |
| `MSG91_SENDER_ID` | Approved 6-character sender ID | `RAITHA` |
| `FRONTEND_ORIGIN` | CORS allowed origin | `http://localhost:5173` |

> [!IMPORTANT]
> All sensitive credentials, database keys, and JWT secrets are managed exclusively through environment variables and are never checked into version control.

---

## 👨‍💻 Author & Acknowledgements

- **Developer**: [Suleman Agasimani](https://github.com/SulemanAG)
- **Project Name**: Raitha Mitra (ರೈತ ಮಿತ್ರ)
- **Repository**: [SulemanAG/raitha-mitra](https://github.com/SulemanAG/raitha-mitra)
