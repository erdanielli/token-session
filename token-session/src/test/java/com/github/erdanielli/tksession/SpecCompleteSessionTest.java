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
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import java.util.UUID;

import static com.github.erdanielli.tksession.SessionAssert.assertThat;
import static java.util.Collections.singletonMap;

/**
 * @author erdanielli
 */
class SpecCompleteSessionTest extends SessionSpec {

    private static final UUID ID = UUID.randomUUID();

    private final long tenSecondsAgo = System.currentTimeMillis() - 10_000L;

    private Session restored;

    @Override
    Session createSessionImplementation(ServletContext context) {
        restored = new SpecCompleteSession(context, new RestoredTokenSession(ID, tenSecondsAgo, tenSecondsAgo, 0,
                singletonMap("name", "Restored Session")));
        return new SpecCompleteSession(context, new NewSession());
    }

    @Test
    void shouldSupportNewSessions() {
        assertThat(session())
                .isNew()
                .wasLastAccessedAt(session().getCreationTime())
                .hasMaxInactiveInterval(0)
                .hasNoAttributes();
    }

    @Test
    void shouldSupportRestoredSessions() {
        assertThat(restored)
                .isNotNew()
                .wasCreatedAt(tenSecondsAgo)
                .wasLastAccessedAt(tenSecondsAgo)
                .hasAttributes("name", "Restored Session")
                .hasMaxInactiveInterval(0);
    }

    @Test
    void shouldExpireIfInactive() {
        restored.setMaxInactiveInterval(10);
        assertThat(restored).hasExpired();
        restored.setMaxInactiveInterval(15);
        assertThat(restored).hasNotExpired();
    }

    @Test
    void shouldNeverExpireWhenInactiveIntervalIsZero() {
        restored.setMaxInactiveInterval(0);
        assertThat(restored).hasNotExpired();
    }

    @Test
    void shouldAllowSomeMethodsAfterInvalidation() {
        session().invalidate();
        session().getId();
        session().setMaxInactiveInterval(10);
        session().getMaxInactiveInterval();
        session().getServletContext();
    }

    @Test
    void shouldThrowIllegalStateExceptionOnMostMethodsAfterInvalidation() {
        final Session session = session();
        session.invalidate();
        shouldThrow(session::getCreationTime, "#getCreationTime");
        shouldThrow(session::getLastAccessedTime, "#getLastAccessedTime");
        shouldThrow(() -> session.getAttribute("attr"), "#getAttribute(String)");
        shouldThrow(session::getAttributeNames, "#getAttributeNames");
        shouldThrow(() -> session.setAttribute("name", 1), "#setAttribute(String,Object)");
        shouldThrow(() -> session.removeAttribute("name"), "#removeAttribute(String)");
        shouldThrow(session::invalidate, "#invalidate");
        shouldThrow(session::isNew, "#isNew");
    }

    private void shouldThrow(ThrowableAssert.ThrowingCallable fn, String message) {
        Assertions.assertThatThrownBy(fn)
                .withFailMessage("session" + message + " should throw an IllegalStateException after invalidation")
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("session was invalidated");
    }
}