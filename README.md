# RFlow 🚀

RFlow is a lightweight API Gateway built with Java Spring Boot.

It works like a traffic controller between clients and backend services.  
The gateway receives requests, applies rules like rate limiting and logging, and forwards them to the correct backend service. 

Built to understand backend architecture, API traffic management, and microservice communication.

---

## Features ✨

- Dynamic API routing
- Request forwarding
- Rate limiting
- Request logging
- Health check endpoints
- Multi-tenant architecture
- Configurable gateway services
- Mock microservices for testing
- Admin Dashboard (React + Vite)

---

## Architecture 🏗️

```text
Client
   ↓
RFlow API Gateway
   ↓
Backend Services
   ├── User Service
   ├── Product Service
   └── Payment Service
```

---

## Tech Stack 🛠️

**Gateway & Backend:**
- Java
- Spring Boot
- PostgreSQL
- Spring Data JPA
- Maven
- REST APIs
- Docker (Containerization)

**Frontend Dashboard:**
- React (v19)
- Vite
- Vanilla CSS (Modern layouts & variables)

---

## Repository Structure 📁

```text
rflow/
├── api-gateway/        → Spring Boot API Gateway
├── frontend/           → React & Vite Admin Dashboard
├── mock-services/      → Downstream mock services (User, Product, Payment)
│   ├── user-service/
│   ├── product-service/
│   └── payment-service/
└── README.md
```

---

## Database Files 🗄️

Database scripts are available inside:

```text
api-gateway/src/main/resources/
mock-services/user-service/src/main/resources/
mock-services/product-service/src/main/resources/
mock-services/payment-service/src/main/resources/
```

Files:

- `schema.sql` → database schema
- `data.sql` → sample seed data
- `application-example.yml` → example configuration

---

## API Gateway Responsibilities ⚡

The gateway handles:

- Finding the correct backend service
- Forwarding requests
- Applying rate limits
- Logging request details
- Monitoring service health
- Managing tenant-based routing

---

## Multi-Tenant Support 🏢

RFlow supports multi-tenant architecture using shared database tables with tenant isolation.

Each tenant has its own:

- services
- route configurations
- users
- rate limit policies
- request logs

The system separates tenant data using `tenant_id`.

Example:

```text
Tenant A
/api/users  → localhost:8081

Tenant B
/api/users  → localhost:8082
```

Even if route names are the same, each tenant can have different backend services, configurations, and rate limits.

Main tables used for tenant isolation:

```text
tenants
users
services
rate_limit_policies
request_logs
```

This makes the gateway scalable for handling multiple organizations from a single system.

---

## Example Routes 🌐

```http
/api/users      → localhost:8081
/api/products   → localhost:8082
/api/payments   → localhost:8083
```

---

## Running the Project ▶️

### Clone Repository

```bash
git clone https://github.com/Raj-Patel7807/rflow.git
cd rflow
```

---

### Configure PostgreSQL

Create a PostgreSQL database and update the database configuration inside:

```text
application.yaml
```

---

### Start API Gateway

**Using Maven:**

```bash
cd api-gateway
./mvnw spring-boot:run
```

**Using Docker:**

```bash
cd api-gateway
docker build -t api-gateway .
docker run -p 8080:8080 api-gateway
```

---

### Start Mock Services

Run all services separately.

#### User Service

```bash
cd mock-services/user-service
./mvnw spring-boot:run
```

#### Product Service

```bash
cd mock-services/product-service
./mvnw spring-boot:run
```

#### Payment Service

```bash
cd mock-services/payment-service
./mvnw spring-boot:run
```

---

### Start Frontend Dashboard

Ensure you have [Node.js](https://nodejs.org/) installed.

```bash
cd frontend
npm install
npm run dev
```

The frontend dashboard will run locally at [http://localhost:5173](http://localhost:5173). Connect it to your API gateway (default `http://localhost:8080`) by configuring the `VITE_API_URL` environment variable in `frontend/.env`.

---

## Sample Requests 📬

```http
GET /api/users
GET /api/products
POST /api/payments
```

---

## Future Improvements 🚧

- JWT Authentication
- Redis-based Rate Limiting
- Docker Compose for orchestrating all services (Gateway, Frontend, and Mock Services)
- Service Discovery
- Load Balancing
- API Analytics

---

Built by Raj Patel.
