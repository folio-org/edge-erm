package org.folio.edge.erm.controller;

import org.folio.edge.erm.service.ErmService;
import org.folio.erm.domain.dto.LicenseTerms;
import org.folio.erm.domain.dto.LicenseTermsBatch;
import org.folio.erm.domain.dto.RequestIds;
import org.folio.erm.rest.resource.ErmApi;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Edge erm")
@Log4j2
@RestController
@RequiredArgsConstructor
public class ErmController implements ErmApi {

  private final ErmService ermService;

  @Override
  public ResponseEntity<LicenseTermsBatch> batchLicenseTerms(RequestIds requestIds, String tenant,
      String token) {
    var ids = requestIds.getIds();
    log.info("Retrieving licenses terms by ids {}", ids);
    return ResponseEntity.ok(ermService.batchLicenseTerms(ids, tenant, token));
  }

  @Override
  public ResponseEntity<LicenseTerms> getLicenseTerms(String id, String tenant, String token) {
    log.info("Retrieving license terms by id {}", id);
    return ResponseEntity.ok(ermService.getLicenseTerms(id, tenant, token));
  }
}
