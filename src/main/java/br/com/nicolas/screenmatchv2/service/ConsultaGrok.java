package br.com.nicolas.screenmatchv2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsultaGrok {

    public static String obterTraducao(String texto) {
        try {
            String apiKey = System.getenv("GROQ_API_KEY");

            ObjectMapper mapper = new ObjectMapper();

            // Monta o corpo da requisição no formato da API
            String jsonBody = """
                {
                  "model": "llama-3.3-70b-versatile",
                  "messages": [
                    {"role": "user", "content": "Traduza para o português o seguinte texto: %s"}
                  ]
                }
                """.formatted(texto.replace("\"", "\\\""));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            return root.get("choices").get(0).get("message").get("content").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return texto; // fallback: retorna o texto original se der erro
        }
    }
}