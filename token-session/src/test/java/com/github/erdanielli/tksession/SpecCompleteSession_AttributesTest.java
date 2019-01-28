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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import java.util.UUID;

import static com.github.erdanielli.tksession.SessionAssert.assertThat;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;

/** @author erdanielli */
class SpecCompleteSession_AttributesTest {
  private Session newSession =
      new SpecCompleteSession(mock(ServletContext.class), new NewSession());
  private Session session =
      new SpecCompleteSession(
          mock(ServletContext.class),
          new RestoredTokenSession(UUID.randomUUID(), 0L, 0L, 0, singletonMap("foo", "Bar")));

  @Test
  void shouldAllowNewAttributes() {
    newSession.setAttribute("name", "John");
    assertThat(newSession).hasAttributes("name", "John");

    session.setAttribute("age", 37);
    assertThat(session).hasAttributes("foo", "Bar", "age", 37);
  }

  @Test
  void shouldAllowAttributeSubstitution() {
    session.setAttribute("foo", true);
    assertThat(session).hasAttributes("foo", true);
  }

  @Test
  void shouldAllowAttributeRemoval() {
    newSession.setAttribute("foo", 1);
    newSession.removeAttribute("foo");
    assertThat(newSession).hasNoAttributes();

    session.setAttribute("foo", null);
    assertThat(session).hasNoAttributes();
  }

  @Test
  void shouldSupportDeprecatedApi() {
    Assertions.assertThat(session.getValue("foo"))
        .withFailMessage("session#getValue is broken")
        .isEqualTo("Bar");

    session.putValue("v_name", "Deprecated");
    Assertions.assertThat(session.getValueNames())
        .withFailMessage("session#getValueNames is broken")
        .containsExactlyInAnyOrder("v_name", "foo");

    session.putValue("foo", null);
    session.removeValue("v_name");
    assertThat(session).hasNoAttributes();
  }
}
