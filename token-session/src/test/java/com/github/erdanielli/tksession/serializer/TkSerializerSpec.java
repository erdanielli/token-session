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

import com.github.erdanielli.tksession.NewSession;
import com.github.erdanielli.tksession.RestoredTokenSession;
import com.github.erdanielli.tksession.Session;
import com.github.erdanielli.tksession.SpecCompleteSession;
import com.github.erdanielli.tksession.listener.SessionListenerNotifier;
import com.github.erdanielli.tksession.listener.SessionListenerNotifierBuilder;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/** @author erdanielli */
abstract class TkSerializerSpec {
  private SessionListenerNotifier notifier = new SessionListenerNotifierBuilder().build();

  abstract TkSerializer createTkSerializer();

  @Test
  void shouldSupportNewSessions() {
    doTest(newSession());
  }

  @Test
  void shouldSupportRestoredSession() {
    final Session session =
        new SpecCompleteSession(
            mock(ServletContext.class),
            notifier.observe(
                new RestoredTokenSession(randomUUID(), 1L, 2L, 3, singletonMap("foo", "Bar"))));
    doTest(session);
  }

  final void assertEquals(Session actual, Session expected) {
    assertThat(actual.getId()).isEqualTo(expected.getId());
    assertThat(actual.getCreationTime()).isEqualTo(expected.getCreationTime());
    assertThat(actual.getLastAccessedTime()).isEqualTo(expected.getLastAccessedTime());
    assertThat(actual.getMaxInactiveInterval()).isEqualTo(expected.getMaxInactiveInterval());
    assertThat(actual.attributes()).containsAllEntriesOf(expected.attributes());
  }

  private Session newSession() {
    return new SpecCompleteSession(mock(ServletContext.class), notifier.observe(new NewSession()));
  }

  private void doTest(Session expected) {
    assertEquals(writeThenRead(expected), expected);
  }

  private Session writeThenRead(Session session) {
    final TkSerializer serializer = createTkSerializer();
    return serializer.read(serializer.write(session));
  }
}
