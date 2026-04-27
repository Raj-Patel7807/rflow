# 🚀 RFlow - API Gateway & Rate Limiter

RFlow is a lightweight API Gateway system built using Spring Boot that acts as a centralized entry point for backend services. It manages and controls all incoming API traffic efficiently.

---

## 📌 What RFlow Does

RFlow sits between clients and backend services and acts like a **traffic controller + security layer**.

It handles:

- 🔀 Request Routing to backend services
- 🔐 Authentication layer (optional / extensible)
- 🚦 Rate Limiting per user / API key
- 🛡️ Abuse prevention & traffic protection
- 📊 Request monitoring (future scope)

---

## 🧠 Simple Idea

Instead of clients directly calling microservices:
