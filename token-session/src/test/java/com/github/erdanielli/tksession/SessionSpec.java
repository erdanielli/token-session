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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletContext;

import static com.github.erdanielli.tksession.SessionAssert.assertThat;

/**
 * @author erdanielli
 */
abstract class SessionSpec {
    private ServletContext servletContext;
    private Session session;

    abstract Session createSessionImplementation(ServletContext context);

    @BeforeEach
    private void createSession() {
        servletContext = Mockito.mock(ServletContext.class);
        session = createSessionImplementation(servletContext);
    }

    final Session session() {
        return session;
    }

    @Test
    void shouldHaveContext() {
        assertThat(session).hasServletContext(servletContext);
    }

    @Test
    void shouldHaveConsistentCreationAndLastAccessedTimes() {
        assertThat(session).wasCreatedInThePast();
        assertThat(session).wasLastAccessedInThePast();
        Assertions.assertThat(session.getLastAccessedTime())
                .withFailMessage("Session's last created time should not be less that it's creation time")
                .isGreaterThanOrEqualTo(session.getCreationTime());
    }

    @Test
    void shouldAllowAttributes() {
        session.setAttribute("name", "John");
        session.setAttribute("lastName", null);
        session.setAttribute("age", 37);
        session.removeAttribute("name");
        session.setAttribute("male", true);
        assertThat(session).hasAttributes("age", 37, "male", true);
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

    @Test
    void shouldHaveIdCompatibleWithUUID() {
        assertThat(session).hasValidId();
    }

    @Test
    void shouldSupportDeprecatedApi() {
        session.putValue("v_name", "Deprecated");
        Assertions.assertThat(session.getValue("v_name"))
                .withFailMessage("session#getValue is broken")
                .isEqualTo("Deprecated");
        Assertions.assertThat(session.getValueNames())
                .withFailMessage("session#getValueNames is broken")
                .contains("v_name");
        session.removeValue("v_name");
        Assertions.assertThat(session.getValue("v_name"))
                .withFailMessage("session#removeValue is broken")
                .isNull();
        Assertions.assertThatThrownBy(() -> session.getSessionContext())
                .withFailMessage("session#getSessionContext should not be supported")
                .isInstanceOf(UnsupportedOperationException.class);
    }

}
