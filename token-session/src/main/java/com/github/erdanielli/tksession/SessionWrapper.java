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
@SuppressWarnings("DesignForExtensionCheck")
public abstract class SessionWrapper extends Session {
  private final Session original;

  protected SessionWrapper(Session original) {
    this.original = original;
  }

  @Override
  public UUID getUUID() {
    return original.getUUID();
  }

  @Override
  public long getCreationTime() {
    return original.getCreationTime();
  }

  @Override
  public long getLastAccessedTime() {
    return original.getLastAccessedTime();
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

  @Override
  public Object getAttribute(String name) {
    return original.getAttribute(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return original.getAttributeNames();
  }

  @Override
  @SuppressWarnings("squid:S2441")
  public void setAttribute(String name, Object value) {
    original.setAttribute(name, value);
  }

  @Override
  public void removeAttribute(String name) {
    original.removeAttribute(name);
  }

  @Override
  public void invalidate() {
    original.invalidate();
  }

  @Override
  public boolean isNew() {
    return original.isNew();
  }
}
