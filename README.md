# Hotel Booking Test Application

This is a **test microservice-based application** for hotel booking. It consists of two main services:

- **Hotel Booking Service**
- **Statistics Service**

The services are orchestrated using **Docker Compose** and exposed through an API Gateway.

---

## 🚀 Getting Started

To run the application locally:

```
docker compose up -d
```

Make sure Docker is installed and running.

---

## ✅ How to Verify It's Working

1. Open your browser and go to:

   ```
   http://localhost:8072/booking-hotels/api/hotel
   ```

2. Log in with the following credentials:

   - **Username:** `user1`
   - **Password:** `password1`

3. If everything is working correctly, you will receive a JSON response like this:

   ```json
   {
     "hotels": []
   }
   ```

This means the hotel booking service is up and responding.

---

## 🧱 Tech Stack

- **Java + Spring Boot**
- **Spring Cloud**
- **Keycloak** (for authentication)
- **PostgreSQL**
- **Docker & Docker Compose**

---

## 📌 Notes

- Default users are preconfigured in Keycloak.

---