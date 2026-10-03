package com.example.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private String category;
    private String note;
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Expense(){}

    @PrePersist
    public void setCurrentDateTime(){
        this.dateTime=LocalDateTime.now();
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}

    public Double getAmount(){return amount;}
    public void setAmount(Double amount){this.amount=amount;}

    public String getCategory(){return category;}
    public void setCategory(String category){this.category=category;}

    public String getNote(){return note;}
    public void setNote(String note){this.note=note;}

    public LocalDateTime getDateTime(){return dateTime;}

    public Account getAccount(){return account;}
    public void setAccount(Account account){this.account=account;}

    public User getUser(){return user;}
    public void setUser(User user){this.user=user;}
}
