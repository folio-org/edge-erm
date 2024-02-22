package org.folio.edge.erm.config;

import org.folio.edge.erm.config.property.ThreadPoolExecutorConfigs;
import org.folio.edge.erm.mapper.LicenseTermItemValueDeserializer;
import org.folio.edge.erm.mapper.LicenseTermItemValueSerializer;
import org.folio.erm.domain.dto.LicenseTermItemValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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

  @Bean
  @Primary
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    SimpleModule module = new SimpleModule();
    module.addSerializer(LicenseTermItemValue.class, new LicenseTermItemValueSerializer());
    module.addDeserializer(LicenseTermItemValue.class, new LicenseTermItemValueDeserializer());
    return builder.build().registerModule(module);
  }

}
