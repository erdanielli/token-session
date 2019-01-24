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

import javax.servlet.ServletContext;
import java.time.Instant;
import java.util.UUID;

import static com.github.erdanielli.tksession.SessionAssert.assertThat;
import static java.util.Collections.singletonMap;

/**
 * @author erdanielli
 */
class RestoredTokenSessionTest extends IncompleteSessionSpec {
    private final long creationTime = Instant.now().minusSeconds(60).toEpochMilli();
    private final long lastAccessedTime = creationTime + 30_000;
    private final UUID uuid = UUID.randomUUID();

    @Override
    Session createSessionImplementation(ServletContext context) {
        return new RestoredTokenSession(context, uuid, creationTime, lastAccessedTime, 60,
                singletonMap("name", "Restored Session"));
    }

    @Test
    void shouldNotBeNew() {
        assertThat(session())
                .isNotNew()
                .hasAttributes("name", "Restored Session")
                .wasCreatedAt(creationTime)
                .wasLastAccessedAt(lastAccessedTime)
                .hasMaxInactiveInterval(60)
                .hasNotExpired();
    }

    @Test
    void shouldExpireIfInactive() {
        session().setMaxInactiveInterval(10);
        assertThat(session()).hasExpired();
    }

    @Test
    void shouldNeverExpireWhenInactiveIntervalIsZero() {
        session().setMaxInactiveInterval(0);
        assertThat(session()).hasNotExpired();
    }

}