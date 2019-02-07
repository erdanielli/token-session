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

import com.github.erdanielli.tksession.serializer.TkAesSerializer;
import com.github.erdanielli.tksession.serializer.TkBase64Serializer;
import com.github.erdanielli.tksession.serializer.TkJdkSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.fail;

@SuppressWarnings("ConstantConditions")
class TkSessionFilterTest {
    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private FakeNotifier notifier = new FakeNotifier();
    private TkSessionFilter filter =
            new TkSessionFilter(
                    notifier,
                    Duration.ofSeconds(1L),
                    "X-SESSION",
                    new TkBase64Serializer(new TkAesSerializer("secr3t", new TkJdkSerializer())));

    @Test
    @SuppressWarnings("squid:S2925")
    void shouldAllowSessionTimeoutOverride() throws InterruptedException {
        assertAfterRequest(
                (req, resp) -> {
                    req.getSession(false);
                    req.getSession(false);
                    req.getSession(true);
                    final HttpSession session = req.getSession(true);
                    session.setMaxInactiveInterval(2);
                })
                .isOk()
                .hasToken()
                .createdSessions(1)
                .destroyedSessions(0);
        sleep(1_010L);
        prepareNextRequestWithToken();
        assertAfterRequest(
                (req, resp) -> {
                    req.getSession(false);
                    req.getSession(true);
                    req.getSession(false);
                })
                .isOk()
                .hasToken()
                .createdSessions(1)
                .destroyedSessions(0);
        sleep(2_010L);
        prepareNextRequestWithToken();
        assertAfterRequest(
                (req, resp) -> {
                    req.getSession(false);
                    req.getSession(true);
                    req.getSession(false);
                })
                .isOk()
                .hasToken()
                .createdSessions(2)
                .destroyedSessions(1);
        sleep(1_010L);
        prepareNextRequestWithToken();
        assertAfterRequest((req, resp) -> req.getSession(false))
                .isOk()
                .doesNotHaveToken()
                .createdSessions(2)
                .destroyedSessions(2);
    }

    @Test
    @SuppressWarnings("squid:S2925")
    void shouldBypassTokenIfNoSessionIsRequired() throws InterruptedException {
        assertAfterRequest((req, resp) -> req.getSession(false))
                .isOk()
                .doesNotHaveToken()
                .createdSessions(0)
                .destroyedSessions(0);
        assertAfterRequest((req, resp) -> req.getSession())
                .isOk()
                .hasToken()
                .createdSessions(1)
                .destroyedSessions(0);
        sleep(1_010L);
        prepareNextRequestWithToken();
        assertAfterRequest((req, resp) -> req.getSession(true))
                .isOk()
                .hasToken()
                .createdSessions(2)
                .destroyedSessions(1);
        sleep(1_010L);
        prepareNextRequestWithToken();
        assertAfterRequest((req, resp) -> req.getSession(false))
                .isOk()
                .doesNotHaveToken()
                .createdSessions(2)
                .destroyedSessions(2);
    }

    @Test
    void shouldReturnBadRequestOnInvalidToken() {
        request.addHeader("X-SESSION", "ASDF");
        assertAfterRequest((req, resp) -> req.getSession(true))
                .isFailure(400, "INVALID_SESSION_TOKEN")
                .doesNotHaveToken()
                .createdSessions(0)
                .destroyedSessions(0);
    }

    @Test
    void shouldRethrowUnexpectedException() {
        Assertions.assertThatThrownBy(
                () ->
                        filter.doFilter(
                                request,
                                response,
                                new Endpoint() {
                                    @Override
                                    void execute(HttpServletRequest request, HttpServletResponse response)
                                            throws IOException {
                                        throw new IOException("Ops!");
                                    }
                                }))
                .isInstanceOf(IOException.class)
                .hasMessage("Ops!");
    }

    @Test
    void shouldAllowSessionUpdates() {
        assertAfterRequest(session -> session.setAttribute("count", 1))
                .isOk()
                .hasToken()
                .createdSessions(1)
                .destroyedSessions(0);
        prepareNextRequestWithToken();
        assertAfterRequest(
                new Endpoint() {
                    @Override
                    void execute(HttpServletRequest request, HttpServletResponse response) {
                        final HttpSession session = request.getSession();
                        session.setAttribute("count", (int) session.getAttribute("count") + 1);
                        session.setAttribute("name", "Foo");
                        session.setMaxInactiveInterval(1);
                        printAttributes(session, response);
                    }
                })
                .isOk()
                .hasToken()
                .hasBody("count=2,name=Foo")
                .createdSessions(1)
                .destroyedSessions(0);
    }

    private ResponseAssert assertAfterRequest(
            BiConsumer<HttpServletRequest, HttpServletResponse> endpoint) {
        return assertAfterRequest(
                new Endpoint() {
                    @Override
                    void execute(HttpServletRequest request, HttpServletResponse response) {
                        endpoint.accept(request, response);
                    }
                });
    }

    private ResponseAssert assertAfterRequest(Consumer<HttpSession> endpoint) {
        return assertAfterRequest(
                new Endpoint() {
                    @Override
                    void execute(HttpServletRequest request, HttpServletResponse response) {
                        endpoint.accept(request.getSession());
                    }
                });
    }

    private ResponseAssert assertAfterRequest(Endpoint endpoint) {
        try {
            filter.doFilter(request, response, endpoint);
        } catch (IOException | ServletException e) {
            fail("Should not throw exception", e);
        }
        return ResponseAssert.assertThat(notifier, response);
    }

    private void prepareNextRequestWithToken() {
        request = new MockHttpServletRequest();
        request.addHeader("X-SESSION", response.getHeader("X-SESSION"));
        response = new MockHttpServletResponse();
    }
}
