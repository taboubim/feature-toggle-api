package uk.gov.hmcts.reform.feature.api;

import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.BaseTest;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import java.io.IOException;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class CreateFeatureToggleTest extends BaseTest {

    private RequestSpecification requestSpecification;

    @Before
    public void setUp() {
        requestSpecification = given()
            .spec(jsonRequest)
            .auth().preemptive().basic(testAdminUser, testAdminPassword);
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_successfully_create_feature_toggle_in_feature_store() throws IOException {
        //Feature name should be unique in the feature store
        String featureUuid = "smoke-test-" + UUID.randomUUID();

        String createRequestBody = loadJson("feature-toggle-enabled.json");

        requestSpecification
            .log().uri()
            .and()
            .body(createRequestBody.replace("{uid}", featureUuid))
            .when()
            .put(FF4J_STORE_FEATURES_URL + featureUuid)
            .then()
            .statusCode(201);

        //Delete the created feature
        requestSpecification.delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }
}
