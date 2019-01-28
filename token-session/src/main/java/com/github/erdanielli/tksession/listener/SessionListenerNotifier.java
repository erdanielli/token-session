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

/** @author erdanielli */
public interface SessionListenerNotifier {

  default Session observe(Session session) {
    return new ObservedSession(this, session);
  }

  // HttpSessionListener

  default void sessionCreated(HttpSession s) {}

  default void sessionDestroyed(HttpSession s) {}

  // HttpSessionAttributeListener

  default void attributeAdded(HttpSession s, String attrName, Object value) {}

  default void attributeRemoved(HttpSession s, String attrName, Object prevValue) {}

  default void attributeReplaced(
      HttpSession s, String attrName, Object prevValue, Object newValue) {}
}
