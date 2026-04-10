package com.nexapay.customer.entity;

import com.nexapay.common.enums.KycTier;
import com.nexapay.common.enums.RiskCategory;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "customer_number", length = 20, nullable = false, unique = true)
    private String customerNumber;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_tier", length = 20, nullable = false)
    private KycTier kycTier;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", length = 20, nullable = false)
    private RiskCategory riskCategory;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CustomerProfileEntity profile;

    public CustomerEntity() {}

    public CustomerEntity(String id, String customerNumber, String firstName, String lastName, String email, String phoneNumber, KycTier kycTier, RiskCategory riskCategory) {
        this.id = id;
        this.customerNumber = customerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.kycTier = kycTier;
        this.riskCategory = riskCategory;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public KycTier getKycTier() { return kycTier; }
    public void setKycTier(KycTier kycTier) { this.kycTier = kycTier; }

    public RiskCategory getRiskCategory() { return riskCategory; }
    public void setRiskCategory(RiskCategory riskCategory) { this.riskCategory = riskCategory; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public CustomerProfileEntity getProfile() { return profile; }
    public void setProfile(CustomerProfileEntity profile) { this.profile = profile; }
}
