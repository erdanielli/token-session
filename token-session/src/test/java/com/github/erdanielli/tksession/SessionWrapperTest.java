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

import com.github.erdanielli.tksession.listener.SessionListenerNotifier;
import com.github.erdanielli.tksession.listener.SessionListenerNotifierBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletContext;

import static com.github.erdanielli.tksession.SessionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** @author erdanielli */
class SessionWrapperTest {

  @Test
  void shouldDelegateAllMethods() {
    final SessionListenerNotifier notifier = new SessionListenerNotifierBuilder().build();
    final ServletContext sc = Mockito.mock(ServletContext.class);
    final Session session = new SpecCompleteSession(sc, notifier.observe(new NewSession()));
    final Session wrapper = new SessionWrapper(session) {};

    assertThat(wrapper).hasServletContext(sc);
    wrapper.invalidate();
    assertThatThrownBy(wrapper::invalidate).isInstanceOf(IllegalStateException.class);
  }
}
