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
package com.github.erdanielli.tksession.listener;

import com.github.erdanielli.tksession.Session;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** @author erdanielli */
final class AllNotifiers implements SessionListenerNotifier {
  private static final SessionListenerNotifier NULL =
      new SessionListenerNotifier() {
        @Override
        public Session observe(Session session) {
          return new UnobservedSession(session);
        }
      };
  private final List<SessionListenerNotifier> notifiers;

  static SessionListenerNotifier combine(SessionListenerNotifier... instances) {
    final List<SessionListenerNotifier> list =
        Stream.of(instances).filter(Objects::nonNull).collect(Collectors.toList());

    return list.isEmpty() ? NULL : new AllNotifiers(list);
  }

  private AllNotifiers(List<SessionListenerNotifier> notifiers) {
    this.notifiers = notifiers;
  }

  @Override
  public void sessionCreated(HttpSession s) {
    notifiers.forEach(it -> it.sessionCreated(s));
  }

  @Override
  public void sessionDestroyed(HttpSession s) {
    notifiers.forEach(it -> it.sessionDestroyed(s));
  }

  @Override
  public void attributeAdded(HttpSession s, String attrName, Object value) {
    notifiers.forEach(it -> it.attributeAdded(s, attrName, value));
  }

  @Override
  public void attributeRemoved(HttpSession s, String attrName, Object prevValue) {
    notifiers.forEach(it -> it.attributeRemoved(s, attrName, prevValue));
  }

  @Override
  public void attributeReplaced(HttpSession s, String attrName, Object prevValue, Object newValue) {
    notifiers.forEach(it -> it.attributeReplaced(s, attrName, prevValue, newValue));
  }
}
