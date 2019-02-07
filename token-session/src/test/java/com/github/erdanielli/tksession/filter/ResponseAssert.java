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

import org.assertj.core.api.AbstractAssert;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;

/** @author erdanielli */
@SuppressWarnings({ "SameParameterValue", "UnusedReturnValue" })
final class ResponseAssert extends AbstractAssert<ResponseAssert, MockHttpServletResponse> {

  private final FakeNotifier notifier;

  static ResponseAssert assertThat(FakeNotifier notifier, MockHttpServletResponse actual) {
    return new ResponseAssert(actual, notifier);
  }

  ResponseAssert isOk() {
    if (actual.getStatus() != 200) {
      failWithMessage("Expected HTTP status 200, got %d", actual.getStatus());
    }
    return this;
  }

  ResponseAssert isFailure(int status, String message) {
    if (actual.getStatus() != status) {
      failWithMessage("Expected HTTP status %d, got %d", status, actual.getStatus());
    } else if (!message.equals(actual.getErrorMessage())) {
      failWithMessage("Expected error message '%s', got '%s'", message, actual.getErrorMessage());
    }
    return this;
  }

  ResponseAssert hasToken() {
    final String tokenValue = (String) actual.getHeaderValue("X-SESSION");
    if (tokenValue == null) {
      failWithMessage("Missing token (HTTP header X-SESSION)");
    }
    return this;
  }

  ResponseAssert doesNotHaveToken() {
    final String tokenValue = (String) actual.getHeaderValue("X-SESSION");
    if (tokenValue != null) {
      failWithMessage("Not expecting token (HTTP header X-SESSION), got '%s'", tokenValue);
    }
    return this;
  }

  ResponseAssert hasBody(String expected) {
    try {
      if (!expected.equals(actual.getContentAsString())) {
        failWithMessage(
                "Expected response body '%s', got '%s'", expected, actual.getContentAsString());
      }
    } catch (UnsupportedEncodingException e) {
      failWithMessage("Could not retrieve response body");
    }
    return this;
  }

  ResponseAssert createdSessions(int exceptedAmount) {
    if (notifier.sessionsCreated() != exceptedAmount) {
      failWithMessage(
              "Expected notifier to notify %dx session creation, got %d",
              exceptedAmount, notifier.sessionsCreated());
    }
    return this;
  }

  ResponseAssert destroyedSessions(int exceptedAmount) {
    if (notifier.sessionsDestroyed() != exceptedAmount) {
      failWithMessage(
              "Expected notifier to notify %dx session destruction, got %d",
              exceptedAmount, notifier.sessionsDestroyed());
    }
    return this;
  }

  private ResponseAssert(MockHttpServletResponse actual, FakeNotifier notifier) {
    super(actual, ResponseAssert.class);
    this.notifier = notifier;
  }
}
