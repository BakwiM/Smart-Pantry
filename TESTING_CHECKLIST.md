# Smart Pantry Manager – test checklist

## Core requirements
1. Launch the app: the pantry is empty and shows the "Your pantry is empty" message.
2. Tap **Add ingredient**. Save with every field empty – an error shows under name, quantity and unit.
3. Enter quantity `0`, then `abc` – quantity errors appear. Enter `-5` – error appears.
4. Add *Tomato*, 1, pieces. It appears in the list.
5. Edit Tomato (pencil icon), change the quantity to 2. The list shows 2.
6. Delete Tomato (bin icon) → confirm. It disappears; tap **UNDO** to bring it back.
7. Open **Recipes** with only Tomato in the pantry: "No recipes match your pantry yet" message is shown.

## Strict matching (the most important rule)
8. Add pasta 100 g, tomato 1 piece, garlic 1 piece → **Tomato Pasta** appears in Recipes.
9. Delete garlic → Tomato Pasta **disappears**. Re-add garlic → it **returns**.
10. Change pasta to 50 g → Tomato Pasta disappears (not enough quantity). Set it back to 100.
11. Plural names: add *Tomatoes* instead of *Tomato* → recipe still matches.
12. Units: add pasta as 0.1 kg → counts as 100 g and still matches.
13. Open a suggested recipe: ingredients (with g/ml) and numbered method are shown.

## Navigation and settings
14. Use the bottom bar to switch Pantry → Recipes → Settings → Pantry. Back button returns to Pantry.
15. Add an ingredient with an expiry date from the date picker (use today/tomorrow).
    With **Expiring-soon alerts ON** it shows an orange chip and the orange banner on the Pantry screen.
16. Turn alerts OFF in Settings: the orange chip and banner disappear.
17. Settings → **Clear whole pantry** → confirm. The pantry becomes empty.

## Persistence
18. Add a few ingredients, close the app completely (swipe it away) and reopen: the data is still there.
