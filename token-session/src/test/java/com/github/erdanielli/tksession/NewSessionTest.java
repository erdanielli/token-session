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

import static com.github.erdanielli.tksession.SessionAssert.assertThat;

/**
 * @author erdanielli
 */
class NewSessionTest extends IncompleteSessionSpec {

    @Override
    Session createSessionImplementation(ServletContext context) {
        return new NewSession(context);
    }

    @Test
    void shouldBeNewAndEmpty() {
        assertThat(session())
                .isNew()
                .wasLastAccessedAt(session().getCreationTime())
                .hasMaxInactiveInterval(0)
                .hasNoAttributes()
                .hasNotExpired();
    }

}