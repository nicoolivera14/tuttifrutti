/*package com.tuttifrutti.demo.repository;

import com.tuttifrutti.demo.domain.model.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DictionaryRepository {
    private final Map<String, Set<String>> dictionaries = new HashMap<>();

    public DictionaryRepository() {
        dictionaries.put("Animal", Set.of("Perro", "Gato", "León"));
        dictionaries.put("País",Set.of("Argentina","Brasil","Chile"));
        dictionaries.put("Color", Set.of("Rojo", "Verde", "Azul"));
    }

    public boolean existInCategory(String category, String word) {
        return dictionaries.getOrDefault(Category, Set.of()).stream().anyMatch(w -> w.equalsIgnoreCase(word));
    }
}
*/