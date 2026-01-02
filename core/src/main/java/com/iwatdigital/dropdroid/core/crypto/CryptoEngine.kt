package com.iwatdigital.dropdroid.core.crypto

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoEngine {
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val AES_KEY_SIZE = 32
    private const val NONCE_SIZE = 12

    data class KeyMaterial(
        val keyPair: KeyPair,
        val publicKeyBytes: ByteArray
    )

    data class SessionKeys(
        val aesKey: SecretKey,
        val authenticationCode: String
    )

    fun generateKeyPair(): KeyMaterial {
        val generator = KeyPairGenerator.getInstance("X25519")
        generator.initialize(255, SecureRandom())
        val keyPair = generator.generateKeyPair()
        return KeyMaterial(
            keyPair = keyPair,
            publicKeyBytes = keyPair.public.encoded
        )
    }

    fun deriveSessionKeys(
        localKeyPair: KeyPair,
        remotePublicKeyBytes: ByteArray
    ): SessionKeys {
        val remotePublicKey = KeyFactory.getInstance("X25519")
            .generatePublic(X509EncodedKeySpec(remotePublicKeyBytes))
        val keyAgreement = KeyAgreement.getInstance("X25519")
        keyAgreement.init(localKeyPair.private)
        keyAgreement.doPhase(remotePublicKey, true)
        val sharedSecret = keyAgreement.generateSecret()
        val kdfSalt = sharedSecret.copyOfRange(0, 16)
        val derived = hkdf(sharedSecret, kdfSalt, AES_KEY_SIZE)
        val aesKey = SecretKeySpec(derived, "AES")
        val authCode = computeAuthCode(sharedSecret)
        return SessionKeys(aesKey = aesKey, authenticationCode = authCode)
    }

    private fun hkdf(secret: ByteArray, salt: ByteArray, size: Int): ByteArray {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(
            secret.toHex().toCharArray(),
            salt,
            10_000,
            size * 8
        )
        val key = factory.generateSecret(spec).encoded
        return key.copyOf(size)
    }

    fun encryptChunk(
        sessionKey: SecretKey,
        nonce: ByteArray,
        chunk: ByteArray,
        associatedData: ByteArray? = null
    ): ByteArray {
        require(nonce.size == NONCE_SIZE) { "Nonce must be $NONCE_SIZE bytes" }
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, nonce)
        cipher.init(Cipher.ENCRYPT_MODE, sessionKey, spec)
        associatedData?.let { cipher.updateAAD(it) }
        return cipher.doFinal(chunk)
    }

    fun decryptChunk(
        sessionKey: SecretKey,
        nonce: ByteArray,
        payload: ByteArray,
        associatedData: ByteArray? = null
    ): ByteArray {
        require(nonce.size == NONCE_SIZE) { "Nonce must be $NONCE_SIZE bytes" }
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, nonce)
        cipher.init(Cipher.DECRYPT_MODE, sessionKey, spec)
        associatedData?.let { cipher.updateAAD(it) }
        return cipher.doFinal(payload)
    }

    fun nextNonce(counter: Long): ByteArray {
        val nonce = ByteArray(NONCE_SIZE)
        for (i in nonce.indices) {
            val shift = (nonce.size - 1 - i) * 8
            nonce[i] = (counter shr shift).toByte()
        }
        return nonce
    }

    fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(bytes).toHex()
    }

    fun computeAuthCode(sharedSecret: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(sharedSecret)
        val short = hash.take(4)
        val intValue = short.fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }
        return (intValue % 1000000).toString().padStart(6, '0')
    }
}

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
