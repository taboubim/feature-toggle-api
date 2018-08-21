package uk.gov.hmcts.reform.feature.webconsole;

import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.BaseTest;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;

public class AdminAccessTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_not_allow_access_for_non_admin_user() {


        Cookies cookies = given()
            .spec(jsonRequest)
            .contentType(ContentType.URLENC)
            .formParam("username", testEditorUser)
            .formParam("password", testEditorPassword)
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .response()
            .getDetailedCookies();

        given()
            .spec(jsonRequest)
            .cookies(cookies)
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .body("html.head.title", equalTo("Error"));
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_verify_login_logout_journey() {

        Cookies cookies = given()
            .spec(jsonRequest)
            .contentType(ContentType.URLENC)
            .formParam("username", testAdminUser)
            .formParam("password", testAdminPassword)
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .response()
            .getDetailedCookies();

        given()
            .spec(jsonRequest)
            .cookies(cookies)
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .body("html.head.title", equalTo("FF4J - Home"));

        given()
            .spec(jsonRequest)
            .cookies(cookies)
            .get("/logout")
            .then()
            .statusCode(OK.value());

        given()
            .spec(jsonRequest)
            .cookies(cookies)
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .body("html.head.title", equalTo("Login Page"));
    }

    @Test
    public void should_allow_admin_to_login_to_ff4j_web_console() {

        String location = given()
            .spec(jsonRequest)
            .contentType(ContentType.URLENC)
            .formParam("username", testAdminUser)
            .formParam("password", testAdminPassword)
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .header(LOCATION);

        assertThat(location).contains(FF4J_WEB_CONSOLE_URL);
    }
}
