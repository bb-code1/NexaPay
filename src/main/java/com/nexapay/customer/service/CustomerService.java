package com.nexapay.customer.service;

import com.nexapay.customer.entity.CustomerEntity;
import com.nexapay.customer.entity.CustomerProfileEntity;
import com.nexapay.customer.repository.CustomerProfileRepository;
import com.nexapay.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerProfileRepository customerProfileRepository) {
        this.customerRepository = customerRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    public Optional<CustomerEntity> getCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }

    public Optional<CustomerEntity> getCustomerByNumber(String customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber);
    }

    public Optional<CustomerProfileEntity> getProfileByCustomerId(String customerId) {
        return customerProfileRepository.findByCustomerId(customerId);
    }
}
