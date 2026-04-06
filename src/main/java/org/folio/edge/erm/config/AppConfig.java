package org.folio.edge.erm.config;

import java.util.concurrent.Executor;
import org.folio.edge.erm.config.property.ThreadPoolExecutorConfigs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfig {

  @Bean(name = "threadPoolTaskExecutor")
  public Executor threadPoolTaskExecutor(ThreadPoolExecutorConfigs executorConfigs) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(executorConfigs.getCorePoolSize());
    executor.setMaxPoolSize(executorConfigs.getMaxPoolSize());
    executor.setQueueCapacity(executorConfigs.getQueueCapacity());
    executor.setAllowCoreThreadTimeOut(true);
    executor.setKeepAliveSeconds(executorConfigs.getKeepAliveSeconds());
    executor.setThreadNamePrefix("TaskExecutor-");
    executor.initialize();
    return executor;
  }

}
