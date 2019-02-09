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
package com.github.erdanielli.tksession;

import com.github.erdanielli.tksession.listener.SessionListenerNotifier;
import com.github.erdanielli.tksession.listener.SessionListenerNotifierBuilder;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/** @author erdanielli */
class SpecCompleteSession_InvalidationTest {
  private SessionListenerNotifier notifier = new SessionListenerNotifierBuilder().build();
  private Session session =
      new SpecCompleteSession(mock(ServletContext.class), notifier.observe(new NewSession()));

  @Test
  void allowedMethodsAfterInvalidation() {
    session.invalidate();

    session.getId();
    session.setMaxInactiveInterval(10);
    session.getMaxInactiveInterval();
    session.getServletContext();
    assertThat(session.expired()).isTrue();
  }

  @Test
  void forbiddenMethodsAfterInvalidation() {
    session.invalidate();
    shouldThrow(session::getCreationTime, "getCreationTime");
    shouldThrow(session::getLastAccessedTime, "getLastAccessedTime");
    shouldThrow(() -> session.getAttribute("attr"), "getAttribute(String)");
    shouldThrow(session::getAttributeNames, "getAttributeNames");
    shouldThrow(() -> session.setAttribute("name", 1), "setAttribute(String,Object)");
    shouldThrow(() -> session.removeAttribute("name"), "removeAttribute(String)");
    shouldThrow(session::invalidate, "invalidate");
    shouldThrow(session::isNew, "isNew");
  }

  private void shouldThrow(ThrowableAssert.ThrowingCallable fn, String m) {
    assertThatThrownBy(fn)
        .withFailMessage(
            "SpecCompleteSession#%s should throw an IllegalStateException after invalidation", m)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("session was invalidated");
  }
}
