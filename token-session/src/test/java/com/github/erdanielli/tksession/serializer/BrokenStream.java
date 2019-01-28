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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** @author erdanielli */
final class BrokenStream {

  private BrokenStream() {}

  static InputStream brokenInput() {
    return new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("BROKEN");
      }
    };
  }

  static OutputStream brokenOutput() {
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        throw new IOException("BROKEN");
      }
    };
  }
}
