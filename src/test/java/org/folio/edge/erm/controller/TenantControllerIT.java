package org.folio.edge.erm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.folio.edge.erm.BaseIntegrationTests;
import org.junit.jupiter.api.Test;

class TenantControllerIT extends BaseIntegrationTests {

  @Test
  void postTenant_shouldReturn204() throws Exception {
    mockMvc
        .perform(
            post("/_/tenant")
                .headers(defaultHeaders())
                .content("{ \"module_from\":  \"edge-erm.1\",  \"module_to\":  \"edge-erm.2\" }"))
        .andExpect(status().isNoContent());
  }
}
