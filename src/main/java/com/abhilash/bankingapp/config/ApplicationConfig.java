package com.abhilash.bankingapp.config;

import com.abhilash.bankingapp.rest.Handler;
import com.abhilash.bankingapp.service.BankingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public Handler handler(BankingService service){
        return new Handler(service);
    }

    @Bean
    public BankingService service(){
        return new BankingService();
    }
}
