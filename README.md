# Hotel Booking Test Application

This is a **test microservice-based application** for hotel booking. It consists of two main services:

- **Hotel Booking Service**
- **Statistics Service**

The services are orchestrated using **Docker Compose** and exposed through an API Gateway.

---

## 🚀 Getting Started

To run the application locally:

1. In the file `compose.dev.yaml`, update the environment variable  
   `KEYCLOAK_CLIENT_SECRET` to a string of **10 asterisks (`**********`)**.

2. Start the application:

   ```bash
   docker compose up -d
   ```

Make sure Docker is installed and running.

---

## ✅ How to Verify It's Working

1. **Create a user in Keycloak**

   - Open the Keycloak admin console at [http://localhost:8080](http://localhost:8080).  
   - Log in with the admin account (`admin` / `adminpass`).  
   - In the left menu, go to **Users → Add user**.  
   - Enter the username `user1` and click **Save**.  
   - Go to the **Credentials** tab → set the password to `password1` → uncheck **Temporary** → click **Set Password**.

2. **Assign the `ADMIN` role to the user**

   - Open the **Users** page, click on `user1`.  
   - Go to the **Role Mappings** tab.  
   - Click on **Assign role** button.
   - In the opened window, find `ADMIN`.  
   - Select it and click **Assign**.  
   - Now the role `ADMIN` assigned to the user.

3. **Test the service**

   - Open your browser and go to:

     ```
     http://localhost:8072/booking-hotels/api/hotel
     ```

   - Log in with the credentials:

     - **Username:** `user1`  
     - **Password:** `password1`

   - If everything is working correctly, you will receive a JSON response like this:

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
