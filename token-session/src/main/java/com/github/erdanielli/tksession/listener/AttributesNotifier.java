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
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;
import java.util.function.BiConsumer;

/** @author erdanielli */
final class AttributesNotifier implements SessionListenerNotifier {
  private final List<HttpSessionAttributeListener> listeners;

  static SessionListenerNotifier combine(List<HttpSessionAttributeListener> list) {
    return list.isEmpty() ? null : new AttributesNotifier(list);
  }

  private AttributesNotifier(List<HttpSessionAttributeListener> listeners) {
    this.listeners = listeners;
  }

  @Override
  public void attributeAdded(HttpSession s, String attrName, Object value) {
    final HttpSessionBindingEvent event = new HttpSessionBindingEvent(s, attrName, value);
    notify(event, HttpSessionAttributeListener::attributeAdded);
    if (value instanceof HttpSessionBindingListener) {
      ((HttpSessionBindingListener) value).valueBound(event);
    }
  }

  @Override
  public void attributeRemoved(HttpSession s, String attrName, Object prevValue) {
    final HttpSessionBindingEvent event =
        notify(
            new HttpSessionBindingEvent(s, attrName, prevValue),
            HttpSessionAttributeListener::attributeRemoved);
    if (prevValue instanceof HttpSessionBindingListener) {
      ((HttpSessionBindingListener) prevValue).valueUnbound(event);
    }
  }

  @Override
  public void attributeReplaced(HttpSession s, String attrName, Object prevValue, Object newValue) {
    final HttpSessionBindingEvent event =
        notify(
            new HttpSessionBindingEvent(s, attrName, newValue),
            HttpSessionAttributeListener::attributeReplaced);
    if (prevValue instanceof HttpSessionBindingListener) {
      ((HttpSessionBindingListener) prevValue).valueUnbound(event);
    }
    if (newValue instanceof HttpSessionBindingListener) {
      ((HttpSessionBindingListener) newValue).valueBound(event);
    }
  }

  private HttpSessionBindingEvent notify(
      HttpSessionBindingEvent event,
      BiConsumer<HttpSessionAttributeListener, HttpSessionBindingEvent> cn) {
    listeners.forEach(it -> cn.accept(it, event));
    return event;
  }
}
