package com.jurc._pay_demo.controller;

import com.jurc._pay_demo.config.PaymentProperties;
import com.jurc._pay_demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentProperties properties;

    @GetMapping("/request")
    public String showPaymentForm() {
        return "request";
    }

    @PostMapping("/request")
    public String handlePaymentRequest(@RequestParam String amount, Model model) {
        Map<String, String> paymentParams = paymentService.createPaymentRequest(amount);
        model.addAttribute("paymentUrl", properties.getUrl());
        model.addAttribute("params", paymentParams);
        return "redirectForm";
    }

    @GetMapping("/rurl")
    public String handlePaymentRedirect(
            @RequestParam String MsTxnId,
            @RequestParam String Amount,
            @RequestParam String CurrCode,
            @RequestParam String Result,
            @RequestParam String Sign,
            Model model
    ) {
        Map<String, String> params = new HashMap<>();
        params.put("MsTxnId", MsTxnId);
        params.put("Amount", Amount);
        params.put("CurrCode", CurrCode);
        params.put("Result", Result);
        params.put("Sign", Sign);
        boolean isValid = paymentService.verifySignature(params);

        model.addAttribute("validSignature", isValid);
        model.addAttribute("result", Result);
        model.addAttribute("transactionId", MsTxnId);
        model.addAttribute("amount", Amount);
        model.addAttribute("currency", CurrCode);

        return "result";
    }
}