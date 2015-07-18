package com.example.lindsey.wayfair_alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.util.HashMap;
import java.util.Random;

/**
 * Public Utility class that contains functions useful for the whole application.
 */
public class Utility {

    private static final Random RAND = new Random();
    private static final HashMap<Integer, Boolean> duplicateMap = new HashMap<>();

    private DialogInterface.OnClickListener l;
    /**
     * Public function that opens up a warning dialog on the specified Context displaying
     * the specified title and message.
     * @param c Context Dialog will be displayed.
     * @param title String The title of the dialog.
     * @param msg String The message displayed in the dialog.
     */
    public static void warningDialog(Context c, String title, String msg) {
        AlertDialog.Builder warning = new AlertDialog.Builder(c);
        warning.setTitle(title);
        warning.setMessage(msg);
        warning.setPositiveButton(android.R.string.yes, null);
        warning.setIcon(android.R.drawable.ic_dialog_alert);
        warning.create().show();
    }

    /**
     * Public function that opens up a warning dialog on the specified Context displaying
     * the specified title, message and OK button.
     * @param c Context Dialog will be displayed.
     * @param title String The title of the dialog.
     * @param msg String The message displayed in the dialog.
     * @param okText String The text of the OK button on the dialog.
     * @param okListener OnClickListener The listener callback for OK button.
     */
    public static void warningDialog(Context c, String title, String msg, String okText,
                                     DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder warning = new AlertDialog.Builder(c);
        warning.setTitle(title);
        warning.setMessage(msg);
        warning.setPositiveButton(android.R.string.yes, null);
        warning.setIcon(android.R.drawable.ic_dialog_alert);
        warning.setPositiveButton(okText, okListener);
        warning.create().show();
    }

    /**
     * Public function that opens up a warning dialog on the specified Context displaying
     * the specified title, message and OK/Cancel button.
     * @param c Context Dialog will be displayed.
     * @param title String The title of the dialog.
     * @param msg String The message displayed in the dialog.
     * @param okText String The text of the OK button on the dialog.
     * @param okListener OnClickListener The listener callback for OK button.
     * @param cancelText String The text of the Cancel button on the dialog.
     * @param cancelListener OnClickListener The listener callback for Cancel button.
     */
    public static void warningDialog(Context c, String title, String msg, String okText,
                                     DialogInterface.OnClickListener okListener, String cancelText,
                                     DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder warning = new AlertDialog.Builder(c);
        warning.setTitle(title);
        warning.setMessage(msg);
        warning.setPositiveButton(android.R.string.yes, null);
        warning.setIcon(android.R.drawable.ic_dialog_alert);
        warning.setPositiveButton(okText, okListener);
        warning.setNegativeButton(cancelText, cancelListener);
        warning.create().show();
    }

    /**
     * Public function that opens up a warning dialog on the specified Context displaying
     * the specified title, message and OK/Cancel button.
     * @param c Context Dialog will be displayed.
     * @param title String The title of the dialog.
     * @param msg String The message displayed in the dialog.
     * @param okText String The text of the OK button on the dialog.
     * @param okListener OnClickListener The listener callback for OK button.
     * @param cancelText String The text of the Cancel button on the dialog.
     * @param cancelListener OnClickListener The listener callback for Cancel button.
     */
    public static void warningDialog(Context c, String title, String msg, String okText,
                                     DialogInterface.OnClickListener okListener, String cancelText,
                                     DialogInterface.OnClickListener cancelListener, String neutralText,
                                     DialogInterface.OnClickListener neutralListener) {
        AlertDialog.Builder warning = new AlertDialog.Builder(c);
        warning.setTitle(title);
        warning.setMessage(msg);
        warning.setPositiveButton(android.R.string.yes, null);
        warning.setIcon(android.R.drawable.ic_dialog_alert);
        warning.setPositiveButton(okText, okListener);
        warning.setNegativeButton(cancelText, cancelListener);
        warning.setNeutralButton(neutralText, neutralListener);
        warning.create().show();
    }


    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     * http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
     */
    public static int randomInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return RAND.nextInt((max - min) + 1) + min;
    }

    /**
     * Generates a pseudo-random number in [min, max] and the random number is guaranteed to not
     * be equal to the number passed in to avoid.
     * @param min Lower bound
     * @param max Upper bound
     * @param avoid The number to avoid
     * @return Integer in [min, max] less the integer to avoid
     */
    public static int randomInt(int min, int max, int avoid) {
        int randomNum = 0;
        while (avoid == randomNum)
            randomNum = RAND.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /**
     * The same function as {@link #randomInt(int, int) randomInt} method
     * except that this version guarantees that the random numbers generated
     * before will not appear again until the session is cleared by calling
     * {@link #clearSession() clearSession}.
     *
     * Note duplicates might be actually returned when there isn't any more numbers
     * that can be generated. As session gets filled, the running time scales up.
     */
    public static int randomIntNonDuplicated(int min, int max) {
        //session filled
        if (max - min + 1 == duplicateMap.size())
            return min;

        int randomNum;
        do {
            randomNum = RAND.nextInt((max - min) + 1) + min;
        } while (duplicateMap.containsKey(randomNum));

        duplicateMap.put(randomNum, true);
        return randomNum;
    }

    /**
     * The same function as {@link #randomInt(int, int, int) randomInt} method
     * except that this version guarantees that the random numbers generated
     * before will not appear again until the session is cleared by calling
     * {@link #clearSession() clearSession}.
     *
     * Note duplicates might be actually returned when there isn't any more numbers
     * that can be generated. As session gets filled, the running time scales up.
     */
    public static int randomIntNonDuplicated(int min, int max, int avoid) {
        //session filled
        if (max - min + 1 == duplicateMap.size())
            return min;

        int randomNum;
        do {
            randomNum = RAND.nextInt((max - min) + 1) + min;
        } while (avoid == randomNum || duplicateMap.containsKey(randomNum));

        duplicateMap.put(randomNum, true);
        return randomNum;
    }

    /**
     * Starts a new session for generating non-duplicated random integers.
     */
    public static void clearSession() {
        duplicateMap.clear();
    }

    /**
     * Combine two strings in a sorted manner, where the string that appears lexicographically
     * after the other gets appended to the first.
     * @param a String The first string.
     * @param b String The second string.
     * @return String the combined string of a and b.
     */
    public static String combineStringSorted(String a, String b) {
        Log.d("combined", "" + a + "//" + b);
        int compare = a.compareToIgnoreCase(b);
        if (compare < 0) {
            return a + b;
        } else {
            return b + a;
        }
    }
}
