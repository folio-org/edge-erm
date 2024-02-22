package org.folio.edge.erm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.folio.edge.erm.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class TenantControllerIT extends BaseIntegrationTests {

  @Autowired 
  private MockMvc mockMvc;

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
