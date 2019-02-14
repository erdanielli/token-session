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
package com.github.erdanielli.tksession.serializer.security;

import com.github.erdanielli.tksession.serializer.TkJdkSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.Principal;

import static com.github.erdanielli.tksession.serializer.SuppressedExceptions.readObject;
import static java.util.Collections.emptyList;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

/** @author erdanielli */
public final class SecurityContextSerializer extends TkJdkSerializer {

  @Override
  protected void writeAttribute(ObjectOutput output, String name, Object value) throws IOException {
    if ("SPRING_SECURITY_CONTEXT".equals(name)) {
      output.writeObject(name);
      writeAuth(output, ((SecurityContext) value).getAuthentication());
    } else {
      super.writeAttribute(output, name, value);
    }
  }

  @Override
  protected Object readAttribute(ObjectInput input, String name) throws IOException {
    if ("SPRING_SECURITY_CONTEXT".equals(name)) {
      return new SecurityContextImpl(readAuth(input));
    }
    return super.readAttribute(input, name);
  }

  private void writeAuth(ObjectOutput output, Authentication authentication) throws IOException {
    if (authentication == null) {
      output.writeBoolean(true);
    } else {
      output.writeBoolean(false);
      output.writeObject(authentication.getName());
      final String authList =
          authentication.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .reduce((a, b) -> a + "," + b)
              .orElse(" ");
      output.writeObject(authList);
    }
  }

  private Authentication readAuth(ObjectInput input) throws IOException {
    if (input.readBoolean()) {
      return null;
    }
    final Principal principal = new PrincipalImpl(readObject(input));
    final String authList = ((String) readObject(input)).trim();
    return authList.isEmpty()
        ? new UsernamePasswordAuthenticationToken(principal, null, emptyList())
        : new UsernamePasswordAuthenticationToken(
            principal, null, commaSeparatedStringToAuthorityList(authList));
  }

  private class PrincipalImpl implements Principal {
    private final String username;

    PrincipalImpl(String username) {
      this.username = username;
    }

    @Override
    public String getName() {
      return username;
    }
  }
}
