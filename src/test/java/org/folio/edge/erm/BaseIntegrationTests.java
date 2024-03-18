package org.folio.edge.erm;

import static org.folio.edge.erm.TestConstants.TEST_TENANT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.folio.edge.erm.service.ErmService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.folio.edgecommonspring.client.EnrichUrlClient;
import org.folio.spring.integration.XOkapiHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Log4j2
@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@AutoConfigureMockMvc
@AutoConfigureObservability
public abstract class BaseIntegrationTests {
  protected static final WireMockServer WIRE_MOCK = new WireMockServer(
      WireMockSpring.options()
          .dynamicPort()
          .extensions(new ResponseTemplateTransformer(false)));
  private static final String TEST_API_KEY = "eyJzIjoiQlBhb2ZORm5jSzY0NzdEdWJ4RGgiLCJ0IjoidGVzdCIsInUiOiJ0ZXN0X2FkbWluIn0=";

  @BeforeAll
  static void beforeAll(@Autowired ErmService ermService, @Autowired EnrichUrlClient enrichUrlClient) {
    WIRE_MOCK.start();
    ReflectionTestUtils.setField(enrichUrlClient, "okapiUrl", WIRE_MOCK.baseUrl());
    ReflectionTestUtils.setField(ermService, "okapiUrl", WIRE_MOCK.baseUrl());
  }

  @AfterAll
  static void afterAll() {
    WIRE_MOCK.stop();
  }

  protected static ResultActions doGet(MockMvc mockMvc, String url) throws Exception {
    return mockMvc.perform(get(url)
        .headers(defaultHeaders()));
  }

  @SneakyThrows
  protected static ResultActions doPost(MockMvc mockMvc, String url, String body) {
    return mockMvc.perform(post(url)
        .content(body)
        .headers(defaultHeaders()));
  }

  protected static ResultActions doGetWithParams(MockMvc mockMvc, String url, String paramName, String paramValue)
      throws Exception {
    return mockMvc.perform(get(url)
        .param(paramName, paramValue)
        .headers(defaultHeaders()));
  }
  protected static ResultActions doGetWithLimitAndOffset(MockMvc mockMvc, String url, Integer limit, Integer offset)
      throws Exception {
    return mockMvc.perform(get(url)
        .param("limit", limit.toString())
        .param("offset", offset.toString())
        .headers(defaultHeaders()));
  }

  protected static HttpHeaders defaultHeaders() {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(APPLICATION_JSON);
    httpHeaders.put(XOkapiHeaders.TENANT, List.of(TEST_TENANT));
    httpHeaders.put(XOkapiHeaders.URL, List.of(WIRE_MOCK.baseUrl()));
    httpHeaders.put(XOkapiHeaders.AUTHORIZATION, List.of(TEST_API_KEY));
    return httpHeaders;
  }

}
