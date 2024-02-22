package org.folio.edge.erm.config;

import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;

public class ErmClientConfig {

  @Bean
  public OkHttpClient feignOkHttpClient(okhttp3.OkHttpClient okHttpClient) {
    return new OkHttpClient(okHttpClient);
  }
}
