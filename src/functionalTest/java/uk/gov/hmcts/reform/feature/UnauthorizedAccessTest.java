package uk.gov.hmcts.reform.feature;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class UnauthorizedAccessTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_restrict_anonymous_access_to_api_ff4j() {
        given()
            .spec(jsonRequest)
            .auth().none()
            .delete(FF4J_STORE_FEATURES_URL + "doesnotexist")
            .then()
            .statusCode(UNAUTHORIZED.value());
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_restrict_anonymous_access_to_ff4j_web_console() {
        given()
            .spec(jsonRequest)
            .auth().none()
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .and()
            .body("html.head.title", equalTo("Login Page"));
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_restrict_access_for_user_to_access_ff4j_web_console() {
        given()
            .spec(jsonRequest)
            .auth().preemptive().basic(testEditorUser, testEditorPassword)
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .and()
            .body("html.head.title", equalTo("Login Page"));
    }
}
