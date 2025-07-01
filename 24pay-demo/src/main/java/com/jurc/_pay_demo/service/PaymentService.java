package com.jurc._pay_demo.service;

import com.jurc._pay_demo.config.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProperties properties;
    private final SignService signService;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public Map<String, String> createPaymentRequest(String amount) {
        String timestamp = this.getTimestamp();

        Map<String, String> params = new HashMap<>();
        params.put("Mid", properties.getMid());
        params.put("EshopId", properties.getEshopId());
        params.put("MsTxnId", properties.getMsTxnId());
        params.put("Amount", amount);
        params.put("CurrAlphaCode", properties.getCurrAlphaCode());
        params.put("ClientId", properties.getClientId());
        params.put("FirstName", properties.getFirstName());
        params.put("FamilyName", properties.getFamilyName());
        params.put("Email", properties.getEmail());
        params.put("Country", properties.getCountry());
        params.put("Timestamp", timestamp);

        String message = properties.getMid() + amount + properties.getCurrAlphaCode() + properties.getMsTxnId() + properties.getFirstName() + properties.getFamilyName() + timestamp;
        String sign = signService.generateSign(message, properties.getKey(), properties.getMid());

        params.put("Sign", sign);
        params.put("RURL", properties.getRurl());
        params.put("RedirectSign", "true");
        params.put("Debug", "true");

        logger.info("CreatePaymentRequest data: " + params);
        return params;
    }

    public boolean verifySignature(Map<String, String> params) {
        logger.info("verifySignature data: " + params);
        String message = params.get("MsTxnId") + params.get("Amount") + params.get("CurrCode") + params.get("Result");
        String sign = signService.generateSign(message, properties.getKey(), properties.getMid()).toUpperCase();
        logger.info("Generated sign to verify: " + sign);
        return sign.equals(params.get("Sign"));
    }

    public String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}
