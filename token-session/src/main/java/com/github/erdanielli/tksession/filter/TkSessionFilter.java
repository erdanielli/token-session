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
import com.github.erdanielli.tksession.serializer.InvalidTokenException;
import com.github.erdanielli.tksession.serializer.TkBase64Serializer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/** @author erdanielli */
public final class TkSessionFilter implements Filter {
  private final SessionListenerNotifier notifier;
  private final Duration ttl;
  private final String header;
  private final TkBase64Serializer serializer;

  public TkSessionFilter(
      SessionListenerNotifier notifier,
      Duration ttl,
      String header,
      TkBase64Serializer serializer) {
    this.notifier = notifier;
    this.ttl = ttl;
    this.header = header;
    this.serializer = serializer;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    final HttpServletRequest httpReq = (HttpServletRequest) request;
    final HttpServletResponse httpResp = (HttpServletResponse) response;
    final String token = httpReq.getHeader(header);
    final TkRequest tkRequest =
        token == null
            ? new TkRequestWithoutHeader(ttl, notifier, httpReq)
            : new TkRequestWithHeader(token, serializer, ttl, notifier, httpReq);

    final TkResponse tkResponse = new TkResponse(header, serializer, tkRequest, httpResp);
    try {
      chain.doFilter(tkRequest, tkResponse);
    } catch (IOException | ServletException e) {
      if (e.getCause() instanceof InvalidTokenException) {
        tkResponse.sendError(400, "INVALID_SESSION_TOKEN");
      } else {
        throw e;
      }
    }
  }
}
