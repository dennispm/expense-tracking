package com.example.expensetracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
    }

    @Test
    void testEncryptAndDecrypt() throws Exception {
        String plainText = "MySecretPassword123!";
        String masterPassword = "MasterPass123!";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);

        String decrypted = encryptionService.decrypt(encrypted, masterPassword);
        assertEquals(plainText, decrypted);
    }

    @Test
    void testEncryptProducesDifferentOutputEachTime() throws Exception {
        String plainText = "MySecretPassword123!";
        String masterPassword = "MasterPass123!";

        String encrypted1 = encryptionService.encrypt(plainText, masterPassword);
        String encrypted2 = encryptionService.encrypt(plainText, masterPassword);

        assertNotEquals(encrypted1, encrypted2, "Encryption should produce different output each time due to random salt and IV");
    }

    @Test
    void testDecryptWithWrongPassword() throws Exception {
        String plainText = "MySecretPassword123!";
        String masterPassword = "MasterPass123!";
        String wrongPassword = "WrongPass123!";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);

        assertThrows(Exception.class, () -> {
            encryptionService.decrypt(encrypted, wrongPassword);
        });
    }

    @Test
    void testEncryptEmptyString() throws Exception {
        String plainText = "";
        String masterPassword = "MasterPass123!";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);
        assertNotNull(encrypted);

        String decrypted = encryptionService.decrypt(encrypted, masterPassword);
        assertEquals(plainText, decrypted);
    }

    @Test
    void testEncryptLongString() throws Exception {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("This is a very long password with lots of characters. ");
        }
        String plainText = longText.toString();
        String masterPassword = "MasterPass123!";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);
        assertNotNull(encrypted);

        String decrypted = encryptionService.decrypt(encrypted, masterPassword);
        assertEquals(plainText, decrypted);
    }

    @Test
    void testEncryptWithSpecialCharacters() throws Exception {
        String plainText = "P@ssw0rd!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        String masterPassword = "MasterPass123!";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);
        assertNotNull(encrypted);

        String decrypted = encryptionService.decrypt(encrypted, masterPassword);
        assertEquals(plainText, decrypted);
    }

    @Test
    void testEncryptWithUnicodeCharacters() throws Exception {
        String plainText = "密码123!こんにちは🔒";
        String masterPassword = "MasterPass123!";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);
        assertNotNull(encrypted);

        String decrypted = encryptionService.decrypt(encrypted, masterPassword);
        assertEquals(plainText, decrypted);
    }

    @Test
    void testDecryptInvalidBase64() {
        String invalidEncrypted = "not-valid-base64!@#$";
        String masterPassword = "MasterPass123!";

        assertThrows(Exception.class, () -> {
            encryptionService.decrypt(invalidEncrypted, masterPassword);
        });
    }

    @Test
    void testDecryptTruncatedData() throws Exception {
        String plainText = "MySecretPassword123!";
        String masterPassword = "MasterPass123!";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);
        String truncated = encrypted.substring(0, encrypted.length() / 2);

        assertThrows(Exception.class, () -> {
            encryptionService.decrypt(truncated, masterPassword);
        });
    }

    @Test
    void testMultipleEncryptDecryptCycles() throws Exception {
        String plainText = "MySecretPassword123!";
        String masterPassword = "MasterPass123!";

        for (int i = 0; i < 10; i++) {
            String encrypted = encryptionService.encrypt(plainText, masterPassword);
            String decrypted = encryptionService.decrypt(encrypted, masterPassword);
            assertEquals(plainText, decrypted, "Cycle " + i + " failed");
        }
    }

    @Test
    void testDifferentMasterPasswords() throws Exception {
        String plainText = "MySecretPassword123!";
        String masterPassword1 = "MasterPass123!";
        String masterPassword2 = "DifferentPass456!";

        String encrypted1 = encryptionService.encrypt(plainText, masterPassword1);
        String encrypted2 = encryptionService.encrypt(plainText, masterPassword2);

        String decrypted1 = encryptionService.decrypt(encrypted1, masterPassword1);
        String decrypted2 = encryptionService.decrypt(encrypted2, masterPassword2);

        assertEquals(plainText, decrypted1);
        assertEquals(plainText, decrypted2);

        assertThrows(Exception.class, () -> {
            encryptionService.decrypt(encrypted1, masterPassword2);
        });

        assertThrows(Exception.class, () -> {
            encryptionService.decrypt(encrypted2, masterPassword1);
        });
    }

    @Test
    void testEncryptedDataIsBase64() throws Exception {
        String plainText = "MySecretPassword123!";
        String masterPassword = "MasterPass123!";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);

        assertTrue(encrypted.matches("^[A-Za-z0-9+/]*={0,2}$"), "Encrypted data should be valid Base64");
    }

    @Test
    void testEncryptWithShortMasterPassword() throws Exception {
        String plainText = "MySecretPassword123!";
        String masterPassword = "a";

        String encrypted = encryptionService.encrypt(plainText, masterPassword);
        assertNotNull(encrypted);

        String decrypted = encryptionService.decrypt(encrypted, masterPassword);
        assertEquals(plainText, decrypted);
    }

    @Test
    void testEncryptWithVeryLongMasterPassword() throws Exception {
        String plainText = "MySecretPassword123!";
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longPassword.append("VeryLongMasterPassword");
        }
        String masterPassword = longPassword.toString();

        String encrypted = encryptionService.encrypt(plainText, masterPassword);
        assertNotNull(encrypted);

        String decrypted = encryptionService.decrypt(encrypted, masterPassword);
        assertEquals(plainText, decrypted);
    }
}
