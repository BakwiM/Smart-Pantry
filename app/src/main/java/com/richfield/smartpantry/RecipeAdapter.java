package com.richfield.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Custom RecyclerView adapter for the Suggested Recipes list.
 * Tapping a card tells the Activity which recipe was opened (via the Listener).
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener {
        void onOpen(Recipe recipe);
    }

    private final ArrayList<Recipe> items = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Recipe> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = items.get(position);

        holder.name.setText(recipe.name);
        holder.avatar.setText(recipe.name.substring(0, 1).toUpperCase(Locale.getDefault()));
        holder.ingredients.setText(ingredientSummary(recipe));
        holder.itemView.setOnClickListener(v -> listener.onOpen(recipe));
    }

    /** "Pasta • Tomato • Garlic" */
    private String ingredientSummary(Recipe recipe) {
        StringBuilder text = new StringBuilder();
        for (String ingredient : recipe.ingredients.keySet()) {
            if (text.length() > 0) {
                text.append(" • ");
            }
            text.append(capitalise(ingredient));
        }
        return text.toString();
    }

    static String capitalise(String text) {
        if (text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase(Locale.getDefault()) + text.substring(1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView avatar;
        final TextView name;
        final TextView ingredients;

        ViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            name = view.findViewById(R.id.name);
            ingredients = view.findViewById(R.id.ingredients);
        }
    }
}
