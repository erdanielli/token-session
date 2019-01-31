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
package com.github.erdanielli.tksession.filter;

import com.github.erdanielli.tksession.NewSession;
import com.github.erdanielli.tksession.Session;
import com.github.erdanielli.tksession.listener.SessionListenerNotifier;
import com.github.erdanielli.tksession.serializer.TkBase64Serializer;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;

/** @author erdanielli */
final class TkRequestWithHeader extends TkRequest {
  private final String token;
  private final TkBase64Serializer serializer;

  TkRequestWithHeader(
          String token,
          TkBase64Serializer serializer,
          Duration ttl, SessionListenerNotifier notifier,
          HttpServletRequest request) {
    super(ttl, notifier, request);
    this.token = token;
    this.serializer = serializer;
  }

  @Override
  Session createIncompleteSession() {
    final Session restored = serializer.readToken(token);
    if (restored.expired()) {
      return new NewSession();
    }
    return new RenewedSession(restored);
  }
}