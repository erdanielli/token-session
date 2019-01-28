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

import java.util.Enumeration;

/** @author erdanielli */
final class InvalidatedSession extends SessionWrapper {

  InvalidatedSession(Session original) {
    super(original);
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
