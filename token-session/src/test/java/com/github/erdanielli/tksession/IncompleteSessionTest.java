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

import java.util.UUID;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author erdanielli
 */
class IncompleteSessionTest {
    private Session newSession = new NewSession();
    private Session restoredTokenSession = new RestoredTokenSession(UUID.randomUUID(), 0L, 0L, 0, emptyMap());
    private Session invalidatedSession = new InvalidatedSession(newSession);

    @Test
    void shouldNotSupportInvalidate() {
        shouldNotSupport(newSession::invalidate, "NewSession#invalidate");
        shouldNotSupport(restoredTokenSession::invalidate, "RestoredTokenSessionn#invalidate");
    }

    @Test
    void shouldNotProvideServletContext() {
        shouldNotSupport(newSession::getServletContext, "NewSession#getServletContext");
        shouldNotSupport(restoredTokenSession::getServletContext, "RestoredTokenSession#getServletContext");
        shouldNotSupport(invalidatedSession::getServletContext, "InvalidatedSession#getServletContext");
    }

    private void shouldNotSupport(Runnable fn, String method) {
        try {
            fn.run();
            fail(method + " should not be supported");
        } catch (UnsupportedOperationException e) {
            // SUCCESS
        }
    }
}
