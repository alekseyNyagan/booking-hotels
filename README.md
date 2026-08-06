# Enterprise Hotel Booking Microservices

A production-ready, highly scalable microservice-based application for hotel booking and real-time analytics. Built with an asynchronous, event-driven architecture and a robust monitoring ecosystem.

## 🏗️ System Architecture & Architecture Patterns

*   **API Gateway Pattern**: Single entry point (`gateway-server`) routing traffic to target microservices.
*   **Service Discovery**: Dynamic microservice registration and resolution via Netflix Eureka (`eureka-server`).
*   **Centralized Configuration**: Unified configuration management via Spring Cloud Config Server (`config-server`).
*   **CQRS Pattern (Command Query Responsibility Segregation)**:
   *   **Write Side**: `booking-hotels-service` manages room availability and bookings, using **PostgreSQL** (OLTP) and **Redis** for high-performance caching.
   *   **Event Bus**: Core business actions trigger events published to **Apache Kafka**.
   *   **Read Side**: `statistic-service` consumes events asynchronously from Kafka and pushes raw analytics into **ClickHouse** (OLAP) for fast aggregate reporting.
*   **Distributed Security**: Centralized Identity and Access Management (IAM) powered by **Keycloak** (OAuth2 / OpenID Connect) protecting downstream services.

---

## 🛠️ Infrastructure & Observability (Production Ready)

The project includes a complete corporate monitoring and logging stack:
*   **Centralized Logging (ELK Stack)**: `logstash` gathers text streams from all microservices, pipes them to `elasticsearch` for indexing, and visualizes system logs in `kibana`.
*   **Distributed Tracing**: **Zipkin** tracks end-to-end request flows across internal servers to uncover network bottlenecks.
*   **Metrics & Dashboards**: **Prometheus** scrapes application performance metrics (JVM state, CPU/Memory) while **Grafana** visualizes them in beautiful custom charts.

---

## 🧱 Tech Stack

*   **Backend**: Java 17+, Spring Boot, Spring Cloud (Gateway, Config, Eureka)
*   **Databases**: PostgreSQL, ClickHouse, Redis
*   **Message Broker**: Apache Kafka + Zookeeper
*   **IAM & Security**: Keycloak (OAuth2, RBAC)
*   **DevOps & Tools**: Docker, Docker Compose, pgAdmin

---

## 🚀 Getting Started

### Prerequisites
Make sure Docker and Docker Compose are installed and running on your machine.

### Local Deployment
1. Open `compose.dev.yaml` and update the environment variable `KEYCLOAK_CLIENT_SECRET` to your actual client secret if needed.
2. Spin up the entire infrastructure with a single command:
   ```bash
   docker compose up -d
   ```

---

## ✅ How to Verify It's Working

### 1. Configure the IAM (Keycloak)
1. Open the Keycloak admin console at [http://localhost:8080](http://localhost:8080).
2. Log in with the root admin account: `admin` / `adminpass`.
3. In the left menu, navigate to **Users** → **Add user**.
4. Enter the username `user1` and click **Save**.
5. Switch to the **Credentials** tab → set the password to `password1` → uncheck **Temporary** → click **Set Password**.
6. Go to the **Role Mappings** tab, click **Assign role**, find and select the `ADMIN` role, then click **Assign**.

### 2. Test Endpoints & Services
*   **Booking Service (via Gateway)**:
    Open your browser and navigate to: `http://localhost:8072/booking-hotels/api/hotel`
    Log in using your new credentials (`user1` / `password1`). You should receive a valid JSON response:
    ```json
    {
      "hotels": []
    }
    ```
*   **Log Management**: Access Kibana at `http://localhost:5601` to view consolidated traces.
*   **Tracing Console**: Access Zipkin at `http://localhost:9411` to inspect inter-service performance.
