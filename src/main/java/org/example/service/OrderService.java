package org.example.service;

import jakarta.transaction.Transactional;
import org.example.exception.InsufficientStockException;
import org.example.exception.InvalidOrderStatusException;
import org.example.exception.OrderDoesntExistException;
import org.example.model.*;
import org.example.repository.CustomerRepository;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                                CustomerRepository customerRepository,
                                ProductRepository productRepository){
        this.orderRepository=orderRepository;
        this.customerRepository=customerRepository;
        this.productRepository=productRepository;
    }

    @Transactional
    public void placeOrder(Long customerId, Map<Long, Integer> productQuantities) throws InsufficientStockException {
        Customer c =customerRepository.findById(customerId);
        if (c==null){
            throw new RuntimeException("Customer not found");
        }

        Order order = new Order();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity =entry.getValue();

            Product product = productRepository.findById(productId);
            if(product==null){
                throw new RuntimeException("No such product found");
            }

            if (quantity <= 0||product.getStock() <quantity) {

                throw new InsufficientStockException();
            }


            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(product.getPrice());
            order.addItem(orderItem);


            product.setStock(product.getStock()-quantity);

        }
        order.setCustomer(c);

        orderRepository.saveOrder(order);


    }

    @Transactional
    public void pay(Long orderId, PaymentMethod paymentMethod){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderDoesntExistException::new);
        if(order.getStatus() != OrderStatus.NEW){
            throw new InvalidOrderStatusException();
        }
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        order.setPayment(payment);
        order.setStatus(OrderStatus.PAID);

    }

    @Transactional
    public void ship(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderDoesntExistException::new);
        if(order.getStatus()!=OrderStatus.PAID){
            throw new InvalidOrderStatusException();
        }
        order.setStatus(OrderStatus.SHIPPED);
    }

    @Transactional
    public void cancel(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderDoesntExistException::new);
        if(order.getStatus()!=OrderStatus.NEW && order.getStatus()!=OrderStatus.PAID){
            throw new InvalidOrderStatusException();
        }
        order.setStatus(OrderStatus.CANCELLED);

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock()+ item.getQuantity());
        }
    }


}
