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
package com.github.erdaniell.tksession.examples.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Optional.ofNullable;

/** @author erdanielli */
@RestController
class SessionController {

  @GetMapping("/session/{create}")
  Map<String, Object> print(@PathVariable boolean create, HttpServletRequest req) {
    final HttpSession session = req.getSession(create);
    if (session == null) {
      return Collections.emptyMap();
    }
    ofNullable(req.getParameter("ttl"))
        .ifPresent(str -> session.setMaxInactiveInterval(Integer.parseInt(str)));

    return flatSession(session);
  }

  @PostMapping("/session/{create}")
  Map<String, Object> setAttributes(@PathVariable boolean create, HttpServletRequest req) {
    final HttpSession session = req.getSession(create);
    if (session == null) {
      return Collections.emptyMap();
    }
    req.getParameterMap().forEach((k, v) -> session.setAttribute(k, v[0]));
    return flatSession(session);
  }

  @GetMapping("/session/invalidate")
  Map<String, Object> invalidate(HttpServletRequest req) {
    final HttpSession session = req.getSession(false);
    if (session == null) {
      return Collections.emptyMap();
    }
    session.invalidate();
    return Collections.singletonMap("invalidated-session-id", session.getId());
  }

  private Map<String, Object> flatSession(HttpSession session) {
    final Map<String, Object> map = new TreeMap<>();
    map.put("id", session.getId());
    map.put("creation-time", Instant.ofEpochMilli(session.getCreationTime()));
    map.put("last-accessed-time", Instant.ofEpochMilli(session.getLastAccessedTime()));
    map.put("max-inactive-interval", session.getMaxInactiveInterval());

    final Enumeration<String> attributes = session.getAttributeNames();
    if (!attributes.hasMoreElements()) {
      map.put("attributes", Collections.emptyMap());
    } else {
      final Map<String, Object> attrMap = new TreeMap<>();
      do {
        final String name = attributes.nextElement();
        attrMap.put(name, session.getAttribute(name));
      } while (attributes.hasMoreElements());
      map.put("attributes", attrMap);
    }
    return map;
  }
}
