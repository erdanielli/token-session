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

import java.io.*;

/** @author erdanielli */
@SuppressWarnings("squid:S1610")
abstract class SuppressedExceptions {

  @SuppressWarnings("unchecked")
  static <T> T readObject(ObjectInput input) throws IOException {
    try {
      return (T) input.readObject();
    } catch (ClassNotFoundException e) {
      throw new IOException("unknown type", e);
    }
  }

  static byte[] writeToByteArray(CheckedConsumer cn) {
    final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (final ObjectOutputStream output = new ObjectOutputStream(bytes)) {
      cn.accept(output);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return bytes.toByteArray();
  }

  private SuppressedExceptions() {}

  @FunctionalInterface
  interface CheckedConsumer {
    void accept(ObjectOutput output) throws IOException;
  }
}
