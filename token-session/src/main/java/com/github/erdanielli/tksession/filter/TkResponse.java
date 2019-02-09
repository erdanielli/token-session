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

import com.github.erdanielli.tksession.serializer.TkBase64Serializer;

import javax.servlet.http.HttpServletResponse;

import static java.util.Optional.ofNullable;

/** @author erdanielli */
final class TkResponse extends OnCommittedResponseWrapper {
  private final String header;
  private final TkBase64Serializer serializer;
  private final TkRequest request;

  TkResponse(
      String header,
      TkBase64Serializer serializer,
      TkRequest request,
      HttpServletResponse response) {
    super(response);
    this.header = header;
    this.serializer = serializer;
    this.request = request;
  }

  @Override
  protected void onResponseCommitted() {
    ofNullable(request.getSession(false))
        .filter(s -> !s.expired())
        .map(serializer::writeToken)
        .ifPresent(tk -> setHeader(header, tk));
  }
}
