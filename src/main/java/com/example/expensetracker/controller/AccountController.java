package com.example.expensetracker.controller;

import com.example.expensetracker.model.Account;
import com.example.expensetracker.model.AccountTransaction;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.AccountRepository;
import com.example.expensetracker.repository.AccountTransactionRepository;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

     @Autowired
    private AccountTransactionRepository transactionRepository;

    // Helper: get the currently logged-in User object
    private User getLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @GetMapping("/new")
    public String newAccountForm(Model model) {
        model.addAttribute("account", new Account());
        return "account-form";
    }

    @PostMapping("/save")
    public String saveAccount(@ModelAttribute Account account, Authentication authentication) {
        User user = getLoggedInUser(authentication);
        account.setUser(user);
        accountRepository.save(account);
        return "redirect:/accounts";
    }


// Show one card's detail page with its transaction history
    @GetMapping("/{id}")
    public String viewAccount(@PathVariable Long id, Model model) {
        Account account = accountRepository.findById(id).orElseThrow();
        model.addAttribute("account", account);
        model.addAttribute("transactions", transactionRepository.findByAccountOrderByDateTimeDesc(account));
        model.addAttribute("transaction", new AccountTransaction());
        return "account-detail";
    }

// Add money to a card
    @PostMapping("/{accountId}/add-money")
public String addMoney(@PathVariable Long accountId, @ModelAttribute AccountTransaction transaction) {
    Account account = accountRepository.findById(accountId).orElseThrow();

    transaction.setAccount(account);
    transactionRepository.save(transaction);

    if ("SUBTRACT".equals(transaction.getType())) {
        account.setBalance(account.getBalance() - transaction.getAmount());
    } else {
        account.setBalance(account.getBalance() + transaction.getAmount());
    }
    accountRepository.save(account);

    return "redirect:/accounts/" + accountId;
}

    @GetMapping
    public String listAccounts(Model model, Authentication authentication) {
        User user = getLoggedInUser(authentication);
        List<Account> accounts = accountRepository.findByUser(user);

        double total = accounts.stream().mapToDouble(Account::getBalance).sum();

        model.addAttribute("accounts", accounts);
        model.addAttribute("totalBalance", total);
        return "accounts";
    }

    @GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable Long id, Model model) {
        Account account = accountRepository.findById(id).orElseThrow();
        model.addAttribute("account", account);
        return "account-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id) {
        accountRepository.deleteById(id);
        return "redirect:/accounts";
    }
}