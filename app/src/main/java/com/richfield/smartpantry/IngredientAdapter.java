package com.richfield.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Custom RecyclerView adapter: turns each Ingredient into one card on the pantry screen.
 * The adapter only displays data; the Edit / Delete clicks are passed to the Activity
 * through the Listener interface.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    /** Implemented by MainActivity so it can react to the two buttons on each card. */
    public interface Listener {
        void onEdit(Ingredient ingredient);

        void onDelete(Ingredient ingredient);
    }

    private final ArrayList<Ingredient> items = new ArrayList<>();
    private final Listener listener;
    private boolean alertsEnabled = true;

    public IngredientAdapter(Listener listener) {
        this.listener = listener;
    }

    /** Replaces the list contents and refreshes the screen. */
    public void setItems(List<Ingredient> newItems, boolean alertsEnabled) {
        items.clear();
        items.addAll(newItems);
        this.alertsEnabled = alertsEnabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient item = items.get(position);

        holder.name.setText(item.name);
        holder.avatar.setText(item.name.isEmpty() ? "?" : item.name.substring(0, 1).toUpperCase(Locale.getDefault()));
        holder.details.setText(Ingredient.formatQuantity(item.quantity) + " " + item.unit);
        bindExpiryChip(holder.expiryChip, item);

        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    /** Shows the expiry status: red = expired, orange = expiring soon, green = fine. */
    private void bindExpiryChip(TextView chip, Ingredient item) {
        long days = item.daysUntilExpiry();
        int textColor = R.color.green_dark;
        int background = R.drawable.bg_chip_ok;
        String label;

        if (days == Ingredient.NO_EXPIRY) {
            label = "No expiry date";
        } else if (alertsEnabled && days < 0) {
            label = "Expired";
            textColor = R.color.tomato;
            background = R.drawable.bg_chip_expired;
        } else if (alertsEnabled && days == 0) {
            label = "Expires today";
            textColor = R.color.orange;
            background = R.drawable.bg_chip_soon;
        } else if (alertsEnabled && days <= Ingredient.SOON_DAYS) {
            label = "Expires in " + days + (days == 1 ? " day" : " days");
            textColor = R.color.orange;
            background = R.drawable.bg_chip_soon;
        } else {
            label = "Expires " + item.expiry;
        }

        chip.setText(label);
        chip.setBackgroundResource(background);
        chip.setTextColor(ContextCompat.getColor(chip.getContext(), textColor));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Holds the views of one card so they are looked up only once. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView avatar;
        final TextView name;
        final TextView details;
        final TextView expiryChip;
        final ImageButton edit;
        final ImageButton delete;

        ViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            name = view.findViewById(R.id.name);
            details = view.findViewById(R.id.details);
            expiryChip = view.findViewById(R.id.expiryChip);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
        }
    }
}
