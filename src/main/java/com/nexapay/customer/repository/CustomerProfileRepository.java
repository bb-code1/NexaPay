package com.nexapay.customer.repository;

import com.nexapay.customer.entity.CustomerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfileEntity, String> {
    Optional<CustomerProfileEntity> findByCustomerId(String customerId);
}
