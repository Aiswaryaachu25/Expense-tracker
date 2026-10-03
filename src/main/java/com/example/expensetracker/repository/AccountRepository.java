package com.example.expensetracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.expensetracker.model.Account;
import com.example.expensetracker.model.User;

public interface AccountRepository extends JpaRepository<Account,Long>{
        List<Account>findByUser(User user);
    
    
}
