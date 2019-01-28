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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.List;
import java.util.function.BiConsumer;

/** @author erdanielli */
final class LifecycleNotifier implements SessionListenerNotifier {
  private final List<HttpSessionListener> listeners;

  static SessionListenerNotifier combine(List<HttpSessionListener> list) {
    return list.isEmpty() ? null : new LifecycleNotifier(list);
  }

  private LifecycleNotifier(List<HttpSessionListener> listeners) {
    this.listeners = listeners;
  }

  @Override
  public void sessionCreated(HttpSession s) {
    notify(new HttpSessionEvent(s), HttpSessionListener::sessionCreated);
  }

  @Override
  public void sessionDestroyed(HttpSession s) {
    notify(new HttpSessionEvent(s), HttpSessionListener::sessionDestroyed);
  }

  private void notify(
      HttpSessionEvent event, BiConsumer<HttpSessionListener, HttpSessionEvent> cn) {
    listeners.forEach(it -> cn.accept(it, event));
  }
}
