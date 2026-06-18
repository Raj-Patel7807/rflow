# RFlow Admin Dashboard 📊

The RFlow Admin Dashboard is a lightweight, responsive developer and admin interface built with **React** and **Vite**. It provides a unified control panel to monitor, configure, and manage the [RFlow API Gateway](file:///d:/Code_PlayGround/rflow/README.md).

---

## Features ✨

- **System & Tenant Dashboards**: View overall system stats (total tenants, services, users, requests) or drill down into a specific tenant's request volume, blocked requests, and average response times.
- **Tenant Management**: Seamlessly register, update, and manage multi-tenant configurations.
- **Service Routing**: Dynamically manage backend service configurations and gateway routes.
- **Health Monitoring**: Check live status and uptime for both the API Gateway and downstream services (User, Product, and Payment services).
- **Rate Limit Policies**: Set, update, and attach rate limiting rules to tenant routes.
- **Interactive API Tester**: Send live HTTP requests to the gateway and inspect response headers, status codes, and payloads directly.
- **Request Log Explorer**: Scan through request histories, response codes (colored by status class), and request times.

---

## Tech Stack 🛠️

- **React 19** – Component-based UI library
- **Vite** – Fast build tool and development server
- **React Router 7** – Client-side routing
- **Vanilla CSS** – Modern CSS layout and custom theme tokens

---

## Getting Started 🚀

### 1. Setup Environment Variables

Copy the example environment file:

```bash
cp .env.example .env
```

Ensure the API Gateway target matches your running RFlow instance:

```env
VITE_API_URL=http://localhost:8080
```

### 2. Install Dependencies

Install all package dependencies using npm:

```bash
npm install
```

### 3. Run Development Server

Launch the local development server:

```bash
npm run dev
```

The application will run at [http://localhost:5173](http://localhost:5173).

---

## Production Build

To generate the optimized production assets:

```bash
npm run build
```

The output will be created inside the `dist/` directory, ready to be served by any static host.
