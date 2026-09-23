package com.example

import com.example.data.security.CryptoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CryptoManagerTest {

    @Test
    fun testEncryptionAndDecryption() {
        val originalToken = "stream_token_trt1_hd_secret_auth_key_2025"
        val encrypted = CryptoManager.encrypt(originalToken)

        // Encrypted text must not be equal to plain text
        assertNotEquals(originalToken, encrypted)
        assertTrue(encrypted.isNotBlank())

        // Decrypted text must match original plain text exactly
        val decrypted = CryptoManager.decrypt(encrypted)
        assertEquals(originalToken, decrypted)
    }

    @Test
    fun testChecksumDeterministic() {
        val data = "test_channel_payload_data"
        val hash1 = CryptoManager.generateChecksum(data)
        val hash2 = CryptoManager.generateChecksum(data)

        assertEquals(hash1, hash2)
        assertEquals(16, hash1.length)
    }
}
