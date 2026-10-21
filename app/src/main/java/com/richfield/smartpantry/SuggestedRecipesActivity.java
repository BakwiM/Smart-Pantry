package com.richfield.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Screen 3 - Suggested Recipes.
 * Runs the STRICT MATCHING logic (DBHelper.strictMatches) against the current pantry and
 * lists only the recipes the user can cook right now.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DBHelper db;
    private RecipeAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        db = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        emptyState = findViewById(R.id.emptyState);
        message = findViewById(R.id.message);

        // Tapping a recipe opens the detail screen, passing the recipe id in the Intent.
        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("id", recipe.id);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        NavHelper.setupBottomNav(this, R.id.nav_recipes);
        NavHelper.applyWindowInsets(findViewById(R.id.root), findViewById(R.id.header), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        ArrayList<Recipe> matches = db.strictMatches();
        adapter.setItems(matches);

        if (matches.isEmpty()) {
            // Feedback required by the brief: never show a blank or broken screen.
            message.setText("No recipes match your pantry yet — add more ingredients.");
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            message.setText("You can make " + matches.size()
                    + (matches.size() == 1 ? " recipe" : " recipes")
                    + " right now using only your pantry");
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
