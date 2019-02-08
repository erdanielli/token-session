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

import com.github.erdanielli.tksession.RestoredTokenSession;
import com.github.erdanielli.tksession.Session;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.erdanielli.tksession.serializer.SuppressedExceptions.readObject;

/** @author erdanielli */
public final class TkJdkSerializer implements TkSerializer {

  @Override
  public byte[] write(Session session) {
    return SuppressedExceptions.writeToByteArray(
        output -> {
          writeSessionId(output, session);
          output.writeLong(session.getCreationTime());
          output.writeLong(session.getLastAccessedTime());
          output.writeInt(session.getMaxInactiveInterval());
          writeAttributes(output, session);
        });
  }

  @Override
  public Session read(byte[] bytes) {
    try (final ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      final UUID sessionId = readSessionId(input);
      final long creationTime = input.readLong();
      final long lastAccessedTime = input.readLong();
      final int maxInactiveInterval = input.readInt();
      return new RestoredTokenSession(
          sessionId, creationTime, lastAccessedTime, maxInactiveInterval, readAttributes(input));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void writeSessionId(ObjectOutput output, Session s) throws IOException {
    final UUID uuid = s.getUUID();
    output.writeLong(uuid.getMostSignificantBits());
    output.writeLong(uuid.getLeastSignificantBits());
  }

  private UUID readSessionId(ObjectInputStream input) throws IOException {
    final long most = input.readLong();
    final long least = input.readLong();
    return new UUID(most, least);
  }

  private void writeAttributes(ObjectOutput output, Session s) throws IOException {
    final Map<String, Object> m = s.attributes();
    output.writeInt(m.size());
    for (Map.Entry<String, Object> e : m.entrySet()) {
      output.writeObject(e.getKey());
      output.writeObject(e.getValue());
    }
  }

  private Map<String, Object> readAttributes(ObjectInputStream input) throws IOException {
    final int size = input.readInt();
    if (size == 0) {
      return Collections.emptyMap();
    }
    final Map<String, Object> m = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      final String key = readObject(input);
      m.put(key, readObject(input));
    }
    return m;
  }
}
