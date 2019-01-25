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
import java.util.Enumeration;
import java.util.UUID;

/**
 * @author erdanielli
 */
final class InvalidatedSession extends Session {
    private final Session original;

    InvalidatedSession(Session original) {
        this.original = original;
    }

    // methods allowed after invalidation

    @Override
    UUID getUUID() {
        return original.getUUID();
    }

    @Override
    public ServletContext getServletContext() {
        return original.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        original.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return original.getMaxInactiveInterval();
    }

    // methods not allowed after invalidation

    @Override
    public long getCreationTime() {
        throw invalidatedException();
    }

    @Override
    public long getLastAccessedTime() {
        throw invalidatedException();
    }

    @Override
    public Object getAttribute(String name) {
        throw invalidatedException();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw invalidatedException();
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw invalidatedException();
    }

    @Override
    public void removeAttribute(String name) {
        throw invalidatedException();
    }

    @Override
    public void invalidate() {
        throw invalidatedException();
    }

    @Override
    public boolean isNew() {
        throw invalidatedException();
    }

    private IllegalStateException invalidatedException() {
        return new IllegalStateException("session was invalidated");
    }

}
