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
import org.assertj.core.api.Assertions;
import org.assertj.core.api.MapAssert;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/** @author erdanielli */
final class ResponseAssert
    extends AbstractAssert<ResponseAssert, ResponseEntity<Map<String, Object>>> {

  static ResponseAssert assertThat(ResponseEntity<Map<String, Object>> actual) {
    return new ResponseAssert(actual);
  }

  ResponseAssert hasStatus(int expected) {
    if (actual.getStatusCodeValue() != expected) {
      failWithMessage("Expected HTTP status %d, got %d", expected, actual.getStatusCodeValue());
    }
    return this;
  }

  ResponseAssert hasHeader(String name) {
    final List<String> values = actual.getHeaders().get(name);
    if (values == null || values.isEmpty()) {
      failWithMessage("Missing HTTP header %s", name);
    }
    return this;
  }

  ResponseAssert doesNotHaveHeader(String name) {
    final List<String> values = actual.getHeaders().get(name);
    if (values != null && values.get(0) != null) {
      failWithMessage("Not expecting HTTP header %s", name);
    }
    return this;
  }

  MapAssert<String, Object> body() {
    return Assertions.assertThat(actual.getBody());
  }

  private ResponseAssert(ResponseEntity<Map<String, Object>> actual) {
    super(actual, ResponseAssert.class);
    isNotNull();
  }
}
