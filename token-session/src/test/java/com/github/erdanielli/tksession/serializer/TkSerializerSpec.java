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
package com.github.erdanielli.tksession.serializer;

import com.github.erdanielli.tksession.NewSession;
import com.github.erdanielli.tksession.RestoredTokenSession;
import com.github.erdanielli.tksession.Session;
import org.junit.jupiter.api.Test;

import java.io.*;

import static com.github.erdanielli.tksession.serializer.BrokenStream.brokenInput;
import static com.github.erdanielli.tksession.serializer.BrokenStream.brokenOutput;
import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author erdanielli
 */
abstract class TkSerializerSpec {

    abstract TkSerializer createTkSerializer();

    @Test
    void shouldSupportNewSessions() {
        doTest(new NewSession());
    }

    @Test
    void shouldSupportRestoredSession() {
        doTest(new RestoredTokenSession(randomUUID(), 1L, 2L, 3, singletonMap("foo", "Bar")));
    }

    @Test
    void shouldRethrowUnexpectedExceptions() {
        final TkSerializer serializer = createTkSerializer();
        assertUncheckedIOExceptionOnRead(serializer::read);
        assertUncheckedIOExceptionOnWrite(output -> serializer.write(new NewSession(), output));
    }

    final void assertEquals(Session actual, Session expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getCreationTime()).isEqualTo(expected.getCreationTime());
        assertThat(actual.getLastAccessedTime()).isEqualTo(expected.getLastAccessedTime());
        assertThat(actual.getMaxInactiveInterval()).isEqualTo(expected.getMaxInactiveInterval());
        assertThat(actual.attributes()).containsAllEntriesOf(expected.attributes());
    }

    private void doTest(Session expected) {
        assertEquals(writeThenRead(expected), expected);
    }

    private Session writeThenRead(Session session) {
        final TkSerializer serializer = createTkSerializer();
        final Bytes bytes = new Bytes();
        serializer.write(session, bytes.output());
        return serializer.read(bytes.input());
    }

    final void assertUncheckedIOExceptionOnRead(CheckedConsumer<InputStream> action) {
        assertUncheckedIOException(brokenInput(), action);
    }

    final void assertUncheckedIOExceptionOnWrite(CheckedConsumer<OutputStream> action) {
        assertUncheckedIOException(brokenOutput(), action);
    }

    private <T> void assertUncheckedIOException(T value, CheckedConsumer<T> action) {
        try {
            action.accept(value);
        } catch (UncheckedIOException | IOException e) {
            // OK
        } catch (Exception e) {
            fail("should fail with some IOException, got '%s'", e.getClass().getName());
        }
    }

    private static class Bytes {
        ByteArrayOutputStream output;

        OutputStream output() {
            output = new ByteArrayOutputStream();
            return output;
        }

        InputStream input() {
            return new ByteArrayInputStream(output.toByteArray());
        }
    }

    @FunctionalInterface
    interface CheckedConsumer<T> {
        void accept(T value) throws Exception;
    }
}