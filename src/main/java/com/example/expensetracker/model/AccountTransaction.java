package com.example.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String note;
    private LocalDateTime dateTime;
    private String type; // "ADD" or "SUBTRACT"
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public AccountTransaction() {}

    @PrePersist
    public void setCurrentDateTime() {
        this.dateTime = LocalDateTime.now();
    }
    

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getDateTime() { return dateTime; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}