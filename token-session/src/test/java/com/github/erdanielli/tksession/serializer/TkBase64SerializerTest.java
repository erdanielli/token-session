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
import com.github.erdanielli.tksession.Session;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author erdanielli
 */
class TkBase64SerializerTest extends TkSerializerSpec {
    private TkBase64Serializer serializer = new TkBase64Serializer(new TkJdkSerializer());

    @Override
    TkSerializer createTkSerializer() {
        return serializer;
    }

    @Test
    void shouldWriteAndReadString() throws IOException {
        final Session session = new NewSession();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.write(session, out);
        final String tokenString = serializer.writeToken(session);
        assertThat(tokenString).isEqualTo(new String(out.toByteArray(), ISO_8859_1));
        assertEquals(serializer.readToken(tokenString), session);
    }

    @Test
    void shouldFailOnInvalidToken() {
        Assertions.assertThatThrownBy(() -> serializer.readToken("ASDF"))
                .isInstanceOf(IOException.class);
    }
}