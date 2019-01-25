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

import com.github.erdanielli.tksession.Session;

import java.io.*;
import java.util.Base64;

import static java.util.Base64.getEncoder;

/**
 * @author erdanielli
 */
public final class TkBase64Serializer extends TkInMemorySerializer {
    private final TkSerializer next;

    public TkBase64Serializer(TkSerializer next) {
        this.next = next;
    }

    /**
     * First decode base64 then forwards to the next serializer.
     *
     * @param base64Token Header value from request
     * @return A restored session
     * @throws IOException if the token is invalid
     */
    public Session readToken(String base64Token) throws IOException {
        try (final InputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(base64Token))) {
            return next.read(input);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Writes at the end of the chain, encoding the incoming bytes in base64 format
     *
     * @param session a required session
     * @return a base64 encoded token representation of the session
     */
    public String writeToken(Session session) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        next.write(session, output);
        return getEncoder().encodeToString(output.toByteArray());
    }

    @Override
    protected Session read(byte[] token) {
        try (final InputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(token))) {
            return next.read(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes at the end of the chain, encoding the bytes in base64 format
     *
     * @param session a required session
     * @param out     a required target output
     */
    @Override
    public void write(Session session, OutputStream out) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        next.write(session, baos);
        try {
            out.write(getEncoder().encode(baos.toByteArray()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
