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

import org.assertj.core.api.AbstractAssert;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author erdanielli
 */
@SuppressWarnings("UnusedReturnValue")
final class SessionAssert extends AbstractAssert<SessionAssert, Session> {

    static SessionAssert assertThat(Session actual) {
        return new SessionAssert(actual);
    }

    private SessionAssert(Session actual) {
        super(actual, SessionAssert.class);
        isNotNull();
    }

    SessionAssert isNew() {
        if (!actual.isNew()) {
            failWithMessage("Expected a new session");
        }
        if (actual.getUUID() == null) {
            failWithMessage("Required session's uuid is missing");
        }
        if (actual.getCreationTime() < 1L) {
            failWithMessage("Required session's creation time is missing");
        }
        if (actual.getLastAccessedTime() != actual.getCreationTime()) {
            failWithMessage("Expected session's last accessed time to be equals to it's creation time");
        }
        return hasMaxInactiveInterval(0);
    }

    SessionAssert hasNoAttributes() {
        if (!actual.attributes().isEmpty()) {
            failWithMessage("Expected session's attributes to be empty but was '%s'", actual.attributes());
        }
        return this;
    }

    SessionAssert hasAttributes(String name, Object value, Object... nameValuePairs) {
        Map<String, Object> expected = Collections.singletonMap(name, value);
        if (nameValuePairs.length != 0) {
            expected = new HashMap<>(expected);
            for (int i = 0; i < nameValuePairs.length; i += 2) {
                expected.put((String) nameValuePairs[i], nameValuePairs[i + 1]);
            }
        }
        final Map<String, Object> actualRemaining = new HashMap<>(actual.attributes());

        expected.forEach((k, expectedValue) -> {
            final Object actualValue = actualRemaining.remove(k);
            if (!Objects.equals(actualValue, expectedValue)) {
                failWithMessage("Expected session's attribute '%s' to be '%s' but was '%s'",
                        k, String.valueOf(expectedValue), String.valueOf(actualValue));
            }
        });

        if (!actualRemaining.isEmpty()) {
            failWithMessage("Unexpected session's attributes found: '%s'", actualRemaining);
        }
        return this;
    }

    SessionAssert hasMaxInactiveInterval(int expected) {
        if (actual.getMaxInactiveInterval() != expected) {
            failWithMessage("Expected session's max inactive interval to be %d but was %d", expected,
                    actual.getMaxInactiveInterval());
        }
        return this;
    }

    SessionAssert hasServletContext(ServletContext expected) {
        if (actual.getServletContext() != expected) {
            failWithMessage("Servlet context mismatch");
        }
        return this;
    }

    SessionAssert cantInvalidate() {
        try {
            actual.invalidate();
            failWithMessage("Session should not support invalidation");
        } catch (UnsupportedOperationException e) {
            // NOP
        }
        return this;
    }
}
