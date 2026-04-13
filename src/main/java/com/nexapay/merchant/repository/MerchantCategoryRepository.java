package com.nexapay.merchant.repository;

import com.nexapay.merchant.entity.MerchantCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantCategoryRepository extends JpaRepository<MerchantCategoryEntity, String> {
}
