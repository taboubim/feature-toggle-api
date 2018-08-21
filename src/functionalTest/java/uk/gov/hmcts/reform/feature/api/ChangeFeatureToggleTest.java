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

public class ChangeFeatureToggleTest extends BaseTest {

    private RequestSpecification requestSpecification;

    @Before
    public void setUp() {
        requestSpecification = given()
            .spec(jsonRequest)
            .auth().preemptive().basic(testAdminUser, testAdminPassword);
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_successfully_enable_feature_toggle_in_feature_store() throws IOException {
        //Feature name should be unique in the feature store
        String featureUuid = "smoke-test-" + UUID.randomUUID();

        createFeatureToggle(featureUuid, loadJson("feature-toggle-disabled.json"), requestSpecification);

        //Enable feature toggle
        requestSpecification
            .log().uri()
            .and()
            .when()
            .post(FF4J_STORE_FEATURES_URL + featureUuid + "/enable")
            .then()
            .statusCode(202);

        //Delete the created feature
        requestSpecification.delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_successfully_disable_feature_toggle_in_feature_store() throws IOException {
        //Feature name should be unique in the feature store
        String featureUuid = "smoke-test-" + UUID.randomUUID();

        createFeatureToggle(featureUuid, loadJson("feature-toggle-enabled.json"), requestSpecification);

        //Disable feature toggle
        requestSpecification
            .log().uri()
            .and()
            .when()
            .post(FF4J_STORE_FEATURES_URL + featureUuid + "/disable")
            .then()
            .statusCode(202);

        //Delete the created feature
        requestSpecification.delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }
}
