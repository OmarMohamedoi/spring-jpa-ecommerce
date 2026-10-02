package org.example;

import org.example.config.Jpaconfig;
import org.example.model.*;
import org.example.repository.CustomerRepository;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ReportRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Initialize the pure Spring container using your configuration class
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Jpaconfig.class)) {

            System.out.println("==================================================");
            System.out.println("  Spring JPA E-Commerce Application Started (H2)  ");
            System.out.println("==================================================");

            // Fetch repositories from the container
            ProductRepository productRepository = context.getBean(ProductRepository.class);
            CustomerRepository customerRepository = context.getBean(CustomerRepository.class);
            OrderRepository orderRepository = context.getBean(OrderRepository.class);
            ReportRepository reportRepository = context.getBean(ReportRepository.class);

            // 1. Seed Sample Data
            System.out.println("\n[SEEDING DATA] Creating sample products, customers, and orders...");

            // Products
            Product p1 = new Product();
            p1.setSku("LAP-001");
            p1.setName("Gaming Laptop");
            p1.setPrice(new BigDecimal("1200.00"));
            p1.setStock(10);
            productRepository.save(p1);

            Product p2 = new Product();
            p2.setSku("MOU-002");
            p2.setName("Wireless Mouse");
            p2.setPrice(new BigDecimal("25.50"));
            p2.setStock(50);
            productRepository.save(p2);

            // This product will remain un-ordered to test productsNeverOrdered()
            Product p3 = new Product();
            p3.setSku("MON-003");
            p3.setName("4K Monitor");
            p3.setPrice(new BigDecimal("450.00"));
            p3.setStock(15);
            productRepository.save(p3);

            // Customer
            Address address = new Address("123 Street", "Cairo", "Cairo", "12345", "Egypt");
            Customer customer = new Customer();
            customer.setName("Omar Hosny");
            customer.setEmail("omar@example.com");
            customer.setAddress(address);
            customerRepository.save(customer);

            // Order & Order Items
            Order order = new Order();
            order.setCustomer(customer);
            order.setStatus(OrderStatus.PAID);
            order.setOrderedAt(LocalDateTime.of(2026, 5, 15, 10, 30));

            OrderItem item1 = new OrderItem();
            item1.setProduct(p1);
            item1.setQuantity(1);
            item1.setUnitPrice(p1.getPrice());
            order.addItem(item1);

            OrderItem item2 = new OrderItem();
            item2.setProduct(p2);
            item2.setQuantity(2);
            item2.setUnitPrice(p2.getPrice());
            order.addItem(item2);

            orderRepository.saveOrder(order);
            System.out.println("[SEEDING COMPLETED] Data successfully written to H2 database.");

            // 2. Test Reports & Queries
            System.out.println("\n--------------------------------------------------");
            System.out.println("                 TESTING REPORTS                  ");
            System.out.println("--------------------------------------------------");

            // Revenue By Category (using category name/field mapping)
            System.out.println("-> Testing revenueByCategory():");
            reportRepository.revenueByCategory().forEach(dto ->
                    System.out.println("   Category: " + dto.category() + " | Revenue: " + dto.revenue())
            );

            // Top Customers
            System.out.println("\n-> Testing topCustomers(5):");
            reportRepository.topCustomers(5).forEach(dto ->
                    System.out.println("   Customer: " + dto.customerName() + " | Total Spent: " + dto.totalSpent())
            );

            // Orders Per Status
            System.out.println("\n-> Testing ordersPerStatus():");
            Map<OrderStatus, Long> statusCounts = reportRepository.ordersPerStatus();
            statusCounts.forEach((status, count) ->
                    System.out.println("   Status: " + status + " | Count: " + count)
            );

            // Products Never Ordered
            System.out.println("\n-> Testing productsNeverOrdered():");
            reportRepository.productsNeverOrdered().forEach(dto ->
                    System.out.println("   Unordered Product ID: " + dto.id() + " | Name: " + dto.name())
            );

            // Monthly Sales for 2026
            System.out.println("\n-> Testing monthlySales(2026):");
            reportRepository.monthlySales(2026).forEach(dto ->
                    System.out.println("   Month: " + dto.month() + " | Total Sales: " + dto.totalSales())
            );

            // 3. Test Bulk Update & Persistence Context Clear
            System.out.println("\n--------------------------------------------------");
            System.out.println("        TESTING BULK UPDATE & CACHE CLEAR         ");
            System.out.println("--------------------------------------------------");
            // Note: If you map categories via string or entity field, adjust field alignment accordingly.
            int updatedRows = reportRepository.applyDiscount("Electronics", 10.0);
            System.out.println("-> Bulk update executed. Rows updated: " + updatedRows);

            System.out.println("\n==================================================");
            System.out.println("       ALL TESTS EXECUTED SUCCESSFULLY!           ");
            System.out.println("==================================================");
        }
    }
}