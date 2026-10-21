package com.richfield.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Screen 1 - Pantry list (the launcher screen).
 * Shows every ingredient from the database in a RecyclerView and lets the user
 * add, edit and delete items.
 */
public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private IngredientAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private View alertCard;
    private TextView alertText;
    private TextView summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        emptyState = findViewById(R.id.emptyState);
        alertCard = findViewById(R.id.alertCard);
        alertText = findViewById(R.id.alertText);
        summary = findViewById(R.id.summary);

        // The adapter shows the data; this Activity decides what Edit / Delete do.
        adapter = new IngredientAdapter(new IngredientAdapter.Listener() {
            @Override
            public void onEdit(Ingredient ingredient) {
                openEditScreen(ingredient);
            }

            @Override
            public void onDelete(Ingredient ingredient) {
                confirmDelete(ingredient);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // "Add ingredient" opens the form in "add" mode (no extras in the Intent).
        findViewById(R.id.addButton).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        NavHelper.setupBottomNav(this, R.id.nav_pantry);
        NavHelper.applyWindowInsets(findViewById(R.id.root), findViewById(R.id.header), null);
    }

    /** Reload the list every time the screen becomes visible (e.g. after saving an item). */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        ArrayList<Ingredient> items = db.getIngredients();
        boolean alertsOn = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(SettingsActivity.KEY_ALERTS, true);

        adapter.setItems(items, alertsOn);

        // Show the friendly empty state instead of a blank list.
        boolean empty = items.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);

        // Count the items that are expired or expire within 3 days.
        int expiring = 0;
        for (Ingredient item : items) {
            if (item.isExpiringSoon()) {
                expiring++;
            }
        }

        String text = items.size() + (items.size() == 1 ? " item" : " items");
        if (alertsOn && expiring > 0) {
            text += " • " + expiring + " expiring soon";
        }
        summary.setText(text);

        if (alertsOn && expiring > 0) {
            alertText.setText("Heads up: " + expiring + (expiring == 1 ? " ingredient is" : " ingredients are")
                    + " expired or expiring within " + Ingredient.SOON_DAYS + " days. Cook with them first!");
            alertCard.setVisibility(View.VISIBLE);
        } else {
            alertCard.setVisibility(View.GONE);
        }
    }

    /** Opens the same form as "add", but passes the ingredient's data in the Intent extras. */
    private void openEditScreen(Ingredient ingredient) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra("id", ingredient.id);
        intent.putExtra("name", ingredient.name);
        intent.putExtra("quantity", ingredient.quantity);
        intent.putExtra("unit", ingredient.unit);
        intent.putExtra("expiry", ingredient.expiry);
        startActivity(intent);
    }

    private void confirmDelete(Ingredient ingredient) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + ingredient.name + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.delete(ingredient.id);
                    loadPantry();
                    showUndoMessage(ingredient);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Lets the user bring back an ingredient they deleted by accident. */
    private void showUndoMessage(Ingredient deleted) {
        Snackbar.make(findViewById(R.id.root), deleted.name + " removed", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.bottomNav)
                .setAction("UNDO", v -> {
                    db.insert(deleted.name, deleted.quantity, deleted.unit, deleted.expiry);
                    loadPantry();
                })
                .show();
    }
}
