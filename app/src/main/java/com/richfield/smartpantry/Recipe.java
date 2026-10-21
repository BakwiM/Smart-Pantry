package com.richfield.smartpantry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model class: one recipe and the ingredients it needs.
 * The map is ordered (LinkedHashMap) so ingredients show in the order they were stored.
 */
public class Recipe {

    public int id;
    public String name;
    public String method;

    /** Required ingredients: ingredient name -> required quantity (g, ml or pieces). */
    public Map<String, Double> ingredients = new LinkedHashMap<>();

    public Recipe(int id, String name, String method) {
        this.id = id;
        this.name = name;
        this.method = method;
    }
}
