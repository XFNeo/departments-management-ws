package ru.xfneo.departmentsmanagement.config;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.xfneo.departmentsmanagement.client.EmployeeClient;

@Configuration
public class FeignConfig {

    @Value("${employees.service.url}")
    private String employeeServiceUrl;

    @Bean
    public EmployeeClient employeeClientService() {
        return Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Logger.ErrorLogger())
                .logLevel(Logger.Level.FULL)
                .target(EmployeeClient.class, employeeServiceUrl);
    }
}
