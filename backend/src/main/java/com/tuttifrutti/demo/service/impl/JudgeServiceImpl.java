package com.tuttifrutti.demo.service.impl;

import com.tuttifrutti.demo.service.JudgeService;
import org.springframework.stereotype.Service;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Override
    public boolean validateAnswer(String word, char expectedLetter, String category) {
        if (word == null || word.isBlank()) {
            System.out.println("Palabra vacía");
            return false;
        }

        String normalized = normalize(word);
        char first = Character.toUpperCase(normalized.charAt(0));
        char expected = Character.toUpperCase(expectedLetter);

        boolean startsWith = (first == expected);

        System.out.printf(
                "Validando palabra: '%s' | Letra esperada: '%s' | Categoría: %s | Resultado: %s%n",
                normalized, expected, category, startsWith ? "válida" : "inválida"
        );

        return startsWith;
    }

    private String normalize(String input) {
        return input.trim()
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                .toLowerCase();
    }
}
