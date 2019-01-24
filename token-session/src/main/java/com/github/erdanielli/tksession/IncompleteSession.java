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

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

/**
 * @author erdanielli
 */
abstract class IncompleteSession extends Session {
    private final ServletContext sc;
    private final UUID uuid;
    private final long creationTime;
    private final Map<String, Object> attributes;
    private int maxInactiveInterval;

    IncompleteSession(ServletContext sc, UUID uuid, long creationTime, int maxInactiveInterval,
            Map<String, Object> attributes) {
        this.sc = sc;
        this.uuid = uuid;
        this.creationTime = creationTime;
        this.attributes = attributes;
        setMaxInactiveInterval(maxInactiveInterval);
    }

    @Override
    public final ServletContext getServletContext() {
        return sc;
    }

    @Override
    protected final UUID getUUID() {
        return uuid;
    }

    @Override
    public final long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public final void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = Math.max(0, interval);
    }

    @Override
    public final int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public final Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public final Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public final void setAttribute(String name, Object value) {
        if (value == null) {
            removeAttribute(name);
        } else {
            attributes.put(name, value);
        }
    }

    @Override
    public final void removeAttribute(String name) {
        attributes.remove(name);
    }
}
