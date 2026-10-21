package com.richfield.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Small helper shared by the screens.
 *  1. setupBottomNav  - makes the bottom navigation bar switch between the 3 main screens
 *                       using Intents.
 *  2. applyWindowInsets - on Android 15+ apps draw edge-to-edge, so we push the green header
 *                       below the status bar (and lift scrolling content above the keyboard).
 */
final class NavHelper {

    private NavHelper() {
        // utility class - not meant to be instantiated
    }

    /** Wires up the bottom navigation bar of the given screen. */
    static void setupBottomNav(final Activity activity, final int selectedItemId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedItemId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedItemId) {
                return true; // already on this screen
            }

            Class<?> target;
            if (id == R.id.nav_pantry) {
                target = MainActivity.class;
            } else if (id == R.id.nav_recipes) {
                target = SuggestedRecipesActivity.class;
            } else {
                target = SettingsActivity.class;
            }

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            if (target == MainActivity.class) {
                // The Pantry is the home screen: go back to it instead of stacking a new copy.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            activity.startActivity(intent);

            if (!(activity instanceof MainActivity)) {
                activity.finish(); // so Back always returns to the Pantry
            }
            return false; // keep the highlight on the screen we are still showing
        });
    }

    /**
     * Keeps content clear of the system bars.
     *
     * @param root         the screen's root layout
     * @param header       the green banner; gets extra top padding for the status bar
     * @param scrollBottom optional scrolling container; gets bottom padding for the
     *                     navigation bar / on-screen keyboard (pass null if not needed)
     */
    static void applyWindowInsets(View root, final View header, final View scrollBottom) {
        final int headerTop = header.getPaddingTop();
        final int scrollBase = scrollBottom == null ? 0 : scrollBottom.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());

            header.setPadding(header.getPaddingLeft(), headerTop + bars.top,
                    header.getPaddingRight(), header.getPaddingBottom());

            if (scrollBottom != null) {
                scrollBottom.setPadding(scrollBottom.getPaddingLeft(), scrollBottom.getPaddingTop(),
                        scrollBottom.getPaddingRight(),
                        scrollBase + Math.max(bars.bottom, keyboard.bottom));
            }
            return insets;
        });
    }
}
