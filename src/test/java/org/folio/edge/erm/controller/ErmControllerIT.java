package org.folio.edge.erm.controller;

import static org.folio.edge.erm.TestConstants.GET_LICENSE_TERM_BY_ID_URL;
import static org.folio.edge.erm.TestConstants.LICENSE_TERMS_BATCH_REQUEST_PATH;
import static org.folio.edge.erm.TestConstants.LICENSE_TERMS_BATCH_URL;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.folio.edge.erm.BaseIntegrationTests;
import org.folio.edge.erm.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class ErmControllerIT extends BaseIntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getLicenseTerms_shouldReturnLicenseTerms() throws Exception {
    doGet(mockMvc, GET_LICENSE_TERM_BY_ID_URL)
        .andExpect(status().isOk())
        .andExpect(jsonPath("licenseTerms", hasSize(7)))
        .andExpect(jsonPath("licenseTerms[0].id", is(201)))
        .andExpect(jsonPath("licenseTerms[0].name", is("CancellationNotice")))
        .andExpect(jsonPath("licenseTerms[0].label", is("Cancellation Notice")))
        .andExpect(jsonPath("licenseTerms[0].description", is("Cancellation Notice")))
        .andExpect(jsonPath("licenseTerms[0].visibility", is("Public")))
        .andExpect(jsonPath("licenseTerms[0].primary", is(false)))
        .andExpect(jsonPath("licenseTerms[0].value", is("90 Day Advance Notice")))
        .andExpect(jsonPath("licenseTerms[0].publicNote", is("Public Note 2")))
        .andExpect(jsonPath("licenseTerms[0].weight", is(0)))
        .andExpect(jsonPath("licenseTerms[1].name", is("PerpetualAccess")))
        .andExpect(jsonPath("licenseTerms[2].name", is("ExpirationDate")))
        .andExpect(jsonPath("licenseTerms[3].name", is("TestPickList")))
        .andExpect(jsonPath("licenseTerms[4].name", is("InterlibraryLoan")))
        .andExpect(jsonPath("licenseTerms[5].name", is("AutomaticRenewalName")))
        .andExpect(jsonPath("licenseTerms[6].name", is("TestMultiList")))
        .andExpect(jsonPath("licenseTerms[5].value", is(1.1)))
        .andExpect(jsonPath("licenseTerms[1].value", is(90)));
  }

  @Test
  void batchLicenseTerms_shouldReturnLicenseTermsInBatch() throws Exception {
    var request = TestUtil.readFileContentFromResources(LICENSE_TERMS_BATCH_REQUEST_PATH);
    doPost(mockMvc, LICENSE_TERMS_BATCH_URL, request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("results", hasSize(2)))
        .andExpect(jsonPath("results[0].licenseTerms", hasSize(7)))
        .andExpect(jsonPath("results[0].id", is("123355-1000141789")))
        .andExpect(jsonPath("results[1].licenseTerms", hasSize(7)))
        .andExpect(jsonPath("results[1].id", is("334455-1000141666")));
  }
}
