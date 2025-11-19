package com.example.expensetracker.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        assertTrue(PasswordValidator.isValid("Test123!"));
        assertTrue(PasswordValidator.isValid("Abcd1234!@#$"));
        assertTrue(PasswordValidator.isValid("MyP@ssw0rd"));
        assertTrue(PasswordValidator.isValid("Secure123!Pass"));
    }

    @Test
    void testPasswordTooShort() {
        assertFalse(PasswordValidator.isValid("Test1!"));
        assertFalse(PasswordValidator.isValid("Abc123!"));
        assertFalse(PasswordValidator.isValid(""));
    }

    @Test
    void testPasswordMissingUppercase() {
        assertFalse(PasswordValidator.isValid("test123!"));
        assertFalse(PasswordValidator.isValid("password123!"));
        assertFalse(PasswordValidator.isValid("mypass123!"));
    }

    @Test
    void testPasswordMissingLowercase() {
        assertFalse(PasswordValidator.isValid("TEST123!"));
        assertFalse(PasswordValidator.isValid("PASSWORD123!"));
        assertFalse(PasswordValidator.isValid("MYPASS123!"));
    }

    @Test
    void testPasswordMissingDigit() {
        assertFalse(PasswordValidator.isValid("TestPass!"));
        assertFalse(PasswordValidator.isValid("Password!"));
        assertFalse(PasswordValidator.isValid("MyPass!"));
    }

    @Test
    void testPasswordMissingSpecialChar() {
        assertFalse(PasswordValidator.isValid("TestPass123"));
        assertFalse(PasswordValidator.isValid("Password123"));
        assertFalse(PasswordValidator.isValid("MyPass123"));
    }

    @Test
    void testNullPassword() {
        assertFalse(PasswordValidator.isValid(null));
    }

    @Test
    void testValidationMessage() {
        String message = PasswordValidator.getValidationMessage();
        assertNotNull(message);
        assertTrue(message.contains("8 characters"));
        assertTrue(message.contains("uppercase"));
        assertTrue(message.contains("lowercase"));
        assertTrue(message.contains("number"));
        assertTrue(message.contains("special character"));
    }

    @Test
    void testValidateAndGetError_ValidPassword() {
        assertNull(PasswordValidator.validateAndGetError("Test123!"));
        assertNull(PasswordValidator.validateAndGetError("Abcd1234!@#$"));
    }

    @Test
    void testValidateAndGetError_NullPassword() {
        String error = PasswordValidator.validateAndGetError(null);
        assertNotNull(error);
        assertTrue(error.contains("required"));
    }

    @Test
    void testValidateAndGetError_EmptyPassword() {
        String error = PasswordValidator.validateAndGetError("");
        assertNotNull(error);
        assertTrue(error.contains("required"));
    }

    @Test
    void testValidateAndGetError_TooShort() {
        String error = PasswordValidator.validateAndGetError("Test1!");
        assertNotNull(error);
        assertTrue(error.contains("8 characters"));
    }

    @Test
    void testValidateAndGetError_MissingUppercase() {
        String error = PasswordValidator.validateAndGetError("test123!");
        assertNotNull(error);
        assertTrue(error.contains("uppercase"));
    }

    @Test
    void testValidateAndGetError_MissingLowercase() {
        String error = PasswordValidator.validateAndGetError("TEST123!");
        assertNotNull(error);
        assertTrue(error.contains("lowercase"));
    }

    @Test
    void testValidateAndGetError_MissingDigit() {
        String error = PasswordValidator.validateAndGetError("TestPass!");
        assertNotNull(error);
        assertTrue(error.contains("number"));
    }

    @Test
    void testValidateAndGetError_MissingSpecialChar() {
        String error = PasswordValidator.validateAndGetError("TestPass123");
        assertNotNull(error);
        assertTrue(error.contains("special character"));
    }

    @Test
    void testVariousSpecialCharacters() {
        assertTrue(PasswordValidator.isValid("Test123!"));
        assertTrue(PasswordValidator.isValid("Test123@"));
        assertTrue(PasswordValidator.isValid("Test123#"));
        assertTrue(PasswordValidator.isValid("Test123$"));
        assertTrue(PasswordValidator.isValid("Test123%"));
        assertTrue(PasswordValidator.isValid("Test123^"));
        assertTrue(PasswordValidator.isValid("Test123&"));
        assertTrue(PasswordValidator.isValid("Test123*"));
        assertTrue(PasswordValidator.isValid("Test123("));
        assertTrue(PasswordValidator.isValid("Test123)"));
        assertTrue(PasswordValidator.isValid("Test123_"));
        assertTrue(PasswordValidator.isValid("Test123+"));
        assertTrue(PasswordValidator.isValid("Test123-"));
        assertTrue(PasswordValidator.isValid("Test123="));
        assertTrue(PasswordValidator.isValid("Test123["));
        assertTrue(PasswordValidator.isValid("Test123]"));
        assertTrue(PasswordValidator.isValid("Test123{"));
        assertTrue(PasswordValidator.isValid("Test123}"));
        assertTrue(PasswordValidator.isValid("Test123;"));
        assertTrue(PasswordValidator.isValid("Test123:"));
        assertTrue(PasswordValidator.isValid("Test123'"));
        assertTrue(PasswordValidator.isValid("Test123\""));
        assertTrue(PasswordValidator.isValid("Test123\\"));
        assertTrue(PasswordValidator.isValid("Test123|"));
        assertTrue(PasswordValidator.isValid("Test123,"));
        assertTrue(PasswordValidator.isValid("Test123."));
        assertTrue(PasswordValidator.isValid("Test123<"));
        assertTrue(PasswordValidator.isValid("Test123>"));
        assertTrue(PasswordValidator.isValid("Test123/"));
        assertTrue(PasswordValidator.isValid("Test123?"));
    }
}
