package com.richfield.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Screen 5 - Settings.
 *  - "Expiring-soon alerts" switch: saved in SharedPreferences and used by the pantry screen
 *    to highlight ingredients that expire within 3 days.
 *  - "Clear whole pantry" button: deletes every ingredient (after a confirmation).
 */
public class SettingsActivity extends AppCompatActivity {

    /** Shared with MainActivity, which reads the same preference. */
    public static final String PREFS_NAME = "settings";
    public static final String KEY_ALERTS = "alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        MaterialSwitch alertsSwitch = findViewById(R.id.expiryAlerts);
        alertsSwitch.setChecked(prefs.getBoolean(KEY_ALERTS, true));
        alertsSwitch.setOnCheckedChangeListener((button, isChecked) ->
                prefs.edit().putBoolean(KEY_ALERTS, isChecked).apply());

        findViewById(R.id.clearPantry).setOnClickListener(v -> confirmClearPantry());

        NavHelper.setupBottomNav(this, R.id.nav_settings);
        NavHelper.applyWindowInsets(findViewById(R.id.root), findViewById(R.id.header), null);
    }

    private void confirmClearPantry() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Clear whole pantry?")
                .setMessage("This removes every ingredient from your pantry. This cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> {
                    new DBHelper(this).clearPantry();
                    Toast.makeText(this, "Pantry cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
