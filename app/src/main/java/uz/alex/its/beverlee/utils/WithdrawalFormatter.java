package uz.alex.its.beverlee.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class WithdrawalFormatter {
    private static final int MASTER_CARD_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int VISA_CARD_TOTAL_SYMBOLS_MIN = 16; // size of pattern 0000-0000-0000-0000
    private static final int VISA_CARD_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000

    private static final int MASTER_CARD_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int VISA_TOTAL_DIGITS_MIN = 13;
    private static final int VISA_TOTAL_DIGITS_MAX = 16;

    private static final char DIVIDER = ' ';

    private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0

    public static TextWatcher getMasterCardFormat() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!isInputCorrect(s, 0, MASTER_CARD_TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, MASTER_CARD_TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }
        };
    }

    public static TextWatcher getVisaCardFormat() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!isInputCorrect(s, VISA_CARD_TOTAL_SYMBOLS_MIN, VISA_CARD_TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, getTotalDigits(s)), DIVIDER_POSITION, DIVIDER));
                }
            }
        };
    }

    private static boolean isInputCorrect(final Editable s, final int totalSymbolsMin, final int totalSymbolsMax, final int dividerModulo, final char divider) {
        boolean isCorrect = false;

        if (totalSymbolsMin == 0) {
            isCorrect = s.length() <= totalSymbolsMax; // check size of entered string
        }
        else {
            isCorrect = s.length() >= totalSymbolsMin && s.length() <= totalSymbolsMax; // check size of entered string
        }

        for (int i = 0; i < s.length(); i++) { // check that every element is right
            if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private static int getTotalDigits(final Editable s) {
        return s.length();
    }

    private static String buildCorrectString(final char[] digits, final int dividerPosition, final char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private static char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }
}
