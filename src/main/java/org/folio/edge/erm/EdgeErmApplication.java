package org.folio.edge.erm;

import org.folio.spring.cql.JpaCqlConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, JpaCqlConfiguration.class})
@EnableFeignClients
public class EdgeErmApplication {

  public static void main(String[] args) {
    SpringApplication.run(EdgeErmApplication.class, args);
  }

}
