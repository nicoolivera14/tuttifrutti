/*/package com.tuttifrutti.demo.service.judge;

import java.util.HashSet;
import java.util.Set;

public class DictionaryJudge implements JudgeStrategy{

    private final Set<String> dictionary = new HashSet<>(Set.of("perro", "gato","león","argentina", "rojo"));

    @Override
    public void evaluate(PlayerAnswerDTO answers) {
        answers.answers().forEach((cat, word) -> {
            char first = Character.toUpperCase(word.charAt(0));
            System.out.println("Evaluando " + word + ": " + (dictionary.contains(word.toLowerCase()) ? "VALID" : "INVALID"));
        });
    }
}*/
