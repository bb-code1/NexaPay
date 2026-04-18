package com.nexapay.transaction.repository;

import com.nexapay.transaction.entity.DeclineCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeclineCodeRepository extends JpaRepository<DeclineCodeEntity, String> {
}
