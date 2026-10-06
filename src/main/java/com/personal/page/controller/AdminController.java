package com.personal.page.controller;

import com.personal.page.model.PaymentStatus;
import com.personal.page.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String dashboard(@RequestParam(required = false) PaymentStatus status, Model model) {
        model.addAttribute("dashboard", adminService.buildDashboard(status));
        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("selectedStatus", status);
        return "admin";
    }
}