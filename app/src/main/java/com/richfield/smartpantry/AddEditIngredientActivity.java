package com.richfield.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * Screen 2 - Add / Edit ingredient form.
 * The same screen is used for both: if the Intent contains an "id" extra we are
 * editing an existing ingredient, otherwise we are adding a new one.
 * All fields are validated before anything is saved.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    /** The units offered in the drop-down (the strict matching knows how to convert these). */
    private static final String[] UNITS = {"g", "kg", "ml", "l", "pieces"};

    private DBHelper db;
    private int editingId = -1; // -1 means "adding a new ingredient"

    private TextInputLayout nameLayout;
    private TextInputLayout quantityLayout;
    private TextInputLayout unitLayout;
    private EditText nameField;
    private EditText quantityField;
    private AutoCompleteTextView unitField;
    private EditText expiryField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = new DBHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // back arrow

        nameLayout = findViewById(R.id.nameLayout);
        quantityLayout = findViewById(R.id.quantityLayout);
        unitLayout = findViewById(R.id.unitLayout);
        nameField = findViewById(R.id.name);
        quantityField = findViewById(R.id.quantity);
        unitField = findViewById(R.id.unit);
        expiryField = findViewById(R.id.expiry);

        unitField.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, UNITS));

        // Tapping the expiry field (or its calendar icon) opens a date picker.
        expiryField.setOnClickListener(v -> showDatePicker());
        ((TextInputLayout) findViewById(R.id.expiryLayout))
                .setEndIconOnClickListener(v -> showDatePicker());
        findViewById(R.id.clearDate).setOnClickListener(v -> expiryField.setText(""));

        // Remove the red error text as soon as the user starts fixing the field.
        clearErrorWhenTyping(nameLayout, nameField);
        clearErrorWhenTyping(quantityLayout, quantityField);
        unitField.setOnItemClickListener((parent, view, position, id) -> unitLayout.setError(null));

        // If the Intent carries an "id", fill the form with the ingredient being edited.
        if (getIntent().hasExtra("id")) {
            editingId = getIntent().getIntExtra("id", -1);
            toolbar.setTitle(R.string.title_edit);
            nameField.setText(getIntent().getStringExtra("name"));
            quantityField.setText(Ingredient.formatQuantity(getIntent().getDoubleExtra("quantity", 0)));
            unitField.setText(getIntent().getStringExtra("unit"), false); // false = don't filter the list
            expiryField.setText(getIntent().getStringExtra("expiry"));
        }

        findViewById(R.id.save).setOnClickListener(v -> saveIngredient());
        findViewById(R.id.cancel).setOnClickListener(v -> finish());

        NavHelper.applyWindowInsets(findViewById(R.id.root), findViewById(R.id.header),
                findViewById(R.id.scroll));
    }

    /** Input validation: checks every field, shows an error on each bad one, saves only if all pass. */
    private void saveIngredient() {
        String name = nameField.getText().toString().trim();
        String quantityText = quantityField.getText().toString().trim();
        String unit = unitField.getText().toString().trim();
        String expiry = expiryField.getText().toString().trim();

        boolean valid = true;

        if (name.isEmpty()) {
            nameLayout.setError("Ingredient name is required");
            valid = false;
        } else if (!name.matches(".*[A-Za-z].*")) {
            nameLayout.setError("The name must contain letters");
            valid = false;
        }

        double quantity = 0;
        if (quantityText.isEmpty()) {
            quantityLayout.setError("Quantity is required");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    quantityLayout.setError("Enter a quantity greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                quantityLayout.setError("Enter a valid number");
                valid = false;
            }
        }

        if (unit.isEmpty()) {
            unitLayout.setError("Please choose a unit");
            valid = false;
        }

        if (!valid) {
            return; // stay on the form so the user can correct the errors
        }

        if (editingId < 0) {
            db.insert(name, quantity, unit, expiry);   // CREATE
        } else {
            db.update(editingId, name, quantity, unit, expiry); // UPDATE
        }
        Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    /** Opens a calendar dialog and writes the chosen date as yyyy-MM-dd. */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // Start the picker on the existing date when editing.
        String[] parts = expiryField.getText().toString().split("-");
        if (parts.length == 3) {
            try {
                calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1,
                        Integer.parseInt(parts[2]));
            } catch (NumberFormatException ignored) {
                // keep today's date
            }
        }

        new DatePickerDialog(this,
                (view, year, month, day) -> expiryField.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void clearErrorWhenTyping(final TextInputLayout layout, EditText field) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
