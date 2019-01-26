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

import com.github.erdanielli.tksession.Session;
import com.github.erdanielli.tksession.SessionWrapper;

/**
 * @author erdanielli
 */
final class ObservedSession extends SessionWrapper {
    private final SessionListenerNotifier notifier;

    ObservedSession(SessionListenerNotifier notifier, Session original) {
        super(original);
        this.notifier = notifier;
        if (isNew()) {
            this.notifier.sessionCreated(this);
        }
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (value == null) {
            removeAttribute(name);
        } else {
            final Object prev = getAttribute(name);
            super.setAttribute(name, value);
            if (prev == null) {
                notifier.attributeAdded(this, name, value);
            } else {
                notifier.attributeReplaced(this, name, prev, value);
            }
        }
    }

    @Override
    public void removeAttribute(String name) {
        final Object prevValue = getAttribute(name);
        super.removeAttribute(name);
        notifier.attributeRemoved(this, name, prevValue);
    }

    @Override
    public void invalidate() {
        notifier.sessionDestroyed(this);
    }
}
