package org.example.service;

import jakarta.transaction.Transactional;
import org.example.exception.InsufficientStockException;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
        private final ProductRepository productRepository;

        public ProductService(ProductRepository productRepository){
            this.productRepository = productRepository;
        }

        public void addProduct(Product product){
            productRepository.save(product);
        }

        @Transactional
        public void restock(Long productId, int quantity) throws InsufficientStockException {
            Product product=productRepository.findById(productId);
            if(product==null){
                throw new RuntimeException("Product doesn't exist");
            }

            if(quantity <=0){
                throw new IllegalArgumentException("Invalid quantity inserted");
            }

            product.setStock(product.getStock()+quantity);
        }

        @Transactional
        public void changePrice(Long productId, BigDecimal newPrice) throws Exception {
            Product product=productRepository.findById(productId);
            if(product==null){
                throw new RuntimeException("Product doesn't exist");
            }

            if(newPrice.compareTo(BigDecimal.ZERO)<=0){
               throw new IllegalArgumentException("Price can't be negative or zero");
            }

            product.setPrice(newPrice);
        }

        @org.springframework.transaction.annotation.Transactional(readOnly = true)
        public List<Product> getProducts(){
            return productRepository.getAll();
        }
}
