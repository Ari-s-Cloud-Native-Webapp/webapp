package com.example.webapp.integration;

import com.example.webapp.repository.HealthCheckRepository;
import com.zaxxer.hikari.HikariDataSource;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;

import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private HealthCheckRepository healthCheckRepository;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setup() {
        RestAssured.reset();
        RestAssured.port = port;
        RestAssured.basePath = "";
        healthCheckRepository.deleteAll();
    }

    /**
     * Tests whether sending a GET request to the /healthz endpoint with no query parameters and body
     * return a 200 OK Status code, correct headers and no request body
     */
    @Test
    public void healthCheckTest() {
        given()
                .when().get("/healthz")
                .then()
                .statusCode(200)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff")
                .body(emptyOrNullString());
    }

    /**
     * Tests if the count of records in the health_check database updates when a new record is created
     */
    @Test
    public void healthCheckTest_CountRecords(){
        long countBefore = healthCheckRepository.count();

        given()
        .when().get("/healthz")
                .then()
                .statusCode(200);

        long countAfter = healthCheckRepository.count();
        assertEquals(countBefore + 1, countAfter);
    }

    /**
     * Tests that when the database is unavailable, the GET /healthz endpoint returns a 503
     * Service Unavailable status.
     * @throws SQLException if closing the data source causes errors
     */
    @Test
    @DirtiesContext
    public void healthCheckTest_ServiceUnavailable () throws SQLException {

        given()
        .when().get("/healthz")
                .then()
                .statusCode(200);

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        hikariDataSource.close();

        given()
        .when().get("/healthz")
                .then()
                .statusCode(503)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    /**
     * Tests that when query parameters are included to the request body of the GET endpoint
     * the request returns 400 Bad Request status.
     */
    @Test
    public void healthCheckTest_QueryParameters() {
        given()
                .queryParam("query", "query")
                .when().get("/healthz")
                .then()
                .statusCode(400)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    /**
     * Tests that when a request body is sent with a GET request to the /healthz endpoint,
     * the request returns 400 Bad Request status.
     */
    @Test
    public void healthCheckTest_Payload() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "query":"query"
                        }
                        """)
                .when().get("/healthz")
                .then()
                .statusCode(400)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    /**
     * Tests that when unsupported method: POST is passed on the /healthz endpoint,
     * it returns a 405 Method not allowed status.
     */
    @Test
    public void healthCheckTest_POST(){
        given()
                .when().post("/healthz")
                .then()
                .statusCode(405)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    /**
     * Tests that when unsupported method: PUT is passed on the /healthz endpoint,
     * it returns a 405 Method not allowed status.
     */
    @Test
    public void healthCheckTest_PUT(){
        given()
                .when().put("/healthz")
                .then()
                .statusCode(405)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    /**
     * Tests that when unsupported method: PATCH is passed on the /healthz endpoint,
     * it returns a 405 Method not allowed status.
     */
    @Test
    public void healthCheckTest_PATCH(){
        given()
                .when().patch("/healthz")
                .then()
                .statusCode(405)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    /**
     * Tests that when unsupported method: DELETE is passed on the /healthz endpoint,
     * it returns a 405 Method not allowed status.
     */
    @Test
    public void healthCheckTest_DELETE(){
        given()
                .when().delete("/healthz")
                .then()
                .statusCode(405)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    /**
     * Tests that when unsupported method: HEAD is passed on the /healthz endpoint,
     * it returns a 405 Method not allowed status.
     */
    @Test
    public void healthCheckTest_HEAD(){
        given()
                .when().head("/healthz")
                .then()
                .statusCode(405)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    /**
     * Tests that when unsupported method: OPTIONS is passed on the /healthz endpoint,
     * it returns a 405 Method not allowed status.
     */
    @Test
    public void healthCheckTest_OPTIONS(){
        given()
                .when().options("/healthz")
                .then()
                .statusCode(405)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }
}
