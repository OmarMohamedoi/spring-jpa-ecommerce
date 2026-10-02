package org.example.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Customer customer){
        em.persist(customer);
    }

    public Customer findById(Long id){
       return em.find(Customer.class, id);
    }

    public Optional<Customer> findByString(String email){
       return em.createQuery("select c from Customer c where c.email = :email")
               .setParameter("email", email)
               .getResultStream().findFirst();
    }

    public List<Customer> findAll(){
        return em.createQuery("select C from customer C").getResultList();
    }

    @Transactional
    public void delete(Long id){
        Customer deleteCustomer= em.find(Customer.class, id);
        if(deleteCustomer!=null){
            em.remove(deleteCustomer);
        }
    }
}
