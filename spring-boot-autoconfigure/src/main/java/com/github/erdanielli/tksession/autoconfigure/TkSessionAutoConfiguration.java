package com.github.erdanielli.tksession.autoconfigure;

import com.github.erdanielli.tksession.filter.TkSessionFilter;
import com.github.erdanielli.tksession.listener.SessionListenerNotifier;
import com.github.erdanielli.tksession.listener.SessionListenerNotifierBuilder;
import com.github.erdanielli.tksession.serializer.TkAesSerializer;
import com.github.erdanielli.tksession.serializer.TkBase64Serializer;
import com.github.erdanielli.tksession.serializer.TkJdkSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;
import java.util.Collection;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

@Configuration
@ConditionalOnWebApplication(type = SERVLET)
public class TkSessionAutoConfiguration {

  @Bean
  SessionListenerNotifier sessionListenerNotifier(
      ObjectProvider<Collection<HttpSessionListener>> sessionListeners,
      ObjectProvider<Collection<HttpSessionAttributeListener>> sessionAttrListeners) {
    final SessionListenerNotifierBuilder builder = new SessionListenerNotifierBuilder();
    sessionListeners.ifAvailable(col -> col.forEach(builder::add));
    sessionAttrListeners.ifAvailable(col -> col.forEach(builder::add));
    return builder.build();
  }

  @Bean
  TkBase64Serializer tkBase64Serializer(@Value("${tksession.secret}") String secret) {
    return new TkBase64Serializer(new TkAesSerializer(secret, new TkJdkSerializer()));
  }

  @Bean
  FilterRegistrationBean<TkSessionFilter> tkSessionFilter(
      SessionListenerNotifier sessionListenerNotifier,
      ServerProperties serverProperties,
      @Value("${tksession.header:X-SESSION}") String header,
      TkBase64Serializer tkBase64Serializer) {
    final FilterRegistrationBean<TkSessionFilter> bean =
        new FilterRegistrationBean<>(
            new TkSessionFilter(
                sessionListenerNotifier,
                serverProperties.getServlet().getSession().getTimeout(),
                header,
                tkBase64Serializer));
    bean.setName("tkSessionFilter");
    bean.setOrder(Ordered.LOWEST_PRECEDENCE);
    return bean;
  }
}
