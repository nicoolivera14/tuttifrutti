/*/package com.tuttifrutti.demo.service;

public class ScoringService {

    public int calculateScore (Map<String, String> veredicts) {
        int score = 0;
        for (String value : veredicts.values()) {
            if ("VALID".equals(value)) score += 10;
        }
        return score;
    }
}
*/