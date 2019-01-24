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
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import java.time.Instant;

import static com.github.erdanielli.tksession.SessionAssert.assertThat;

/**
 * @author erdanielli
 */
abstract class SessionSpec {

    ServletContext servletContext = Mockito.mock(ServletContext.class);

    Session session = createSessionImplementation();

    abstract Session createSessionImplementation();

    @Test
    void shouldHaveConsistentCreationAndLastAccessedTimes() {
        Assertions.assertThat(Instant.ofEpochMilli(session.getCreationTime()))
                .withFailMessage("Expected session's creation time to be in the past")
                .isBeforeOrEqualTo(Instant.now());

        Assertions.assertThat(Instant.ofEpochMilli(session.getLastAccessedTime()))
                .withFailMessage("Expected session's last accessed time to be after or equal its creation")
                .isAfterOrEqualTo(Instant.ofEpochMilli(session.getCreationTime()));
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
    void shouldHaveId() {
        Assertions.assertThat(session.getId())
                .withFailMessage("Expected session's id to be a valid UUID")
                .isEqualTo(session.getUUID().toString());
    }

}
