package org.folio.edge.erm.config;

import lombok.RequiredArgsConstructor;
import org.folio.edge.erm.client.ErmClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class HttpClientConfiguration {

  @Qualifier("edgeHttpServiceProxyFactory")
  private final HttpServiceProxyFactory httpServiceProxyFactory;

  @Bean
  public ErmClient ermClient() {
    return httpServiceProxyFactory.createClient(ErmClient.class);
  }
}

