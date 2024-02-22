package org.folio.edge.erm.service;

import static org.folio.edge.erm.TestConstants.LICENSE_TERMS_RESPONSE_2_PATH;
import static org.folio.edge.erm.TestConstants.LICENSE_TERMS_RESPONSE_PATH;
import static org.folio.edge.erm.TestConstants.NOT_FOUND_RESPONSE_PATH;
import static org.folio.edge.erm.TestConstants.TEST_TENANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.folio.edge.erm.TestUtil;
import org.folio.edge.erm.client.ErmClient;
import org.folio.edge.erm.config.AppConfig;
import org.folio.edge.erm.config.property.ThreadPoolExecutorConfigs;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest(classes = {
    ErmService.class,
    AppConfig.class,
    Jackson2ObjectMapperBuilder.class
})
@EnableConfigurationProperties(value = ThreadPoolExecutorConfigs.class)
class ErmServiceTest {

  public static final String TEST_ID = "e6bc03c6-c137-4221-b679-a7c5c31f986c";
  private static final String OKAPI_URL = "http://localhost:9130";
  @Autowired
  private ErmService ermService;
  @MockBean
  private ErmClient ermClient;

  @BeforeEach
  void before() {
    ReflectionTestUtils.setField(ermService, "okapiUrl", OKAPI_URL);
  }

  @Test
  void getLicenseTerms_shouldReturnLicenseTerms() {
    var expectedErmContent = TestUtil.readFileContentFromResources(LICENSE_TERMS_RESPONSE_PATH);
    var referenceId = TEST_ID;
    when(ermClient.getLicenseTerms(URI.create(OKAPI_URL), referenceId, TEST_TENANT,
        StringUtils.EMPTY)).thenReturn(
        expectedErmContent);

    var response = ermService.getLicenseTerms(referenceId, TEST_TENANT, StringUtils.EMPTY);

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
    var expectedErmContent_1 = TestUtil.readFileContentFromResources(LICENSE_TERMS_RESPONSE_PATH);
    var expectedErmContent_2 = TestUtil.readFileContentFromResources(LICENSE_TERMS_RESPONSE_2_PATH);
    when(ermClient.getLicenseTerms(URI.create(OKAPI_URL), "123355-1000141789", TEST_TENANT,
        StringUtils.EMPTY))
        .thenReturn(expectedErmContent_1);
    when(ermClient.getLicenseTerms(URI.create(OKAPI_URL), "334455-1000141666", TEST_TENANT,
        StringUtils.EMPTY))
        .thenReturn(expectedErmContent_2);

    var licenseTermsBatch = ermService.batchLicenseTerms(ids, TEST_TENANT, StringUtils.EMPTY);

    var results = licenseTermsBatch.getResults();
    var firstItem = results.get(0);
    var secondItem = results.get(1);
    assertEquals("123355-1000141789", firstItem.getId());
    assertEquals("334455-1000141666", secondItem.getId());
    assertEquals(7, firstItem.getLicenseTerms().size());
    assertEquals(7, secondItem.getLicenseTerms().size());
  }

  @Test
  void getLicenseTerms_shouldThrowErrorWhenCanNotMapResponse() {
    var referenceId = TEST_ID;
    when(ermClient.getLicenseTerms(URI.create(OKAPI_URL), referenceId, TEST_TENANT, StringUtils.EMPTY))
        .thenReturn("wrong_format_response");

    var exception = assertThrows(ResponseStatusException.class,
        () -> ermService.getLicenseTerms(referenceId, TEST_TENANT, StringUtils.EMPTY));
    assertEquals("Failed to map licenses response", exception.getReason());
    assertEquals(INTERNAL_SERVER_ERROR, exception.getStatusCode());
  }

  @Test
  void getLicenseTermsBatch_shouldThrowErrorWhenNoIdsProvidedInRequest() {
    List<String> ids = Collections.emptyList();

    var exception = assertThrows(ResponseStatusException.class,
        () -> ermService.batchLicenseTerms(ids, TEST_TENANT, StringUtils.EMPTY));
    assertEquals("Ids to retrieve are not provided", exception.getReason());
    assertEquals(BAD_REQUEST, exception.getStatusCode());
  }

  @Test
  void getLicenseTerms_shouldThrowErrorWhenEmptyResponse() {
    var referenceId = TEST_ID;
    var emptyResponse = TestUtil.readFileContentFromResources(NOT_FOUND_RESPONSE_PATH);
    when(ermClient.getLicenseTerms(URI.create(OKAPI_URL), referenceId, TEST_TENANT,
        StringUtils.EMPTY))
        .thenReturn(emptyResponse);

    var exception = assertThrows(ResponseStatusException.class,
        () -> ermService.getLicenseTerms(referenceId, TEST_TENANT, StringUtils.EMPTY));
    assertEquals(String.format("License terms by id %s are not found", referenceId),
        exception.getReason());
    assertEquals(NOT_FOUND, exception.getStatusCode());
  }
}
