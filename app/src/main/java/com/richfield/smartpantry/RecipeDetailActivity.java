package com.richfield.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Map;

/**
 * Screen 4 - Recipe detail.
 * Receives a recipe id through the Intent, loads that recipe from SQLite and shows
 * the full ingredient list plus the numbered preparation steps.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // back arrow

        int recipeId = getIntent().getIntExtra("id", -1);
        Recipe recipe = new DBHelper(this).getRecipe(recipeId);
        if (recipe == null) {
            finish(); // unknown recipe id - nothing to show
            return;
        }

        ((TextView) findViewById(R.id.name)).setText(recipe.name);
        ((TextView) findViewById(R.id.ingredients)).setText(buildIngredientList(recipe));
        ((TextView) findViewById(R.id.method)).setText(buildSteps(recipe.method));

        NavHelper.applyWindowInsets(findViewById(R.id.root), findViewById(R.id.header),
                findViewById(R.id.scroll));
    }

    /** "• Pasta — 100 g" per line. */
    private String buildIngredientList(Recipe recipe) {
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, Double> entry : recipe.ingredients.entrySet()) {
            String unit = DBHelper.unitFor(entry.getKey());
            text.append("• ")
                    .append(RecipeAdapter.capitalise(entry.getKey()))
                    .append(" — ")
                    .append(Ingredient.formatQuantity(entry.getValue()))
                    .append(unit.isEmpty() ? "" : " " + unit)
                    .append('\n');
        }
        return text.toString().trim();
    }

    /** Splits the method into sentences and numbers them: "1. Boil pasta." */
    private String buildSteps(String method) {
        String[] sentences = method.split("(?<=\\.)\\s+");
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < sentences.length; i++) {
            text.append(i + 1).append(". ").append(sentences[i].trim()).append('\n');
        }
        return text.toString().trim();
    }
}
