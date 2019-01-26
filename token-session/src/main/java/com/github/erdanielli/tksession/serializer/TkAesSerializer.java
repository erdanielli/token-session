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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

/**
 * @author erdanielli
 */
public final class TkAesSerializer extends TkInMemorySerializer {
    private final AesCrypto aes;
    private final TkSerializer original;

    public TkAesSerializer(String plainSecret, TkSerializer next) {
        this.original = next;
        this.aes = new AesCrypto(plainSecret);
    }

    @Override
    protected Session read(byte[] token) {
        return original.read(aes.decrypt(token));
    }

    @Override
    public void write(Session session, OutputStream out) {
        try {
            out.write(aes.encrypt(forwardWrite(session, original)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
