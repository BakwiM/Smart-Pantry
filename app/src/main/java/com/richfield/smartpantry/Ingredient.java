package com.richfield.smartpantry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Model class: one item in the user's pantry (one row of the "ingredients" table).
 */
public class Ingredient {

    /** Returned by {@link #daysUntilExpiry()} when no expiry date was entered. */
    public static final long NO_EXPIRY = Long.MAX_VALUE;

    /** An ingredient counts as "expiring soon" when it expires within this many days. */
    public static final int SOON_DAYS = 3;

    /** Expiry dates are stored as text in this format, e.g. 2026-10-31. */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public int id;
    public String name;
    public double quantity;
    public String unit;
    public String expiry;

    public Ingredient(int id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        // The database can return null for optional columns, so normalise to "".
        this.unit = unit == null ? "" : unit;
        this.expiry = expiry == null ? "" : expiry;
    }

    /**
     * Whole days from today until the expiry date.
     * Negative = already expired, 0 = expires today, NO_EXPIRY = no date entered.
     */
    public long daysUntilExpiry() {
        if (expiry.isEmpty()) {
            return NO_EXPIRY;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            format.setLenient(false);
            Date expiryDate = format.parse(expiry);

            // "Today" at midnight, so we compare whole days rather than hours.
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long millis = expiryDate.getTime() - today.getTimeInMillis();
            return Math.round(millis / 86400000.0); // 86,400,000 ms in one day
        } catch (ParseException e) {
            return NO_EXPIRY; // unreadable date: treat as "no expiry"
        }
    }

    /** True if the item is already expired or expires within SOON_DAYS days. */
    public boolean isExpiringSoon() {
        long days = daysUntilExpiry();
        return days != NO_EXPIRY && days <= SOON_DAYS;
    }

    /** Formats 2.0 as "2" and 0.5 as "0.5" so the UI never shows "1.0 pieces". */
    public static String formatQuantity(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        String text = String.format(Locale.US, "%.2f", value);
        return text.replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
