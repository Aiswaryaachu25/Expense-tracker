package com.example.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, String> parseExpense(String transcript) {
        String systemPrompt = """
            Extract expense details from the user's sentence.
            Return ONLY a JSON object, no explanation, no markdown, in this exact format:
            {"amount": "<number only>", "category": "<short category like Food, Travel, Groceries>", "note": "<short note>"}
            If amount is not mentioned, use "0". If category is unclear, use "Other".
            """;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        requestBody.put("messages", List.of(
            Map.of("role", "system", "content", systemPrompt),
            Map.of("role", "user", "content", transcript)
        ));
        requestBody.put("temperature", 0.2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map message = (Map) choices.get(0).get("message");
        String content = (String) message.get("content");
        System.out.println("GROQ RAW RESPONSE: " + content);

        return parseJsonResponse(content);
    }

    private Map<String, String> parseJsonResponse(String jsonText) {
        Map<String, String> result = new HashMap<>();
        jsonText = jsonText.replaceAll("```json", "").replaceAll("```", "").trim();

        // very simple manual JSON extraction (no extra library needed)
        result.put("amount", extractValue(jsonText, "amount"));
        result.put("category", extractValue(jsonText, "category"));
        result.put("note", extractValue(jsonText, "note"));
        return result;
    }

   private String extractValue(String json, String key) {
    // matches quoted strings OR raw numbers
    String pattern = "\"" + key + "\"\\s*:\\s*\"?([^\",}]*)\"?";
    java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
    return m.find() ? m.group(1).trim() : "";
}
}