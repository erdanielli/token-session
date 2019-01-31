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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureWebTestClient
@SuppressWarnings("squid:S1192")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class TkSessionFilterIT {
  @Autowired TestRestTemplate client;
  private String token;

  @Test
  @SuppressWarnings("squid:S2925")
  void shouldBypassTokenIfNoSessionIsRequired() {
    token = null;
    getAndAssert("/no_session").hasStatus(200).doesNotHaveHeader("X_SESSION");
  }

  @Test
  void shouldReturnBadRequestOnInvalidToken() {
    token = "ASDF";
    getAndAssert("/session").hasStatus(400);
  }

  @Test
  @SuppressWarnings("squid:S2925")
  void shouldAllowSessionUpdatesAndTimeout() throws InterruptedException {
    token = null;
    createSession();
    updateSession();
    Thread.sleep(1200L);
    timeoutSession();
  }

  private void createSession() {
    getAndAssert("/session")
        .hasStatus(200)
        .hasHeader("X-SESSION")
        .body()
        .containsOnlyKeys("count")
        .containsEntry("count", 1);
  }

  private void updateSession() {
    getAndAssert("/session?name=foo")
        .hasStatus(200)
        .hasHeader("X-SESSION")
        .body()
        .containsOnlyKeys("count", "name")
        .containsEntry("count", 2)
        .containsEntry("name", "foo");
  }

  private void timeoutSession() {
    getAndAssert("/session")
        .hasStatus(200)
        .hasHeader("X-SESSION")
        .body()
        .containsOnlyKeys("count")
        .containsEntry("count", 1);
  }

  private ResponseAssert getAndAssert(String uri) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    if (token != null) {
      httpHeaders.add("X-SESSION", token);
    }
    final ResponseEntity<Map<String, Object>> resp =
        client.exchange(
            uri,
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            new ParameterizedTypeReference<Map<String, Object>>() {});

    final List<String> respToken = resp.getHeaders().get("X-SESSION");
    token = (respToken == null || respToken.isEmpty()) ? null : respToken.get(0);

    return ResponseAssert.assertThat(resp);
  }
}
