package org.folio.edge.erm.config.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "thread-pool-executor.config")
public class ThreadPoolExecutorConfigs {

  private int corePoolSize;
  private int maxPoolSize;
  private int queueCapacity;
  private int keepAliveSeconds;

}
