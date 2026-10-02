package org.example.service;

import jakarta.transaction.Transactional;
import org.example.exception.DuplicateCustomerException;
import org.example.model.Address;
import org.example.model.Customer;
import org.example.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    CustomerRepository customerRepository;
    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository=customerRepository;
    }

    @Transactional
    public void register(String name, String email, Address address){

        Boolean exists = customerRepository.findByString(email).isPresent();
        if(exists){
            throw new DuplicateCustomerException();
        }

        Customer newCustomer = new Customer();
        newCustomer.setAddress(address);
        newCustomer.setEmail(email);
        newCustomer.setName(name);

        customerRepository.save(newCustomer);
    }
}
