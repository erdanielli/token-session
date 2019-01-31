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

import com.github.erdanielli.tksession.Session;
import com.github.erdanielli.tksession.SpecCompleteSession;
import com.github.erdanielli.tksession.listener.SessionListenerNotifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.time.Duration;

/** @author erdanielli */
abstract class TkRequest extends HttpServletRequestWrapper {
  private final Duration ttl;
  private final SessionListenerNotifier notifier;
  private Session session;

  TkRequest(Duration ttl, SessionListenerNotifier notifier, HttpServletRequest request) {
    super(request);
    this.ttl = ttl;
    this.notifier = notifier;
  }

  @Override
  public final Session getSession() {
    return getSession(true);
  }

  @Override
  public final Session getSession(boolean create) {
    if (create && session == null) {
      session =
          new SpecCompleteSession(getServletContext(), notifier.observe(createIncompleteSession()));
      session.setMaxInactiveInterval((int) ttl.getSeconds());
    }
    return session;
  }

  abstract Session createIncompleteSession();
}
