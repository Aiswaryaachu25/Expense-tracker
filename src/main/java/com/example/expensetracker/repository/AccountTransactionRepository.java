package com.example.expensetracker.repository;

import com.example.expensetracker.model.Account;
import com.example.expensetracker.model.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
    List<AccountTransaction> findByAccountOrderByDateTimeDesc(Account account);
}