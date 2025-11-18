package com.example.expensetracker.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    public static boolean isValid(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }

        return UPPERCASE_PATTERN.matcher(password).find() &&
               LOWERCASE_PATTERN.matcher(password).find() &&
               DIGIT_PATTERN.matcher(password).find() &&
               SPECIAL_CHAR_PATTERN.matcher(password).find();
    }

    public static String getValidationMessage() {
        return "Password must be at least 8 characters long and include at least one uppercase letter, " +
               "one lowercase letter, one number, and one special character.";
    }

    public static String validateAndGetError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required.";
        }
        if (password.length() < MIN_LENGTH) {
            return "Password must be at least 8 characters long.";
        }
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return "Password must include at least one uppercase letter.";
        }
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return "Password must include at least one lowercase letter.";
        }
        if (!DIGIT_PATTERN.matcher(password).find()) {
            return "Password must include at least one number.";
        }
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return "Password must include at least one special character.";
        }
        return null;
    }
}
