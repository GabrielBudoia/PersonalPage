package com.personal.page.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init(){
        if (secretKey == null || !secretKey.startsWith("sk_test_")){
            throw new IllegalStateException(
                    "Stripe_Key must be a Stripe TEST key"
            );

        }
        Stripe.apiKey = secretKey;
    }


}
