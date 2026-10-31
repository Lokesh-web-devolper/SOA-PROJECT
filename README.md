# ParkGrid Solutions — Enterprise Urban Parking Management System

[![Spring Boot 3.3.5](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud 2023.0.3](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud)
[![React 19](https://img.shields.io/badge/React-19.x-61dafb.svg)](https://react.dev)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-336791.svg)](https://www.postgresql.org/)
[![Vite](https://img.shields.io/badge/Vite-8.x-646cff.svg)](https://vitejs.dev/)

An enterprise-grade, microservices-based automated parking management platform designed for commercial centers, corporate parks, and urban transit hubs. Engineered with a zero-YAML policy, robust concurrency controls against double-booking, dynamic tariff computation, and a modern Stripe/Linear-inspired SaaS user experience.

---

## 📑 Architecture Overview

ParkGrid Solutions is built on an asynchronous, cloud-native Service-Oriented Architecture (SOA):

```
                                    +-----------------------+
                                    |   React 19 Frontend   |
                                    |  (Vite + Modular CSS) |
                                    +-----------+-----------+
                                                |
                                      HTTP / REST (Port 5173)
                                                |
                                                v
                                    +-----------------------+
                                    |  Spring Cloud Gateway |
                                    |     (Port 8080)       |
                                    +-----------+-----------+
                                                |
                     +--------------------------+--------------------------+
                     |                          |                          |
                     v                          v                          v
          +--------------------+     +--------------------+     +--------------------+
          |    Auth Service    |     |  Parking Service   |     |  Booking Service   |
          |    (Port 8081)     |     |    (Port 8082)     |     |    (Port 8083)     |
          +---------+----------+     +---------+----------+     +---------+----------+
                    |                          |                          |
                    +--------------------+     |                          |
                                         |     |                          |
                                         v     v                          v
                                  +--------------------+        +--------------------+
                                  |   Billing Service  |        |   Eureka Registry  |
                                  |    (Port 8085)     |        |    (Port 8761)     |
                                  +--------------------+        +--------------------+
```

### Microservices Catalog

| Service | Port | Description | Database |
| :--- | :---: | :--- | :--- |
| **Eureka Server** | `8761` | Netflix Eureka Service Discovery & Registry | In-Memory |
| **API Gateway** | `8080` | Spring Cloud Gateway with dynamic route dispatching & CORS | Stateless |
| **Auth Service** | `8081` | JWT Authentication, driver registration & RBAC | `parkgrid_auth` |
| **Parking Service** | `8082` | Facilities, zones, slot telemetry & live availability | `parkgrid_parking` |
| **Booking Service** | `8083` | Concurrency-safe reservations & double-booking prevention | `parkgrid_booking` |
| **Billing Service** | `8085` | Dynamic tariff engine, duration calculations & invoicing | `parkgrid_billing` |
| **Frontend Client** | `5173` | React 19 Single Page Application with modular CSS | Client-Side |

---

## 🔒 Concurrency Control & Double-Booking Prevention

To eliminate race conditions when multiple drivers attempt to reserve the same slot simultaneously:

1. **Pessimistic Locking (`PESSIMISTIC_WRITE`)**:
   - The booking transaction queries `ParkingSlot` with an exclusive database lock (`SELECT ... FOR UPDATE`), preventing concurrent modifications during slot assignment.
2. **Overlap Validation**:
   - Before confirming a reservation, the system validates that `startTime < existing.endTime AND endTime > existing.startTime` for active/confirmed states.
3. **Database Constraints & Transaction Isolation**:
   - PostgreSQL `SERIALIZABLE` or `READ_COMMITTED` with `btree_gist` exclusion constraints ensure database-level consistency.

---

## ⚙️ Configuration Standard: Zero YAML Policy

All 6 microservices strictly adhere to **Java Properties format (`application.properties`)**. No `.yml` or `.yaml` files exist anywhere in the active Spring configurations.

Example from [api-gateway/src/main/resources/application.properties](file:///d:/SOA%20PROJECT/backend/api-gateway/src/main/resources/application.properties):

```properties
server.port=8080
spring.application.name=API-GATEWAY

spring.cloud.gateway.routes[0].id=auth-service
spring.cloud.gateway.routes[0].uri=lb://AUTH-SERVICE
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/auth/**

spring.cloud.gateway.routes[1].id=parking-service
spring.cloud.gateway.routes[1].uri=lb://PARKING-SERVICE
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/parking/**

spring.cloud.gateway.routes[2].id=booking-service
spring.cloud.gateway.routes[2].uri=lb://BOOKING-SERVICE
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/bookings/**

spring.cloud.gateway.routes[3].id=billing-service
spring.cloud.gateway.routes[3].uri=lb://BILLING-SERVICE
spring.cloud.gateway.routes[3].predicates[0]=Path=/api/billing/**
```

---

## 🚀 Quick Start Guide

### Prerequisites

- **Java JDK 17+** (JDK 21 or JDK 24 verified)
- **Apache Maven 3.9+**
- **Node.js 18+** & npm
- **PostgreSQL 14+** running on `localhost:5432`

### 1. Database Setup

Ensure the PostgreSQL databases exist. From `psql` or pgAdmin:

```sql
CREATE DATABASE parkgrid_auth;
CREATE DATABASE parkgrid_parking;
CREATE DATABASE parkgrid_booking;
CREATE DATABASE parkgrid_billing;

\c parkgrid_booking;
CREATE EXTENSION IF NOT EXISTS btree_gist;
```

*(Default credentials configured: `postgres` / `Lokesh@123`)*

---

### 2. Launch Entire System (1-Click)

Run the included automated launcher:

#### On Windows Command Prompt:
```cmd
start-all.bat
```

#### On Windows PowerShell:
```powershell
.\start-all.ps1
```

The script launches all 6 microservices in order, waits for Eureka discovery registration, and boots the Vite React client.

To shut down all services at once:
```cmd
stop-all.bat
```

---

## 👤 Default System Credentials

| Role | Email | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@parkgrid.com` | `Admin@123` | Full enterprise command center, slot & tariff management |
| **Customer / Driver** | `customer@parkgrid.com` | `Customer@123` | Driver dashboard, slot reservations, check-in, invoices |
| **Customer / Driver** | `jane@parkgrid.com` | `Customer@123` | Standard driver account |

---

## 🖥️ User Interfaces & Screenshots

- **Customer Dashboard** (`/dashboard`): Real-time occupancy, active bookings, quick check-in / check-out, and invoice history.
- **Facility Discovery** (`/locations` or `/parking`): Filter commercial centers by availability, hourly rate, and location.
- **Interactive Booking** (`/book/:slotId`): Select time windows, enter vehicle license plate, and review instant price estimates.
- **Billing & Invoices** (`/billing`): Full receipt breakdown with 15-minute grace period policy and printable invoice modal.
- **Enterprise Command Center** (`/admin/dashboard`): Recharts revenue trends, capacity vs occupancy telemetry, and quick modules.
- **Facility, Zone & Slot CRUD** (`/admin/facilities`, `/admin/zones`, `/admin/slots`): Real-time maintenance override and capacity management.
- **Master Reservations Stream** (`/admin/bookings`): Complete audit log of all bookings with administrative force check-in/out.
- **Dynamic Tariff Engine** (`/admin/pricing`): Live hourly rate adjustments and duration billing simulator.

---

## 🧪 Testing Backend Compilation

To clean and compile all microservices at once:
```powershell
cd backend
mvn clean compile -DskipTests
```

To build the React frontend:
```powershell
cd frontend
npm run build
```

---

## 🛡️ License & Academic Integrity
Developed as a production-style software architecture project showcasing Spring Boot microservices, Spring Cloud Gateway routing, and React design patterns.
