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

/**
 * @author erdanielli
 */
class SpecCompleteSession_BasicTest {
    private ServletContext sc = mock(ServletContext.class);
    private UUID uuid = UUID.randomUUID();
    private long tenSecondsAgo = System.currentTimeMillis() - 10_000L;
    private Session session = new SpecCompleteSession(sc,
            new RestoredTokenSession(uuid, tenSecondsAgo, tenSecondsAgo, 1, singletonMap("foo", "Bar")));
    private Session newSession = new SpecCompleteSession(sc, new NewSession());

    @Test
    void shouldSupportNewSessions() {
        assertThat(newSession)
                .isNew()
                .wasLastAccessedAt(newSession.getCreationTime())
                .hasMaxInactiveInterval(0)
                .hasNoAttributes()
                .hasServletContext(sc);
    }

    @Test
    void shouldSupportRestoredSessions() {
        assertThat(session)
                .isNotNew()
                .wasCreatedAt(tenSecondsAgo)
                .wasLastAccessedAt(tenSecondsAgo)
                .hasMaxInactiveInterval(1)
                .hasAttributes("foo", "Bar")
                .hasServletContext(sc);
    }

    @Test
    void shouldHaveIdCompatibleWithUUID() {
        assertThat(newSession).hasValidId();
        assertThat(session).hasValidId();
        Assertions.assertThat(session.getId()).isEqualTo(uuid.toString());
    }

    @Test
    void shouldNotSupportDeprecatedApi() {
        Assertions.assertThatThrownBy(() -> session.getSessionContext())
                .withFailMessage("session#getSessionContext should not be supported")
                .isInstanceOf(UnsupportedOperationException.class);
    }

}