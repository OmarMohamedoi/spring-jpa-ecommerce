package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.example.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(Product product){
        em.persist(product);
    }

    public List<Product> getAll(){
       return em.createQuery("select p from products p", Product.class).getResultList();
    }

    public Optional<Product> findBySku(String sku){
        return em.createQuery("select p from products p where p.sku = :sku")
                .setParameter("sku", sku)
                .getResultStream().findFirst();
    }

    public Optional<Product> findByCategory(String category){
        return em.createQuery("select p from products p where p.category = :category")
                .setParameter("category", category)
                .getResultStream().findFirst();
    }

    public Product findById(Long id){
        return em.find(Product.class, id);
    }

    public void deleteById(Long id){
        Product deletedProduct = em.find(Product.class, id);
        if(deletedProduct!=null){
            em.remove(deletedProduct);
        }
    }

    public List<Product> search(String keyword, BigDecimal minPrice, BigDecimal maxPrice, String category){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> product= cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if(keyword!=null && !keyword.trim().isEmpty()){
            predicates.add(cb.like(cb.lower(product.get("name")),"%"+ keyword.toLowerCase()+"%"));
        }
        if(minPrice!=null){
            predicates.add(cb.greaterThanOrEqualTo(product.get("price"), minPrice));
        }

        if(maxPrice!=null){
            predicates.add(cb.lessThanOrEqualTo(product.get("price"), maxPrice));
        }

        if (category != null && !category.trim().isEmpty()) {
            predicates.add(cb.equal(product.get("category"), category));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return em.createQuery(cq).getResultList();
    }

    public List<Product> findByStock(int threshold){
        return em.createQuery("select p from product p where p.stock >=:threshold", Product.class)
                .setParameter("threshold", threshold)
                .getResultList();
    }

    public List<Product> findPage(int page, int size){
        return em.createQuery("Select p from product p")
                .setFirstResult(page*size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("SELECT COUNT(p) FROM product p", Long.class)
                .getSingleResult();
    }
}
