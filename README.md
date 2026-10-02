# 🛍️ E-Commerce Store & Reporting System

A robust backend application built with **Spring Boot**, **Spring Data JPA / Hibernate**, and an embedded **H2 Database**. This project models a fully functional e-commerce store domain (Products, Categories, Customers, Orders, Order Items, and Payments) and features a specialized reporting engine powered by custom JPQL queries, Java Records, and the Stream API.

---

## 🚀 Tech Stack & Core Technologies

*   **Java 17+** (leveraging modern features like Records)
*   **Spring Boot** (Core, Context, AOP)
*   **Spring Data JPA & Hibernate** (ORM, Object-Relational Mapping, JPQL)
*   **H2 Database** (In-memory relational database for rapid testing)
*   **Maven / Gradle** (Dependency management)

---

## 📊 Domain Model & Architecture

The application manages the following core entities:
*   **`Product`**: Tracks SKU, name, price, stock level, and belongs to multiple categories (**Many-to-Many** with `Category`).
*   **`Category`**: Groups products.
*   **`Customer`**: Places orders and tracks account profiles.
*   **`Order`**: Tracks order status (`OrderStatus`), timestamps (`orderedAt`), and contains a collection of order items (**One-to-Many**).
*   **`OrderItem`**: Links products to orders, capturing snapshot pricing (`unitPrice`) and `quantity`.
*   **`Payment`**: Handles order payment tracking (**One-to-One** with `Order`).

---

## 📈 Key Reporting Features (`ReportRepository`)

The reporting engine uses optimized JPQL queries, projections with Java `record` DTOs, and transaction management for data manipulation:

1.  **Revenue by Category**: Aggregates total sales revenue grouped by product categories.
2.  **Top Customers**: Identifies high-value customers ordered by total spending with configurable result limits.
3.  **Orders per Status**: Summarizes order distribution across different lifecycle statuses using Java Streams and Maps.
4.  **Unordered Products**: Detects inventory items that have never been included in any customer order using `NOT EXISTS` subqueries.
5.  **Monthly Sales Analytics**: Calculates monthly sales volume and revenue filtered by specific order statuses and target years.
6.  **Bulk Category Discount**: Safely updates product pricing across an entire category using JPQL bulk update queries wrapped in transactional boundaries.

---

## 🛠️ Project Structure

src/
├── main/
│   ├── java/org/example/
│   │   ├── dto/             # Immutable Java Records for query projections
│   │   ├── model/           # JPA Entities (Product, Category, Order, Customer, etc.)
│   │   ├── repository/      # Data access layer & custom JPQL reports (ReportRepository)
│   │   └── Main.java        # Application entry point
│   │
│   └── resources/
│       └── application.properties # Database & Hibernate configurations

---

## 🚦 Getting Started & Running the App

1. **Clone or Open** the project in your favorite IDE (such as IntelliJ IDEA).
2. **Ensure Dependencies are synced** (Maven/Gradle will automatically download Spring Boot and H2 dependencies).
3. Run the **`Main`** class (`org.example.Main`). 
4. The application will initialize the embedded H2 database, execute the reporting operations, log results, and exit cleanly with exit code `0`.
