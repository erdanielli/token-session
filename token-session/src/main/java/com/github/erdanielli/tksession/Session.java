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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This base class avoid exposure to deprecated API.
 *
 * @author erdanielli
 */
@SuppressWarnings("deprecation")
public abstract class Session implements HttpSession {

  public abstract UUID getUUID();

  public final Map<String, Object> attributes() {
    return Collections.unmodifiableMap(
        Collections.list(getAttributeNames()).stream()
            .collect(Collectors.toMap(k -> k, this::getAttribute)));
  }

  public final boolean expired() {
    if (getMaxInactiveInterval() == 0) {
      return false;
    }
    return System.currentTimeMillis() >= (getLastAccessedTime() + getMaxInactiveInterval() * 1_000);
  }

  @Override
  public final String getId() {
    return getUUID().toString();
  }

  // deprecated methods

  @SuppressWarnings("squid:CallToDeprecatedMethod")
  public final HttpSessionContext getSessionContext() {
    throw new UnsupportedOperationException();
  }

  public final Object getValue(String name) {
    return getAttribute(name);
  }

  public final String[] getValueNames() {
    return Collections.list(getAttributeNames()).toArray(new String[0]);
  }

  @SuppressWarnings("squid:S2441")
  public final void putValue(String name, Object value) {
    setAttribute(name, value);
  }

  public final void removeValue(String name) {
    removeAttribute(name);
  }
}
