package com.richfield.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * All database work lives here (SQLite via SQLiteOpenHelper).
 *
 * Tables:
 *   ingredients        - the user's pantry (full CRUD)
 *   recipes            - 20 recipes, seeded the first time the database is created
 *   recipe_ingredients - which ingredient (and how much) each recipe needs
 *
 * This class also holds the app's core business logic: the STRICT MATCHING rule
 * (see {@link #strictMatches()}).
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ------------------------------------------------------------------
    // Database creation
    // ------------------------------------------------------------------

    /** Runs once, the first time the app creates the database. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ingredients("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "quantity REAL NOT NULL,"
                + "unit TEXT,"
                + "expiry TEXT)");
        db.execSQL("CREATE TABLE recipes("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "method TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients("
                + "recipe_id INTEGER,"
                + "ingredient TEXT,"
                + "quantity REAL)");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS ingredients");
        onCreate(db);
    }

    /** Recipe rows: {id, name, method}. */
    private static final String[][] RECIPES = {
            {"1", "Tomato Pasta", "Boil pasta. Sauté tomato and garlic. Combine with pasta and serve."},
            {"2", "Omelette", "Beat eggs. Add onion and cheese. Cook in a pan until set."},
            {"3", "Fried Rice", "Cook rice with egg, carrot and peas in a hot pan."},
            {"4", "Chicken Stir Fry", "Cook chicken. Add vegetables and soy sauce. Stir-fry until tender."},
            {"5", "Vegetable Soup", "Simmer vegetables in stock until soft. Season and serve."},
            {"6", "Pancakes", "Mix flour, eggs and milk. Cook spoonfuls in a lightly greased pan."},
            {"7", "Grilled Cheese Sandwich", "Butter bread, add cheese and toast until golden."},
            {"8", "Tuna Sandwich", "Mix tuna with mayonnaise. Place on bread with lettuce."},
            {"9", "Garlic Rice", "Fry garlic, add cooked rice and season. Stir until hot."},
            {"10", "Chicken Pasta", "Cook pasta and chicken. Add tomato and combine."},
            {"11", "Bean Burrito", "Fill tortilla with beans, rice and cheese. Fold and heat."},
            {"12", "French Toast", "Dip bread in egg and milk. Fry until golden and serve."},
            {"13", "Potato Omelette", "Cook potato and onion. Add beaten eggs and cook until firm."},
            {"14", "Egg Fried Noodles", "Cook noodles. Stir-fry with egg, carrot and soy sauce."},
            {"15", "Chicken Salad", "Combine chicken, lettuce, tomato and cucumber. Dress and serve."},
            {"16", "Tomato Egg Toast", "Toast bread. Top with cooked tomato and egg."},
            {"17", "Vegetable Rice", "Cook rice with carrot, peas and onion. Season and serve."},
            {"18", "Cheesy Pasta", "Cook pasta and combine with cheese and milk until creamy."},
            {"19", "Banana Pancakes", "Mash banana. Mix with egg and flour. Cook pancakes."},
            {"20", "Chicken Rice Bowl", "Combine cooked rice and chicken with carrot and soy sauce."}
    };

    /** Recipe ingredient rows: {recipe id, ingredient name, quantity needed}. */
    private static final String[][] RECIPE_INGREDIENTS = {
            {"1", "pasta", "100"}, {"1", "tomato", "1"}, {"1", "garlic", "1"},
            {"2", "eggs", "2"}, {"2", "onion", "1"}, {"2", "cheese", "30"},
            {"3", "rice", "150"}, {"3", "eggs", "1"}, {"3", "carrot", "1"}, {"3", "peas", "50"},
            {"4", "chicken", "150"}, {"4", "carrot", "1"}, {"4", "onion", "1"}, {"4", "soy sauce", "10"},
            {"5", "potato", "2"}, {"5", "carrot", "1"}, {"5", "onion", "1"},
            {"6", "flour", "100"}, {"6", "eggs", "1"}, {"6", "milk", "150"},
            {"7", "bread", "2"}, {"7", "cheese", "40"},
            {"8", "tuna", "100"}, {"8", "mayonnaise", "20"}, {"8", "bread", "2"}, {"8", "lettuce", "1"},
            {"9", "rice", "150"}, {"9", "garlic", "1"},
            {"10", "pasta", "100"}, {"10", "chicken", "150"}, {"10", "tomato", "1"},
            {"11", "beans", "100"}, {"11", "rice", "100"}, {"11", "cheese", "30"}, {"11", "tortilla", "1"},
            {"12", "bread", "2"}, {"12", "eggs", "1"}, {"12", "milk", "100"},
            {"13", "potato", "2"}, {"13", "onion", "1"}, {"13", "eggs", "2"},
            {"14", "noodles", "100"}, {"14", "eggs", "1"}, {"14", "carrot", "1"}, {"14", "soy sauce", "10"},
            {"15", "chicken", "100"}, {"15", "lettuce", "1"}, {"15", "tomato", "1"}, {"15", "cucumber", "1"},
            {"16", "bread", "2"}, {"16", "tomato", "1"}, {"16", "eggs", "1"},
            {"17", "rice", "150"}, {"17", "carrot", "1"}, {"17", "peas", "50"}, {"17", "onion", "1"},
            {"18", "pasta", "100"}, {"18", "cheese", "40"}, {"18", "milk", "100"},
            {"19", "banana", "1"}, {"19", "eggs", "1"}, {"19", "flour", "100"},
            {"20", "rice", "150"}, {"20", "chicken", "150"}, {"20", "carrot", "1"}, {"20", "soy sauce", "10"}
    };

    /** Inserts the pre-loaded recipes (called once from onCreate). */
    private void seedRecipes(SQLiteDatabase db) {
        for (String[] row : RECIPES) {
            db.execSQL("INSERT INTO recipes VALUES(?,?,?)",
                    new Object[]{Integer.parseInt(row[0]), row[1], row[2]});
        }
        for (String[] row : RECIPE_INGREDIENTS) {
            db.execSQL("INSERT INTO recipe_ingredients VALUES(?,?,?)",
                    new Object[]{Integer.parseInt(row[0]), row[1], Double.parseDouble(row[2])});
        }
    }

    // ------------------------------------------------------------------
    // Pantry CRUD (Create, Read, Update, Delete)
    // ------------------------------------------------------------------

    /** READ: every pantry item, sorted A-Z. */
    public ArrayList<Ingredient> getIngredients() {
        ArrayList<Ingredient> list = new ArrayList<>();
        String sql = "SELECT id, name, quantity, unit, expiry FROM ingredients ORDER BY name COLLATE NOCASE";
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                list.add(new Ingredient(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getString(4)));
            }
        }
        return list;
    }

    /** CREATE: add a new pantry item. Returns the new row id (or -1 on failure). */
    public long insert(String name, double quantity, String unit, String expiry) {
        return getWritableDatabase().insert("ingredients", null, toValues(name, quantity, unit, expiry));
    }

    /** UPDATE: change an existing pantry item. */
    public void update(int id, String name, double quantity, String unit, String expiry) {
        getWritableDatabase().update("ingredients", toValues(name, quantity, unit, expiry),
                "id=?", new String[]{String.valueOf(id)});
    }

    /** DELETE: remove one pantry item. */
    public void delete(int id) {
        getWritableDatabase().delete("ingredients", "id=?", new String[]{String.valueOf(id)});
    }

    /** Removes every pantry item (used by Settings > Clear whole pantry). */
    public void clearPantry() {
        getWritableDatabase().delete("ingredients", null, null);
    }

    private ContentValues toValues(String name, double quantity, String unit, String expiry) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry", expiry);
        return values;
    }

    // ------------------------------------------------------------------
    // Recipes + STRICT MATCHING (the core logic of the assignment)
    // ------------------------------------------------------------------

    /**
     * STRICT MATCHING RULE: a recipe is suggested only if EVERY ingredient it needs
     * is in the pantry, in at least the required quantity. If even one ingredient is
     * missing or too small, the recipe is left out (no "almost there" results).
     */
    public ArrayList<Recipe> strictMatches() {
        Map<String, Double> pantry = pantryTotals();
        ArrayList<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : getAllRecipes()) {
            if (canMake(recipe, pantry)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /**
     * Adds up the pantry per ingredient. Names are normalised (so "Tomatoes" and
     * "tomato" are the same ingredient) and quantities are converted to base units
     * (kg -> g, l -> ml) so units can differ from the recipe.
     */
    private Map<String, Double> pantryTotals() {
        Map<String, Double> totals = new HashMap<>();
        for (Ingredient item : getIngredients()) {
            String key = normalise(item.name);
            double amount = toBaseUnit(item.quantity, item.unit);
            Double existing = totals.get(key);
            totals.put(key, (existing == null ? 0.0 : existing) + amount);
        }
        return totals;
    }

    /** True only if every required ingredient is present in a large enough amount. */
    private boolean canMake(Recipe recipe, Map<String, Double> pantry) {
        for (Map.Entry<String, Double> need : recipe.ingredients.entrySet()) {
            Double have = pantry.get(normalise(need.getKey()));
            if (have == null || have + 0.000001 < need.getValue()) {
                return false; // missing, or not enough -> recipe is rejected
            }
        }
        return true;
    }

    /**
     * Makes ingredient names comparable: lower-case, trimmed, and singular.
     * tomatoes -> tomato, eggs -> egg, noodles -> noodle, berries -> berry.
     */
    public static String normalise(String raw) {
        String s = raw.toLowerCase(Locale.US).trim().replaceAll("\\s+", " ");
        if (s.endsWith("ies") && s.length() > 4) {
            s = s.substring(0, s.length() - 3) + "y";
        } else if (s.endsWith("oes")
                || s.endsWith("ches") || s.endsWith("shes")
                || s.endsWith("sses") || s.endsWith("xes")) {
            s = s.substring(0, s.length() - 2);          // remove "es"
        } else if (s.endsWith("s") && !s.endsWith("ss") && !s.endsWith("us")) {
            s = s.substring(0, s.length() - 1);          // remove "s"
        }
        return s;
    }

    /** Converts kilograms to grams and litres to millilitres; other units are unchanged. */
    public static double toBaseUnit(double quantity, String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.US);
        switch (u) {
            case "kg":
            case "l":
            case "litre":
            case "liter":
                return quantity * 1000;
            default:
                return quantity;
        }
    }

    /** Recipe amounts are stored as plain numbers; these ingredients are measured in g or ml. */
    private static final Map<String, String> RECIPE_UNITS = new HashMap<>();

    static {
        String[] grams = {"pasta", "flour", "rice", "cheese", "chicken", "tuna",
                "beans", "noodles", "peas", "mayonnaise"};
        for (String name : grams) {
            RECIPE_UNITS.put(name, "g");
        }
        RECIPE_UNITS.put("milk", "ml");
        RECIPE_UNITS.put("soy sauce", "ml");
    }

    /** Unit to display for a recipe ingredient: "g", "ml", or "" for whole items. */
    public static String unitFor(String ingredientName) {
        String unit = RECIPE_UNITS.get(ingredientName.toLowerCase(Locale.US));
        return unit == null ? "" : unit;
    }

    /** All recipes (with their ingredient lists), sorted A-Z. */
    private ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, name, method FROM recipes ORDER BY name", null)) {
            while (cursor.moveToNext()) {
                recipes.add(new Recipe(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            }
        }
        for (Recipe recipe : recipes) {
            loadIngredients(recipe);
        }
        return recipes;
    }

    /** Looks up one recipe by id (used by the recipe detail screen). */
    public Recipe getRecipe(int id) {
        Recipe recipe = null;
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, name, method FROM recipes WHERE id=?", new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                recipe = new Recipe(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            }
        }
        if (recipe != null) {
            loadIngredients(recipe);
        }
        return recipe;
    }

    /** Fills recipe.ingredients from the recipe_ingredients table. */
    private void loadIngredients(Recipe recipe) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT ingredient, quantity FROM recipe_ingredients WHERE recipe_id=?",
                new String[]{String.valueOf(recipe.id)})) {
            while (cursor.moveToNext()) {
                recipe.ingredients.put(cursor.getString(0), cursor.getDouble(1));
            }
        }
    }
}
