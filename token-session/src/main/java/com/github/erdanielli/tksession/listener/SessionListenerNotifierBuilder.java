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

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;

/** @author erdanielli */
public final class SessionListenerNotifierBuilder {
  private final List<HttpSessionListener> sListeners = new ArrayList<>();
  private final List<HttpSessionAttributeListener> attrListeners = new ArrayList<>();

  public SessionListenerNotifierBuilder add(HttpSessionListener listener) {
    return addTo(sListeners, listener);
  }

  public SessionListenerNotifierBuilder add(HttpSessionAttributeListener listener) {
    return addTo(attrListeners, listener);
  }

  public SessionListenerNotifier build() {
    return AllNotifiers.combine(
        LifecycleNotifier.combine(sListeners), AttributesNotifier.combine(attrListeners));
  }

  private <T> SessionListenerNotifierBuilder addTo(List<T> destination, T listener) {
    destination.add(listener);
    return this;
  }
}
