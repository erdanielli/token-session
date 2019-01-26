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
package com.github.erdanielli.tksession.listener;

import com.github.erdanielli.tksession.NewSession;
import com.github.erdanielli.tksession.RestoredTokenSession;
import com.github.erdanielli.tksession.Session;
import org.junit.jupiter.api.Test;

import javax.servlet.http.*;
import java.util.Objects;
import java.util.UUID;

import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.*;

/**
 * @author erdanielli
 */
class SessionListenerNotifierTest {
    private HttpSessionListener httpSessionListener = mock(HttpSessionListener.class);
    private HttpSessionAttributeListener httpSessionAttributeListener = mock(HttpSessionAttributeListener.class);
    private SessionListenerNotifier notifier = new SessionListenerNotifierBuilder()
            .add(httpSessionListener)
            .add(httpSessionAttributeListener)
            .build();
    private HttpSessionBindingListener boundValue1 = mock(HttpSessionBindingListener.class);
    private HttpSessionBindingListener boundValue2 = mock(HttpSessionBindingListener.class);
    private HttpSessionBindingListener boundValue3 = mock(HttpSessionBindingListener.class);

    @Test
    void shouldNotifySessionCreatedOnlyIfNew() {
        notifier.observe(new RestoredTokenSession(UUID.randomUUID(), 0L, 0L, 1, emptyMap()));
        verify(httpSessionListener, times(0)).sessionCreated(any());
        final Session session = notifier.observe(new NewSession());
        verify(httpSessionListener).sessionCreated(eventExpected(session));
    }

    @Test
    void shouldNotifySessionDestroyedOnInvalidation() {
        final Session session = notifier.observe(new NewSession());
        session.invalidate();
        verify(httpSessionListener).sessionDestroyed(eventExpected(session));
    }

    @Test
    void shouldNotifyWhenAttributesChange() {
        final Session session = notifier.observe(new NewSession());

        session.setAttribute("attr", boundValue1);
        verify(httpSessionAttributeListener).attributeAdded(eventExpected(session, "attr", boundValue1));
        verify(boundValue1).valueBound(eventExpected(session, "attr", boundValue1));

        session.setAttribute("attr", boundValue2);
        verify(httpSessionAttributeListener).attributeReplaced(eventExpected(session, "attr", boundValue2));
        verify(boundValue1).valueUnbound(eventExpected(session, "attr", boundValue2));
        verify(boundValue2).valueBound(eventExpected(session, "attr", boundValue2));

        session.setAttribute("attr", "bar");
        verify(httpSessionAttributeListener).attributeReplaced(eventExpected(session, "attr", "bar"));
        verify(boundValue2).valueUnbound(eventExpected(session, "attr", "bar"));

        session.setAttribute("attr", null);
        verify(httpSessionAttributeListener).attributeRemoved(eventExpected(session, "attr", "bar"));

        session.removeAttribute("missing");
        verify(httpSessionAttributeListener).attributeRemoved(eventExpected(session, "missing", null));

        session.setAttribute("attr", boundValue3);
        verify(httpSessionAttributeListener).attributeAdded(eventExpected(session, "attr", boundValue3));
        verify(boundValue3).valueBound(eventExpected(session, "attr", boundValue3));

        session.removeAttribute("attr");
        verify(httpSessionAttributeListener).attributeRemoved(eventExpected(session, "attr", boundValue3));
        verify(boundValue3).valueUnbound(eventExpected(session, "attr", boundValue3));

        session.setAttribute("attr2", true);
        verify(httpSessionAttributeListener).attributeAdded(eventExpected(session, "attr2", true));

        session.setAttribute("attr2", false);
        verify(httpSessionAttributeListener).attributeReplaced(eventExpected(session, "attr2", false));

        verifyNoMoreInteractions(httpSessionAttributeListener, boundValue1);
    }

    private HttpSessionEvent eventExpected(Session session) {
        return argThat(actual -> actual.getSession() == session);
    }

    private HttpSessionBindingEvent eventExpected(Session session, String name, Object value) {
        return argThat(actual -> actual.getSession() == session
                && name.equals(actual.getName())
                && Objects.equals(value, actual.getValue()));
    }
}