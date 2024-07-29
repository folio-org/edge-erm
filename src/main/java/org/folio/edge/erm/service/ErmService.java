package org.folio.edge.erm.service;

import static org.apache.maven.shared.utils.StringUtils.isBlank;

import org.folio.edge.erm.client.ErmClient;
import org.folio.edgecommonspring.client.EdgeFeignClientProperties;
import org.folio.erm.domain.dto.LicenseTerms;
import org.folio.erm.domain.dto.LicenseTermsBatch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Log4j2
public class ErmService {

  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String DESCRIPTION = "description";
  private static final String LABEL = "label";
  private static final String VISIBILITY = "visibility";
  private static final String VALUE = "value";
  private static final String PRIMARY = "primary";
  private static final String PUBLIC_NOTE = "publicNote";
  private static final String WEIGHT = "weight";
  private static final String IDS_ARE_NOT_PROVIDED = "Ids to retrieve are not provided";
  private static final String FAILED_TO_MAP_RESPONSE = "Failed to map licenses response";
  private final ErmClient ermClient;
  private final ObjectMapper objectMapper;
  private final EdgeFeignClientProperties properties;

  /**
   * @deprecated
   */
  @Deprecated(since = "1.2.0", forRemoval = false)
  @Value("${okapi_url:NO_VALUE}")
  private String okapiUrl;

  @SneakyThrows
  public LicenseTermsBatch batchLicenseTerms(List<String> ids, String tenant, String token) {
    validateBatchRequest(ids);
    var responses = ids.stream()
        .map(id -> fetchLicenseTermsAsync(id, tenant, token))
        .map(CompletableFuture::join)
        .collect(Collectors.toList());

    var resultsNode = objectMapper.createObjectNode();
    resultsNode.set("results", objectMapper.valueToTree(responses));
    return objectMapper.treeToValue(resultsNode, LicenseTermsBatch.class);
  }

  @SneakyThrows
  public LicenseTerms getLicenseTerms(String id, String tenant, String token) {
    var responseNode = getLicenceTermByIdResponseNode(id, tenant, token);
    var licenseTerms = objectMapper.treeToValue(responseNode, LicenseTerms.class);
    if (licenseTerms.getLicenseTerms().isEmpty()) {
      log.error("License terms by id {} are not found", id);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          String.format("License terms by id %s are not found", id));
    }
    return objectMapper.treeToValue(responseNode, LicenseTerms.class);
  }

  private void validateBatchRequest(List<String> ids) {
    if (ids.isEmpty()) {
      log.error(IDS_ARE_NOT_PROVIDED);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, IDS_ARE_NOT_PROVIDED);
    }
  }

  private ObjectNode getLicenceTermByIdResponseNode(String id, String tenant, String token) {
    var okapiUrlToUse = properties.getOkapiUrl();
    if (isBlank(okapiUrlToUse)) {
      log.warn("deprecated property okapi_url is used. Please use folio.client.okapiUrl instead.");
      okapiUrlToUse = okapiUrl;
    }
    var response = ermClient.getLicenseTerms(URI.create(okapiUrlToUse), id, tenant, token);
    try {
      var processedLicenses = processClientResponse(response);

      var responseNode = objectMapper.createObjectNode();
      responseNode.set("licenseTerms", objectMapper.valueToTree(processedLicenses));
      return responseNode;
    } catch (Exception e) {
      log.error(FAILED_TO_MAP_RESPONSE, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, FAILED_TO_MAP_RESPONSE);
    }
  }

  private List<ObjectNode> processClientResponse(String response) throws JsonProcessingException {
    var clientResponseNode = (ObjectNode) objectMapper.readTree(response);
    var records = (ArrayNode) clientResponseNode.get("records");
    var uniqueLicenses = getUniqueLicenses(records);
    return getProcessLicenses(uniqueLicenses);
  }

  private HashSet<JsonNode> getUniqueLicenses(ArrayNode records) {
    var licenseSet = new HashSet<JsonNode>();
    records.forEach(responseRecord -> {
      var linkedLicenses = responseRecord.get("linkedLicenses");
      if (!linkedLicenses.isEmpty()) {
        var remoteIdObject = linkedLicenses.get(0).get("remoteId_object");
        var customProperties = remoteIdObject.get("customProperties");
        customProperties.forEach(property -> licenseSet.add(property.get(0)));
      }
    });
    return licenseSet;
  }

  private List<ObjectNode> getProcessLicenses(HashSet<JsonNode> licensesSet) {
    var processedLicenses = new ArrayList<ObjectNode>();
    licensesSet.forEach(license -> {
      var licenseNode = getMappedLicenseNode(license);
      processedLicenses.add(licenseNode);
    });
    return processedLicenses;
  }

  private ObjectNode getMappedLicenseNode(JsonNode license) {
    var licenseNode = objectMapper.createObjectNode();
    var licenseTypeNode = license.get("type");
    licenseNode.set(ID, license.get(ID));
    licenseNode.set(NAME, licenseTypeNode.get(NAME));
    licenseNode.set(LABEL, licenseTypeNode.get(LABEL));
    licenseNode.set(DESCRIPTION, licenseTypeNode.get(DESCRIPTION));
    licenseNode.put(VISIBILITY, "Public");
    licenseNode.set(PRIMARY, licenseTypeNode.get(PRIMARY));
    licenseNode.set(VALUE, license.get(VALUE));
    licenseNode.set(PUBLIC_NOTE, license.get(PUBLIC_NOTE));
    licenseNode.set(WEIGHT, licenseTypeNode.get(WEIGHT));
    return licenseNode;
  }

  private CompletableFuture<JsonNode> fetchLicenseTermsAsync(String id, String tenant, String token) {
    return CompletableFuture.supplyAsync(() -> getLicenceTermByIdResponseNode(id, tenant, token))
        .thenApply(licensesNode -> addId(licensesNode, id)).exceptionally(exception -> {
          log.error("Error during processing licence terms", exception.getCause());
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing licence terms");
        });
  }

  private JsonNode addId(ObjectNode licensesNode, String id) {
    licensesNode.put(ID, id);
    return licensesNode;
  }
}
