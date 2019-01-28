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

/** @author erdanielli */
public final class SpecCompleteSession extends Session {
  private final ServletContext sc;
  private Session incomplete;
  private boolean invalidated;

  public SpecCompleteSession(ServletContext sc, Session incomplete) {
    this.sc = sc;
    this.incomplete = incomplete;
  }

  // remaining spec impl

  @Override
  public ServletContext getServletContext() {
    return sc;
  }

  @Override
  public void invalidate() {
    if (!invalidated) {
      incomplete.invalidate();
      incomplete = new InvalidatedSession(incomplete);
      invalidated = true;
    } else {
      incomplete.invalidate();
    }
  }

  // already implemented

  @Override
  public UUID getUUID() {
    return incomplete.getUUID();
  }

  @Override
  public long getCreationTime() {
    return incomplete.getCreationTime();
  }

  @Override
  public long getLastAccessedTime() {
    return incomplete.getLastAccessedTime();
  }

  @Override
  public void setMaxInactiveInterval(int interval) {
    incomplete.setMaxInactiveInterval(interval);
  }

  @Override
  public int getMaxInactiveInterval() {
    return incomplete.getMaxInactiveInterval();
  }

  @Override
  public Object getAttribute(String name) {
    return incomplete.getAttribute(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return incomplete.getAttributeNames();
  }

  @Override
  @SuppressWarnings("squid:S2441")
  public void setAttribute(String name, Object value) {
    incomplete.setAttribute(name, value);
  }

  @Override
  public void removeAttribute(String name) {
    incomplete.removeAttribute(name);
  }

  @Override
  public boolean isNew() {
    return incomplete.isNew();
  }
}
