package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.model.Customer;
import org.example.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveOrder(Order order){
        em.persist(order);
    }

    public Optional<Order> findById(Long id){
        return em.createQuery("Select o from Order o where o.id =:id", Order.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    public Order findByIdWithItems(Long id){
        return em.createQuery("Select o from Order o join fetch o.items where o.id =:id", Order.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Optional<Order> findByCustomer(Long customerId){
        return em.createQuery("select o from order o where order.customerId =:customerId ", Order.class)
                .setParameter("customerId", customerId)
                .getResultStream()
                .findFirst();

    }

    public Optional<Order> findByStatus(String status){
        return  em.createQuery("select o from order o where o.status =:status", Order.class)
                .setParameter("status", status)
                .getResultStream()
                .findFirst();
    }

}
