package com.nexapay.customer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "customer_profiles")
public class CustomerProfileEntity {

    @Id
    @Column(length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private CustomerEntity customer;

    @Column(name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "home_city", length = 50)
    private String homeCity;

    @Column(name = "home_country", length = 2, nullable = false)
    private String homeCountry = "IN";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public CustomerProfileEntity() {}

    public CustomerProfileEntity(String id, CustomerEntity customer, BigDecimal monthlyIncome, Integer creditScore, String homeCity, String homeCountry) {
        this.id = id;
        this.customer = customer;
        this.monthlyIncome = monthlyIncome;
        this.creditScore = creditScore;
        this.homeCity = homeCity;
        this.homeCountry = homeCountry != null ? homeCountry : "IN";
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CustomerEntity getCustomer() { return customer; }
    public void setCustomer(CustomerEntity customer) { this.customer = customer; }

    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }

    public String getHomeCity() { return homeCity; }
    public void setHomeCity(String homeCity) { this.homeCity = homeCity; }

    public String getHomeCountry() { return homeCountry; }
    public void setHomeCountry(String homeCountry) { this.homeCountry = homeCountry; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
