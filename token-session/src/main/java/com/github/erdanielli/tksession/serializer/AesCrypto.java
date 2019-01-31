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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/** @author erdanielli */
final class AesCrypto {
  private static final String ALG = "AES/GCM/NoPadding";
  private final SecureRandom secureRandom;
  private final SecretKeySpec key;
  private final Cipher encryptor;
  private final Cipher decryptor;

  AesCrypto(String plainSecret) {
    secureRandom = new SecureRandom();
    key = createKey(plainSecret);
    try {
      encryptor = Cipher.getInstance(ALG);
      decryptor = Cipher.getInstance(ALG);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new IllegalArgumentException("AES not supported", e);
    }
  }

  byte[] decrypt(byte[] encryptedInput) {
    synchronized (decryptor) {
      try {
        final byte[] iv = iv(encryptedInput);
        final byte[] input = subArray(encryptedInput, iv.length);
        return initCipher(decryptor, DECRYPT_MODE, iv).doFinal(input);
      } catch (BadPaddingException | IllegalBlockSizeException e) {
        throw new IllegalArgumentException("AES decryption failed", e);
      }
    }
  }

  byte[] encrypt(byte[] plainInput) {
    synchronized (encryptor) {
      try {
        final byte[] iv = iv();
        final byte[] encrypted = initCipher(encryptor, ENCRYPT_MODE, iv).doFinal(plainInput);
        return concatenate(iv, encrypted);
      } catch (BadPaddingException | IllegalBlockSizeException e) {
        throw new IllegalArgumentException("AES encryption failed", e);
      }
    }
  }

  private SecretKeySpec createKey(String plainSecret) {
    try {
      final byte[] fullMd5key =
          MessageDigest.getInstance("MD5").digest(plainSecret.getBytes(UTF_8));
      final byte[] shortKey = Arrays.copyOf(fullMd5key, 16);
      return new SecretKeySpec(shortKey, "AES");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException("MD5 not supported", e);
    }
  }

  private Cipher initCipher(Cipher cipher, int mode, byte[] iv) {
    try {
      cipher.init(mode, key, new GCMParameterSpec(128, iv));
      return cipher;
    } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
      throw new IllegalArgumentException("Invalid AES key", e);
    }
  }

  private byte[] iv() {
    final byte[] result = new byte[16];
    secureRandom.nextBytes(result);
    return result;
  }

  private byte[] iv(byte[] encryptedInput) {
    final byte[] result = new byte[16];
    System.arraycopy(encryptedInput, 0, result, 0, result.length);
    return result;
  }

  private byte[] concatenate(byte[] first, byte[] second) {
    final byte[] result = new byte[first.length + second.length];
    System.arraycopy(first, 0, result, 0, first.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  private byte[] subArray(byte[] array, int skippedBytes) {
    final byte[] result = new byte[array.length - skippedBytes];
    System.arraycopy(array, skippedBytes, result, 0, result.length);
    return result;
  }
}
