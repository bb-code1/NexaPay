package com.nexapay.transaction.entity;

import com.nexapay.common.enums.DeclineCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "decline_codes")
public class DeclineCodeEntity {

    @Id
    @Column(length = 3)
    private String code; // ISO-8583 Code (e.g. 51, 14, 54, 57, 61, 75, 91)

    @Column(length = 100, nullable = false)
    private String meaning;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private DeclineCategory category;

    @Column(name = "customer_message", nullable = false, columnDefinition = "TEXT")
    private String customerMessage;

    public DeclineCodeEntity() {}

    public DeclineCodeEntity(String code, String meaning, DeclineCategory category, String customerMessage) {
        this.code = code;
        this.meaning = meaning;
        this.category = category;
        this.customerMessage = customerMessage;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }

    public DeclineCategory getCategory() { return category; }
    public void setCategory(DeclineCategory category) { this.category = category; }

    public String getCustomerMessage() { return customerMessage; }
    public void setCustomerMessage(String customerMessage) { this.customerMessage = customerMessage; }
}
