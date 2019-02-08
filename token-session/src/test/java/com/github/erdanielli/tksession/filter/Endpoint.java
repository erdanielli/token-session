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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.Map;

/** @author erdanielli */
abstract class Endpoint implements FilterChain {

  @Override
  public final void doFilter(ServletRequest request, ServletResponse response)
      throws IOException, ServletException {
    try {
      execute((HttpServletRequest) request, (HttpServletResponse) response);
      response.flushBuffer();
    } catch (RuntimeException e) {
      throw new ServletException(e);
    }
  }

  abstract void execute(HttpServletRequest request, HttpServletResponse response)
      throws IOException;

  final void printAttributes(HttpSession session, HttpServletResponse response) {
    ((Session) session)
        .attributes().entrySet().stream()
            .filter(e -> e.getValue() != null)
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .map(e -> e.getKey() + "=" + e.getValue())
            .reduce((a, b) -> a + "," + b)
            .ifPresent(
                line -> {
                  try {
                    response.getWriter().print(line);
                  } catch (IOException e) {
                    throw new UncheckedIOException(e);
                  }
                });
  }
}
