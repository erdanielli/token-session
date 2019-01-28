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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import java.util.UUID;

import static com.github.erdanielli.tksession.SessionAssert.assertThat;
import static java.util.Collections.emptyMap;

/** @author erdanielli */
class SpecCompleteSession_ExpirationTest {
  private long tenSecondsAgo = System.currentTimeMillis() - 10_000L;
  private Session session =
      new SpecCompleteSession(
          Mockito.mock(ServletContext.class),
          new RestoredTokenSession(UUID.randomUUID(), tenSecondsAgo, tenSecondsAgo, 0, emptyMap()));

  @Test
  void shouldExpireIfInactive() {
    session.setMaxInactiveInterval(10);
    assertThat(session).hasExpired();
    session.setMaxInactiveInterval(15);
    assertThat(session).hasNotExpired();
  }

  @Test
  void shouldNeverExpireWhenInactiveIntervalIsZero() {
    assertThat(session).hasNotExpired();
  }

  @Test
  void shouldAllowChangesToMaxInactiveInterval() {
    session.setMaxInactiveInterval(10);
    assertThat(session).hasMaxInactiveInterval(10);
  }

  @Test
  void shouldSetZeroOnNegativeMaxInactiveInterval() {
    session.setMaxInactiveInterval(-1);
    assertThat(session).hasMaxInactiveInterval(0);
  }
}
