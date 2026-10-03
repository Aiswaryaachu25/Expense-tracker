package com.example.expensetracker.controller;

import com.example.expensetracker.model.Account;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.AccountRepository;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.GroqService;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

     @Autowired
    private GroqService groqService;

    private User getLoggedInUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName()).orElseThrow();
    }
   

    @PostMapping("/parse-voice")
    @ResponseBody
    public Map<String, String> parseVoice(@RequestBody Map<String, String> body) {
        return groqService.parseExpense(body.get("transcript"));
    }

    // List all expenses for the logged-in user
    @GetMapping
    public String listExpenses(Model model, Authentication authentication) {
        User user = getLoggedInUser(authentication);
        model.addAttribute("expenses", expenseRepository.findByUserOrderByDateTimeDesc(user));
        return "expenses";
    }

    // Show the "add expense" form
    @GetMapping("/new")
    public String newExpenseForm(Model model, Authentication authentication) {
        User user = getLoggedInUser(authentication);
        model.addAttribute("expense", new Expense());
        model.addAttribute("accounts", accountRepository.findByUser(user)); // for the dropdown
        return "expense-form";
    }

    // Save a new expense AND subtract it from the account balance
    @PostMapping("/save")
    public String saveExpense(@ModelAttribute Expense expense,
                               @RequestParam Long accountId,
                               Authentication authentication) {
        User user = getLoggedInUser(authentication);
        Account account = accountRepository.findById(accountId).orElseThrow();

        expense.setUser(user);
        expense.setAccount(account);
        expenseRepository.save(expense);

        // subtract the amount from that account's balance
        account.setBalance(account.getBalance() - expense.getAmount());
        accountRepository.save(account);

        return "redirect:/expenses";
    }

    // Delete an expense AND add the amount back to the account balance
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow();
        Account account = expense.getAccount();

        account.setBalance(account.getBalance() + expense.getAmount()); // refund
        accountRepository.save(account);

        expenseRepository.deleteById(id);
        return "redirect:/expenses";
    }
}