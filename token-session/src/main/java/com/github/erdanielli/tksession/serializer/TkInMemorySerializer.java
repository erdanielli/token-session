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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * @author erdanielli
 */
public abstract class TkInMemorySerializer implements TkSerializer {
    private static final int BUFFER_SIZE = 1024;

    @Override
    public final Session read(InputStream in) {
        return read(toByteArray(in));
    }

    protected abstract Session read(byte[] token);

    private byte[] toByteArray(InputStream in) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
            final byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected final byte[] forwardWrite(Session session, TkSerializer next) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        next.write(session, output);
        return output.toByteArray();
    }
}
