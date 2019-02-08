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
package com.github.erdanielli.tksession.filter;

import com.github.erdanielli.tksession.listener.SessionListenerNotifier;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/** @author erdanielli */
final class FakeNotifier implements SessionListenerNotifier {
  private final List<String> created = new ArrayList<>();
  private final List<String> destroyed = new ArrayList<>();

  @Override
  public void sessionCreated(HttpSession s) {
    created.add(s.getId());
  }

  @Override
  public void sessionDestroyed(HttpSession s) {
    destroyed.add(s.getId());
  }

  int sessionsCreated() {
    return created.size();
  }

  int sessionsDestroyed() {
    return destroyed.size();
  }
}
