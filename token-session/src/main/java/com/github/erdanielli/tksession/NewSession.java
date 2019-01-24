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
import java.util.HashMap;
import java.util.UUID;

/**
 * @author erdanielli
 */
final class NewSession extends IncompleteSession {

    NewSession(ServletContext sc) {
        super(sc, UUID.randomUUID(), System.currentTimeMillis(), new HashMap<>());
    }

    @Override
    public long getLastAccessedTime() {
        return getCreationTime();
    }

    @Override
    public boolean isNew() {
        return true;
    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException();
    }
}
