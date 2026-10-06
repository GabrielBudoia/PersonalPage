package com.personal.page.controller;

import com.personal.page.dto.PaymentResponse;
import com.personal.page.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public String checkout() {
        String stripeUrl = paymentService.createCheckout();
        return "redirect:" + stripeUrl;
    }

    @GetMapping("/success")
    public String success(@RequestParam("session_id") String sessionId, Model model) {
        var payment = paymentService.findBySessionId(sessionId);
        model.addAttribute("payment", PaymentResponse.from(payment));
        return "success";
    }
}