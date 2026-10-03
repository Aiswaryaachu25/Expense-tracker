package com.example.expensetracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
        List<Expense>findByUserOrderByDateTimeDesc(User user);
}
