# Smart Pantry Manager

A food waste reduction Java Android app (Mobile App Development 700). 
The software makes recommendations for **only the dishes that can be cooked right now** 
using the items the user has on hand; no shopping excursion is necessary.

## Features
- Add, modify, and remove ingredients (name, quantity, unit, optional expiration date) using pantry management.
- The pantry list is a RecyclerView with an empty state that is tied to a SQLite database using a custom adapter.
- The first time the application launches, 20 pre-loaded recipes are seeded into the database.
- A recipe is only recommended if *every* item is in the cupboard in at least the necessary amount due to strict matching.Robust to basic messiness: unit discrepancies (kg -> g, l → ml) and singular/plural (tomato / tomatoes, noodle / noodles)
- When nothing matches, a polite message appears on the "Suggested Recipes screen."
- The complete ingredient list and numbered technique are displayed on the recipe detail screen.
- The "clear pantry" and "expiring-soon alerts" switch highlights products that expire in three days.
- Pantry, Recipes, and Settings are located in the bottom navigation bar.Before saving, the name, quantity, and unit are verified; a date picker is used for expiration.
- Revert the removal of a component

## Screens
| Screen | Class | Layout |
|---|---|---|
| Pantry list | `MainActivity` | `activity_main.xml` |
| Add / Edit ingredient | `AddEditIngredientActivity` | `activity_add_edit.xml` |
| Suggested recipes | `SuggestedRecipesActivity` | `activity_suggestions.xml` |
| Recipe detail | `RecipeDetailActivity` | `activity_recipe_detail.xml` |
| Settings | `SettingsActivity` | `activity_settings.xml` |

Other classes: `DBHelper` (SQLite + strict-matching logic), `IngredientAdapter`, `RecipeAdapter`,
`Ingredient`, `Recipe` (models) and `NavHelper` (bottom navigation + system-bar insets).

## Database choice – SQLite
SQLite (`SQLiteOpenHelper`) was chosen because the pantry belongs to one user on one device. It needs
no server or internet connection, is simple to demonstrate, and the data persists after the app is
closed and reopened. Tables: `ingredients`, `recipes`, `recipe_ingredients`.

## Setup / run
1. Install Android Studio (a recent version) with Android SDK Platform 35.
2. **File → Open** and choose the folder that directly contains `settings.gradle`.
3. Wait for the Gradle sync to finish.
4. Create an emulator in Device Manager (or plug in a phone with USB debugging) and press **Run ▶**.

Requirements: Java 17, `minSdk 24`, `compileSdk 35`.

## Academic note
A real GitHub history with at least ten significant, incremental commits is required for the assignment.
As you develop, make commitments (such as "Add bottom navigation" or "Add Material theme and colours").
"Add expiry date picker") and display the video's history. Don't fabricate history.