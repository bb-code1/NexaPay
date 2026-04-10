package com.nexapay.customer.repository;

import com.nexapay.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
    Optional<CustomerEntity> findByCustomerNumber(String customerNumber);
    Optional<CustomerEntity> findByEmail(String email);
    boolean existsByCustomerNumber(String customerNumber);
}
