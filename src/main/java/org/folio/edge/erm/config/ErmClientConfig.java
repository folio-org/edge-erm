package org.folio.edge.erm.config;

import static org.folio.common.utils.tls.FeignClientTlsUtils.getSslOkHttpClient;

import feign.okhttp.OkHttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.Protocol;
import org.folio.edgecommonspring.client.EdgeFeignClientProperties;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;

@AllArgsConstructor
public class ErmClientConfig {
  private final EdgeFeignClientProperties properties;

  @Bean
  public okhttp3.OkHttpClient okHttpClient(okhttp3.OkHttpClient.Builder builder, ConnectionPool connectionPool,
      FeignHttpClientProperties httpClientProperties) {
    boolean followRedirects = httpClientProperties.isFollowRedirects();
    int connectTimeout = httpClientProperties.getConnectionTimeout();
    Duration readTimeout = httpClientProperties.getOkHttp().getReadTimeout();
    List<Protocol> protocols = httpClientProperties.getOkHttp().getProtocols().stream().map(Protocol::valueOf)
        .collect(Collectors.toList());

    builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        .followRedirects(followRedirects).readTimeout(readTimeout).connectionPool(connectionPool)
        .protocols(protocols).build();
    var tls = properties.getTls();
    return tls != null  && Boolean.TRUE.equals(tls.isEnabled()) ?
        getSslOkHttpClient(builder.build(), tls) : builder.build();
  }

  @Bean
  public OkHttpClient feignOkHttpClient(okhttp3.OkHttpClient okHttpClient) {
    return new OkHttpClient(okHttpClient);
  }
}
