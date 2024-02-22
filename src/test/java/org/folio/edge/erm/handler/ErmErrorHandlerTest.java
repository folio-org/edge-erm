package org.folio.edge.erm.handler;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.folio.edge.erm.BaseIntegrationTests;
import org.apache.commons.lang3.StringUtils;
import org.folio.edge.erm.TestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class ErmErrorHandlerTest extends BaseIntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void handleForbiddenException_shouldProcessException() throws Exception {
    doGet(mockMvc, TestConstants.FORBIDDEN_URL)
        .andExpect(status().isForbidden())
        .andExpect(header()
            .stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("code", is(403)));
  }

  @Test
  void handleNotAuthorized_shouldProcessException() throws Exception {
    doGet(mockMvc, TestConstants.NOT_AUTHORIZED)
        .andExpect(status().isUnauthorized())
        .andExpect(header()
            .stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
  }

  @Test
  void handleBadRequest_shouldProcessException() throws Exception {
    doPost(mockMvc, TestConstants.LICENSE_TERMS_BATCH_URL, StringUtils.EMPTY)
        .andExpect(status().isBadRequest())
        .andExpect(header()
            .stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
  }
}
