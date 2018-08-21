package uk.gov.hmcts.reform.feature.api;

import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.feature.BaseTest;

import java.io.IOException;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class FeatureStoreTest extends BaseTest {
    private RequestSpecification requestSpecification;

    @Before
    public void setUp() {
        requestSpecification = given()
            .spec(jsonRequest)
            .auth().preemptive().basic(testAdminUser, testAdminPassword);
    }

    @Test
    public void should_return_feature_store_details() throws IOException {
        String featureUuid1 = UUID.randomUUID().toString();
        String featureUuid2 = UUID.randomUUID().toString();

        createFeatureToggle(featureUuid1, loadJson("feature-toggle-disabled.json"), requestSpecification);
        createFeatureToggle(featureUuid2, loadJson("feature-toggle-disabled.json"), requestSpecification);

        JsonPath jsonPath = requestSpecification
            .get("/api/ff4j/store").jsonPath();

        assertThat(jsonPath.getString("type")).isEqualTo("org.ff4j.audit.proxy.FeatureStoreAuditProxy");
        assertThat(jsonPath.getInt("numberOfFeatures")).isEqualTo(2);
        assertThat(jsonPath.getString("cache")).isNull();

        requestSpecification.delete(FF4J_STORE_FEATURES_URL + featureUuid1);
        requestSpecification.delete(FF4J_STORE_FEATURES_URL + featureUuid2);
    }
}
