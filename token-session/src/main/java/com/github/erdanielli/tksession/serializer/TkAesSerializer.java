/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.erdanielli.tksession.serializer;

import com.github.erdanielli.tksession.Session;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * @author erdanielli
 */
public final class TkAesSerializer extends TkInMemorySerializer {
    private final SecretKeySpec key;
    private final TkSerializer original;

    public TkAesSerializer(String plainSecret, TkSerializer next) {
        this.original = next;
        this.key = createKey(plainSecret);

        // fast check
        initCipher(DECRYPT_MODE);
    }

    @Override
    protected Session read(byte[] token) {
        try (final InputStream decryptedInput = decrypt(token)) {
            return original.read(decryptedInput);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void write(Session session, OutputStream out) {
        try {
            out.write(encrypt(forwardWrite(session, original)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private SecretKeySpec createKey(String plainSecret) {
        try {
            final byte[] fullMd5key = MessageDigest.getInstance("MD5").digest(plainSecret.getBytes(UTF_8));
            final byte[] shortKey = Arrays.copyOf(fullMd5key, 16);
            return new SecretKeySpec(shortKey, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("MD5 not supported", e);
        }
    }

    private Cipher initCipher(int mode) {
        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, key);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalArgumentException("AES not supported", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid AES key", e);
        }
    }

    private InputStream decrypt(byte[] encryptedInput) {
        try {
            return new ByteArrayInputStream(initCipher(DECRYPT_MODE).doFinal(encryptedInput));
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalArgumentException("AES decryption failed");
        }
    }

    private byte[] encrypt(byte[] plainInput) {
        try {
            return initCipher(ENCRYPT_MODE).doFinal(plainInput);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalArgumentException("AES encryption failed");
        }
    }
}
