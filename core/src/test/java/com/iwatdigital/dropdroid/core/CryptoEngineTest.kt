package com.iwatdigital.dropdroid.core

import com.iwatdigital.dropdroid.core.crypto.CryptoEngine
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class CryptoEngineTest {
    @Test
    fun `encrypt and decrypt chunk roundtrips`() {
        val local = CryptoEngine.generateKeyPair()
        val remote = CryptoEngine.generateKeyPair()

        val sessionA = CryptoEngine.deriveSessionKeys(local.keyPair, remote.publicKeyBytes)
        val sessionB = CryptoEngine.deriveSessionKeys(remote.keyPair, local.publicKeyBytes)

        val nonce = CryptoEngine.nextNonce(1)
        val payload = "hello-dropdroid".encodeToByteArray()

        val encrypted = CryptoEngine.encryptChunk(sessionA.aesKey, nonce, payload)
        val decrypted = CryptoEngine.decryptChunk(sessionB.aesKey, nonce, encrypted)

        assertArrayEquals(payload, decrypted)
        assertEquals(sessionA.authenticationCode.length, 6)
    }

    @Test
    fun `sha256 produces deterministic hash`() {
        val hash = CryptoEngine.sha256("abc".encodeToByteArray())
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", hash)
    }
}
