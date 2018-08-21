package uk.gov.hmcts.reform.feature;

import com.google.common.io.Resources;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.Charsets;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.logging.appinsights.SyntheticHeaders;

import java.io.IOException;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
@TestPropertySource("classpath:application.properties")//Requires for executing tests through IntelliJ IDE
public abstract class BaseTest {

    @Value("${test-url}")
    private String testUrl;

    @Value("${test-admin-user}")
    protected String testAdminUser;

    @Value("${test-admin-password}")
    protected String testAdminPassword;

    @Value("${test-editor-user}")
    protected String testEditorUser;

    @Value("${test-editor-password}")
    protected String testEditorPassword;

    private static final String SYNTHETIC_SOURCE_HEADER_VALUE = "Feature Toggle Smoke Test";

    protected static final String FF4J_STORE_FEATURES_URL = "api/ff4j/store/features/";

    protected static final String FF4J_WEB_CONSOLE_URL = "ff4j-web-console/";

    protected static final RequestSpecification jsonRequest = new RequestSpecBuilder()
        .addHeader("accept", "application/json+json")
        .setContentType("application/json")
        .build();


    protected String loadJson(String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }

    @Before
    public void setUpRestAssured() {
        RestAssured.baseURI = this.testUrl;

        RestAssured.requestSpecification = new RequestSpecBuilder()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .addHeader(SyntheticHeaders.SYNTHETIC_TEST_SOURCE, SYNTHETIC_SOURCE_HEADER_VALUE)
            .build();

        RestAssured.useRelaxedHTTPSValidation();
    }

    @After
    public void tearDown() {
        RestAssured.reset();
    }

    protected void createFeatureToggle(
        String featureUuid,
        String createRequestBody,
        RequestSpecification requestSpecification
    ) {
        requestSpecification
            .log().uri()
            .and()
            .body(createRequestBody.replace("{uid}", featureUuid))
            .when()
            .put(FF4J_STORE_FEATURES_URL + featureUuid)
            .then()
            .statusCode(201);
    }
}
