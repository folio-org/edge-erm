package org.folio.edge.erm.service;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.edge.erm.TestConstants.LICENSE_TERMS_RESPONSE_2_PATH;
import static org.folio.edge.erm.TestConstants.LICENSE_TERMS_RESPONSE_PATH;
import static org.folio.edge.erm.TestConstants.NOT_FOUND_RESPONSE_PATH;
import static org.folio.edge.erm.TestConstants.TEST_TENANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Collections;
import java.util.List;
import org.folio.edge.erm.TestUtil;
import org.folio.edge.erm.client.ErmClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ErmServiceTest {

  public static final String TEST_ID = "e6bc03c6-c137-4221-b679-a7c5c31f986c";

  @InjectMocks
  private ErmService ermService;
  @Mock
  private ErmClient ermClient;
  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void getLicenseTerms_shouldReturnLicenseTerms() {
    var expectedErmContent = TestUtil.readFileContentFromResources(LICENSE_TERMS_RESPONSE_PATH);
    var ermResponse = objectMapper.readTree(expectedErmContent);
    when(ermClient.getLicenseTerms(TEST_ID, TEST_TENANT, EMPTY)).thenReturn(ermResponse);

    var response = ermService.getLicenseTerms(TEST_ID, TEST_TENANT, EMPTY);

    var licenseTerms = response.getLicenseTerms();
    assertEquals(7, licenseTerms.size());
    assertEquals("CancellationNotice", licenseTerms.get(0).getName());
    assertEquals("PerpetualAccess", licenseTerms.get(1).getName());
    assertEquals("ExpirationDate", licenseTerms.get(2).getName());
    assertEquals("TestPickList", licenseTerms.get(3).getName());
    assertEquals("InterlibraryLoan", licenseTerms.get(4).getName());
    assertEquals("AutomaticRenewalName", licenseTerms.get(5).getName());
    assertEquals("TestMultiList", licenseTerms.get(6).getName());
  }

  @Test
  void batchLicenseTerms_shouldReturnLicenseTerms() {
    var ids = List.of("123355-1000141789", "334455-1000141666");
    var expectedErmContent1 = TestUtil.readFileContentFromResources(LICENSE_TERMS_RESPONSE_PATH);
    var expectedErmContent2 = TestUtil.readFileContentFromResources(LICENSE_TERMS_RESPONSE_2_PATH);
    var ermResponse1 = objectMapper.readTree(expectedErmContent1);
    var ermResponse2 = objectMapper.readTree(expectedErmContent2);
    when(ermClient.getLicenseTerms("123355-1000141789", TEST_TENANT, EMPTY))
        .thenReturn(ermResponse1);
    when(ermClient.getLicenseTerms("334455-1000141666", TEST_TENANT, EMPTY))
        .thenReturn(ermResponse2);

    var licenseTermsBatch = ermService.batchLicenseTerms(ids, TEST_TENANT, EMPTY);

    var results = licenseTermsBatch.getResults();
    var firstItem = results.get(0);
    var secondItem = results.get(1);
    assertEquals("123355-1000141789", firstItem.getId());
    assertEquals("334455-1000141666", secondItem.getId());
    assertEquals(7, firstItem.getLicenseTerms().size());
    assertEquals(7, secondItem.getLicenseTerms().size());
  }

  @Test
  void getLicenseTermsBatch_shouldThrowErrorWhenNoIdsProvidedInRequest() {
    List<String> ids = Collections.emptyList();

    var exception = assertThrows(ResponseStatusException.class,
        () -> ermService.batchLicenseTerms(ids, TEST_TENANT, EMPTY));
    assertEquals("Ids to retrieve are not provided", exception.getReason());
    assertEquals(BAD_REQUEST, exception.getStatusCode());
  }

  @Test
  void getLicenseTerms_shouldThrowErrorWhenEmptyResponse() {
    var emptyResponse = TestUtil.readFileContentFromResources(NOT_FOUND_RESPONSE_PATH);
    var emptyResponseNode = objectMapper.readTree(emptyResponse);
    when(ermClient.getLicenseTerms(TEST_ID, TEST_TENANT, EMPTY))
        .thenReturn(emptyResponseNode);

    var exception = assertThrows(ResponseStatusException.class,
        () -> ermService.getLicenseTerms(TEST_ID, TEST_TENANT, EMPTY));
    assertEquals(String.format("License terms by id %s are not found", TEST_ID),
        exception.getReason());
    assertEquals(NOT_FOUND, exception.getStatusCode());
  }
}
