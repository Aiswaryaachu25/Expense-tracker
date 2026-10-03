package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        List<Expense> expenses = expenseRepository.findByUserOrderByDateTimeDesc(user);

        // ---- Weekly totals (last 7 days) ----
        Map<String, Double> weeklyMap = new LinkedHashMap<>();
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd-MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            weeklyMap.put(day.format(dayFormat), 0.0);
        }

        for (Expense e : expenses) {
            LocalDate expDate = e.getDateTime().toLocalDate();
            String key = expDate.format(dayFormat);
            if (weeklyMap.containsKey(key)) {
                weeklyMap.put(key, weeklyMap.get(key) + e.getAmount());
            }
        }

        // ---- Monthly totals (last 6 months) ----
        Map<String, Double> monthlyMap = new LinkedHashMap<>();
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMM yyyy");

        for (int i = 5; i >= 0; i--) {
            LocalDate month = LocalDate.now().minusMonths(i);
            monthlyMap.put(month.format(monthFormat), 0.0);
        }

        for (Expense e : expenses) {
            String key = e.getDateTime().toLocalDate().format(monthFormat);
            if (monthlyMap.containsKey(key)) {
                monthlyMap.put(key, monthlyMap.get(key) + e.getAmount());
            }
        }

        // ---- Category breakdown (for a pie chart, bonus) ----
        Map<String, Double> categoryMap = new LinkedHashMap<>();
        for (Expense e : expenses) {
            categoryMap.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        model.addAttribute("weeklyLabels", weeklyMap.keySet());
        model.addAttribute("weeklyData", weeklyMap.values());
        model.addAttribute("monthlyLabels", monthlyMap.keySet());
        model.addAttribute("monthlyData", monthlyMap.values());
        model.addAttribute("categoryLabels", categoryMap.keySet());
        model.addAttribute("categoryData", categoryMap.values());

        double totalSpent = expenses.stream().mapToDouble(Expense::getAmount).sum();
        model.addAttribute("totalSpent", totalSpent);

        return "dashboard";
    }
}