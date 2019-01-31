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

import com.github.erdanielli.tksession.Session;
import com.github.erdanielli.tksession.listener.SessionListenerNotifier;
import com.github.erdanielli.tksession.listener.SessionListenerNotifierBuilder;
import com.github.erdanielli.tksession.serializer.TkAesSerializer;
import com.github.erdanielli.tksession.serializer.TkBase64Serializer;
import com.github.erdanielli.tksession.serializer.TkJdkSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/** @author erdanielli */
@SpringBootApplication
@RestController
class TkSessionApp {

  @GetMapping("/session")
  Map<String, Object> session(HttpServletRequest request) {
    final HttpSession session = request.getSession();
    final Object count = session.getAttribute("count");
    if (count == null) {
      session.setAttribute("count", 1);
    } else {
      session.setAttribute("count", (int) count + 1);
    }
    request.getParameterMap().forEach((k, v) -> session.setAttribute(k, v[0]));
    return ((Session) session).attributes();
  }

  @GetMapping("/no_session")
  Map<String, Object> noSession(HttpServletRequest request) {
    return Optional.ofNullable(request.getSession(false))
        .map(s -> ((Session) s).attributes())
        .orElse(Collections.emptyMap());
  }

  @Bean
  FilterRegistrationBean<TkSessionFilter> tkSessionFilter(
      ObjectProvider<Collection<HttpSessionListener>> sessionListeners,
      ObjectProvider<Collection<HttpSessionAttributeListener>> attrListeners,
      @Value("${ttl:1s}") Duration ttl,
      @Value("${header:X-SESSION}") String header) {

    final TkSessionFilter tkSessionFilter =
        new TkSessionFilter(
            createNotifier(sessionListeners, attrListeners),
            ttl,
            header,
            new TkBase64Serializer(new TkAesSerializer("secr3t", new TkJdkSerializer())));

    final FilterRegistrationBean<TkSessionFilter> bean =
        new FilterRegistrationBean<>(tkSessionFilter);
    bean.setOrder(-1);
    bean.setName("tkSessionFilter");
    return bean;
  }

  private SessionListenerNotifier createNotifier(
      ObjectProvider<Collection<HttpSessionListener>> sessionListeners,
      ObjectProvider<Collection<HttpSessionAttributeListener>> attrListeners) {
    final SessionListenerNotifierBuilder builder = new SessionListenerNotifierBuilder();
    sessionListeners.ifAvailable(col -> col.forEach(builder::add));
    attrListeners.ifAvailable(col -> col.forEach(builder::add));
    return builder.build();
  }

  public static void main(String[] args) {
    SpringApplication.run(TkSessionApp.class, args);
  }
}
