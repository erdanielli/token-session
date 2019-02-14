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

import com.github.erdanielli.tksession.Session;
import com.github.erdanielli.tksession.serializer.TkSerializer;
import com.github.erdanielli.tksession.serializer.TkSerializerSpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

import java.security.Principal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

/** @author erdanielli */
class SecurityContextSerializerTest extends TkSerializerSpec {

  private SecurityContextSerializer serializer = new SecurityContextSerializer();

  @Override
  protected TkSerializer createTkSerializer() {
    return serializer;
  }

  @Test
  void shouldSerializeOnlyPrincipalAndRoles() {
    final Session s = newSession();
    s.setAttribute(
        "SPRING_SECURITY_CONTEXT",
        new SecurityContextImpl(
            new UsernamePasswordAuthenticationToken(
                "John", "123456", createAuthorityList("USER", "ADMIN"))));

    final Session written = writeThenRead(s);
    final Authentication auth =
        ((SecurityContext) written.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication();
    assertThat(auth.isAuthenticated()).isTrue();
    assertThat(auth.getDetails()).isNull();
    assertThat(auth.getCredentials()).isNull();
    assertThat(((Principal) auth.getPrincipal()).getName()).isEqualTo("John");
    assertThat(auth.getName()).isEqualTo("John");
    assertThat(auth.getAuthorities().size()).isEqualTo(2);
  }

  @Test
  void shouldAllowEmptyAuthorities() {
    final Session s = newSession();
    s.setAttribute(
        "SPRING_SECURITY_CONTEXT",
        new SecurityContextImpl(
            new UsernamePasswordAuthenticationToken("John", "123456", Collections.emptyList())));

    final Session written = writeThenRead(s);
    final Authentication auth =
        ((SecurityContext) written.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication();

    assertThat(auth.isAuthenticated()).isTrue();
    assertThat(auth.getAuthorities()).isEmpty();
  }

  @Test
  void shouldAllowNullContext() {
    final Session s = newSession();
    s.setAttribute("age", 37);
    s.setAttribute("SPRING_SECURITY_CONTEXT", new SecurityContextImpl(null));

    final Session written = writeThenRead(s);
    assertThat(written.getAttribute("age")).isEqualTo(37);

    final Authentication auth =
        ((SecurityContext) written.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication();

    assertThat(auth).isNull();
  }
}
