
package com.richfield.smartpantry;

import java.text.ParsePosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Ingredient {

    public static final long NO_EXPIRY = Long.MAX_VALUE;
    public static final int SOON_DAYS = 3;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public int id;
    public String name;
    public double quantity;
    public String unit;
    public String expiry;

    public Ingredient(int id, String name, double quantity,
                      String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit == null ? "" : unit;
        this.expiry = expiry == null ? "" : expiry;
    }

    // Check whether the ingredient contains valid information.
    public boolean isValid() {
        return name != null
                && !name.trim().isEmpty()
                && Double.isFinite(quantity)
                && quantity > 0
                && isValidExpiry(expiry);
    }

    // Expiry date is optional, but must be valid if entered.
    private boolean isValidExpiry(String date) {
        if (date == null || date.trim().isEmpty()) {
            return true;
        }

        SimpleDateFormat format =
                new SimpleDateFormat(DATE_FORMAT, Locale.US);
        format.setLenient(false);

        ParsePosition position = new ParsePosition(0);
        Date parsedDate = format.parse(date, position);

        return parsedDate != null
                && position.getIndex() == date.length();
    }

    // Whole days from today until expiry.
    public long daysUntilExpiry() {
        if (expiry.isEmpty()) {
            return NO_EXPIRY;
        }

        try {
            SimpleDateFormat format =
                    new SimpleDateFormat(DATE_FORMAT, Locale.US);
            format.setLenient(false);
            Date expiryDate = format.parse(expiry);

            if (expiryDate == null) {
                return NO_EXPIRY;
            }

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long millis =
                    expiryDate.getTime() - today.getTimeInMillis();

            return Math.round(millis / 86400000.0);

        } catch (ParseException e) {
            return NO_EXPIRY;
        }
    }

    // True if expired or expiring within three days.
    public boolean isExpiringSoon() {
        long days = daysUntilExpiry();
        return days != NO_EXPIRY && days <= SOON_DAYS;
    }

    // Display whole quantities without unnecessary decimal places.
    public static String formatQuantity(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }

        String text = String.format(Locale.US, "%.2f", value);
        return text.replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
