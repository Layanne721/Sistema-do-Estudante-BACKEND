package com.example.sistema_estudante.service;

import kong.unirest.HttpResponse; 
import kong.unirest.JsonNode;     
import kong.unirest.Unirest;     
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailgunEmailService {

    @Value("${mailgun.api.key}")
    private String mailgunApiKey;

    @Value("${mailgun.domain}")
    private String mailgunDomain;

    @Value("${mailgun.from.email}")
    private String fromEmail;

    public boolean sendPasswordResetEmail(String toEmail, String userName, String resetLink) {
        // Construa o corpo do email HTML
        String htmlContent = "Olá " + userName + ",<br><br>" +
                             "Você solicitou a redefinição de sua senha. " +
                             "Clique no link a seguir para redefinir:<br>" +
                             "<a href=\"" + resetLink + "\">Redefinir Senha</a><br><br>" +
                             "Se você não solicitou isso, por favor, ignore este e-mail.";

        try {

            HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + mailgunDomain + "/messages")
                .basicAuth("api", mailgunApiKey)
                .field("from", fromEmail) // ALTERADO: Usar .field() para parâmetros de formulário
                .field("to", toEmail)
                .field("subject", "Redefina sua Senha")
                .field("html", htmlContent) // ALTERADO: Usar 'html' para conteúdo HTML
                .asJson();

            int statusCode = request.getStatus();
            System.out.println("Email enviado via Mailgun! Status Code: " + statusCode);

            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("Mailgun API Response Body: " + request.getBody());
                return true;
            } else {
                System.err.println("Mailgun API Error Response Body: " + request.getBody());
                System.err.println("Mailgun API Error Status: " + statusCode);
                return false;
            }
        } catch (UnirestException ex) {
            System.err.println("Erro ao enviar email via Mailgun: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
           
        }
    }
}
