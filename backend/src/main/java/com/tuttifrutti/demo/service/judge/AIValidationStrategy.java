package com.tuttifrutti.demo.service.judge;

import com.google.gson.*;
import com.tuttifrutti.demo.domain.model.JudgeResult;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("aiJudge")
public class AIValidationStrategy implements AnswerJudgeStrategy {

    private final String apiKey;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public AIValidationStrategy(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        System.out.println("🔑 API KEY CARGADA: " + apiKey.substring(0,10) + "...");
    }

    @Override
    public JudgeResult evaluate(String word, char letter, String category) {
        if (word == null || word.isBlank()) {
            return new JudgeResult(false, 0, "Vacío");
        }

        try {
            return callAI(word, letter, category);
        } catch (Exception e) {
            System.out.println("⚠ Error en IA → fallback: " + e.getMessage());
            boolean fallback = word.toUpperCase().startsWith(String.valueOf(letter).toUpperCase());
            return new JudgeResult(fallback, fallback ? 100 : 0, "Fallback");
        }
    }

    private JudgeResult callAI(String word, char letter, String category) throws Exception {

        String prompt =
                """
                Actúa como juez del juego Tutti Frutti.

                Reglas:
                - La palabra ya pasó el filtro inicial: empieza con la letra correcta (%s)
                - Acepta variantes reales (zucchini / zukini / zuccini).
                - Rechaza inventadas (arros / harroz / herrós).
                
                Responde SOLO en JSON:
                {"valid": true/false, "reason": "motivo corto"}

                Categoría: %s
                Palabra: %s
                """.formatted(letter, category, word);

        JsonObject req = new JsonObject();
        req.addProperty("model", "gpt-4o-mini");
        req.addProperty("input", prompt);

        RequestBody body = RequestBody.create(
                req.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/responses")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Error API: " + response.body().string());
        }

        JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

        String content = json.getAsJsonArray("output")
                .get(0).getAsJsonObject()
                .getAsJsonArray("content")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        //LIMPIEZA PARA SACAR BLOQUES MARKDOWN
        content = content
                .replace("```json","")
                .replace("```", "")
                .trim();
        
// --- VALIDACIÓN NUEVA ---
        JsonElement parsed;
        try {
            parsed = JsonParser.parseString(content);
        } catch (Exception ex) {
            throw new RuntimeException("La IA devolvió algo que NO es JSON: " + content);
        }

        if (!parsed.isJsonObject()) {
            throw new RuntimeException("La IA devolvió un PRIMITIVO, no JSON: " + content);
        }

        JsonObject answer = parsed.getAsJsonObject();

        JsonObject resultJson = gson.fromJson(content, JsonObject.class);

        boolean valid = resultJson.get("valid").getAsBoolean();
        String reason = resultJson.get("reason").getAsString();

        return new JudgeResult(valid, valid ? 100 : 0, reason);
    }
}

