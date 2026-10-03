package com.example.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double balance;
    private String note;                  // NEW
    private LocalDateTime createdAt;      // NEW

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Account(){}

    public Account(String name, Double balance, User user){
        this.name=name;
        this.balance=balance;
        this.user=user;
    }

    @PrePersist
    public void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id=id; }

    public String getName(){ return name; }
    public void setName(String name){ this.name=name; }

    public Double getBalance(){ return balance; }
    public void setBalance(Double balance){ this.balance=balance; }

    public String getNote(){ return note; }
    public void setNote(String note){ this.note=note; }

    public LocalDateTime getCreatedAt(){ return createdAt; }

    public User getUser(){ return user; }
    public void setUser(User user){ this.user=user; }
}