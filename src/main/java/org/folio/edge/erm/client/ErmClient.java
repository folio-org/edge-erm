package org.folio.edge.erm.client;

import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;

import org.folio.edge.erm.config.ErmClientConfig;
import java.net.URI;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "erm", configuration = ErmClientConfig.class)
public interface ErmClient {

  @GetMapping(value = "/erm/sas/publicLookup", consumes = MediaType.APPLICATION_JSON_VALUE)
  String getLicenseTerms(URI uri,
      @RequestParam String referenceId, 
      @RequestHeader(TENANT) String tenant,
      @RequestHeader(TOKEN) String token);

}
