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

import java.util.Base64;

/** @author erdanielli */
public final class TkBase64Serializer implements TkSerializer {
  private final TkSerializer next;

  public TkBase64Serializer(TkSerializer next) {
    this.next = next;
  }

  /**
   * First decode base64 then forwards to the next serializer.
   *
   * @param base64Token Header value from request
   * @return A restored session
   */
  public Session readToken(String base64Token) {
    try {
      return next.read(Base64.getDecoder().decode(base64Token));
    } catch (RuntimeException e) {
      throw new InvalidTokenException(e);
    }
  }

  /**
   * Writes at the end of the chain, encoding the incoming bytes in base64 format
   *
   * @param session a required session
   * @return a base64 encoded token representation of the session
   */
  public String writeToken(Session session) {
    return Base64.getEncoder().encodeToString(next.write(session));
  }

  @Override
  public Session read(byte[] token) {
    return next.read(Base64.getDecoder().decode(token));
  }

  /**
   * Writes at the end of the chain, encoding the bytes in base64 format
   *
   * @param session a required session
   */
  @Override
  public byte[] write(Session session) {
    return Base64.getEncoder().encode(next.write(session));
  }
}
