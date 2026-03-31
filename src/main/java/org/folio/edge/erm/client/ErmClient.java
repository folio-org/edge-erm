package org.folio.edge.erm.client;

import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tools.jackson.databind.JsonNode;

@HttpExchange(contentType = "application/json")
public interface ErmClient {

  @GetExchange("erm/sas/publicLookup")
  JsonNode getLicenseTerms(@RequestParam("referenceId") String referenceId,
      @RequestHeader(TENANT) String tenant,
      @RequestHeader(TOKEN) String token);

}
