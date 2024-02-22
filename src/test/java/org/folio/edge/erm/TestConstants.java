package org.folio.edge.erm;

public class TestConstants {

  public static final String TEST_TENANT = "test";
  public static final String NOT_AUTHORIZED = "/erm/license-terms/test_401";
  public static final String FORBIDDEN_URL = "/erm/license-terms/test_403";
  public static final String GET_LICENSE_TERM_BY_ID_URL = "/erm/license-terms/123355-1000141789";
  public static final String LICENSE_TERMS_BATCH_URL = "/erm/license-terms/batch";
  public static final String LICENSE_TERMS_RESPONSE_PATH = "__files/responses/license-terms.json";
  public static final String LICENSE_TERMS_RESPONSE_2_PATH = "__files/responses/license-terms_2.json";
  public static final String LICENSE_TERMS_BATCH_REQUEST_PATH = "__files/request/license-terms-batch.json";
  public static final String NOT_FOUND_RESPONSE_PATH = "__files/responses/license-terms-empty.json";
}
