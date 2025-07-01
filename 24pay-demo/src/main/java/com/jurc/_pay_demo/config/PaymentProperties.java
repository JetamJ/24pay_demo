package com.jurc._pay_demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {
    private String mid;
    private String eshopId;
    private String key;
    private String rurl;
    private String url;
    private String msTxnId;
    private String currAlphaCode;
    private String clientId;
    private String firstName;
    private String familyName;
    private String email;
    private String country;
    private String redirectSign;
    private String debug;
}
