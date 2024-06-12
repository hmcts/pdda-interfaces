package uk.gov.hmcts.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static io.restassured.RestAssured.given;

@Configuration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SetupControllerSmokeTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(SetupControllerSmokeTest.class);
    private static final String DB_HOST = "pdda.db_host";
    private static final String DB_PORT = "pdda.db_port";
    
    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void smokeTest() {
        LOG.info("testUrl={}", testUrl);
        LOG.info("DB_HOST: {}",DB_HOST);
        LOG.info("DB_PORT: {}",DB_PORT);
        Response response = given()
            .contentType(ContentType.HTML)
            .when()
            .get()
            .then()
            .extract().response();
        
        LOG.info("Smoketest.status={}",response.statusCode());
        LOG.info("Smoketest.pageContent={}",response.asString());
        Assertions.assertEquals(200, response.statusCode());
    } 
}
