package com.example.sistema_estudante.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    // Pega a lista de URLs permitidas do arquivo application.properties.
    // O Spring automaticamente converte a string separada por vírgulas em um array.
    @Value("${app.frontend.url}")
    private String[] allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Aplica a configuracao apenas aos endpoints da API
                    .allowedOrigins(allowedOrigins) // Permite requisicoes das origens configuradas
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Metodos HTTP permitidos
                    .allowedHeaders("*") // Permite todos os cabecalhos
                    .allowCredentials(true); // Permite o envio de cookies e outros dados de autenticacao
            }
        };
    }
}
