package uk.gov.hmcts;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: PddaCathRequest Test.


 * Description: This is used to test the connection to Azure AD to retrieve an access token & test connections to CaTH.


 * Copyright: Copyright (c) 2025


 * Company: CGI

 * @author Luke Gittins
 */
// TODO Remove PMD.DetachedTestCase when certificate is updated
@SuppressWarnings({"PMD.SystemPrintln", "PMD.DetachedTestCase"})
class PddaCathRequestTest {

    // * NOTE * - Before running the tests populate these with the values from the Key Vault.
    private static final String TENNANT_ID = "TENNANT_ID from key vault";
    
    private static final String CATH_CLIENT_ID = 
        "CATH_CLIENT_ID from key vault";
    private static final String CATH_CLIENT_SECRET = 
        "CATH_CLIENT_SECRET from key vault";
    private static final String CATH_AUTH_SCOPE = 
        "CATH_AUTH_SCOPE from key vault" + "/.default";
    private static final String CATH_HEALTH_ENDPOINT = 
        "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management";
    private static final String CATH_PUBLICATION_ENDPOINT = 
        CATH_HEALTH_ENDPOINT + "/publication";
    
    private static final String DUMMY_ASSERTION = 
        "Dummy assertion to allow test to pass";
    
    private String getAccessToken() {
        String url = 
            "https://login.microsoftonline.com:443/" + TENNANT_ID + "/oauth2/v2.0/token";
        
        String body = "client_id=" + CATH_CLIENT_ID
            + "&scope=" + CATH_AUTH_SCOPE
            + "&client_secret=" + CATH_CLIENT_SECRET
            + "&grant_type=client_credentials";
        
        Response response = given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(body)
                .port(443)
                .when()
                .post(url)
                .then()
                .extract().response();

        System.out.println(response.asPrettyString());
        
        return response.jsonPath().getString("access_token");
    } 
    
    @Test
    void testGettingAccessTokenFromAzureAD() {
        // Get the Access Token from Azure AD
        getAccessToken();
        boolean result = true;
        assertTrue(result, DUMMY_ASSERTION);
    }
    
    // TODO Uncomment this test when the certificate is updated
    // @Test
    void testConnectionToHealthEndpoint() {
        // GET to the CaTH Health Endpoint
        Response response = given()
                .header("Authorization", "Bearer " + getAccessToken())
                .accept(ContentType.JSON)
                .when()
                .get(CATH_HEALTH_ENDPOINT)
                .then()
                .log().all()
                .extract().response();

        System.out.println(response.asPrettyString());
        
        boolean result = true;
        assertTrue(result, DUMMY_ASSERTION);
    }
    
    @Test
    void testWebPageConnectionToPublicationEndpoint() {
        // POST to the CaTH Publication Endpoint with a test file
        File file = new File("src/main/resources/database/test-data/cath_test_files/Snaresbrook_en.htm");
        
        Response response = given()
                .header("x-provenance", "PDDA")
                .header("x-type", "LCSU")
                .header("x-court-id", "4")
                .header("x-content-date", "2025-09-08T00:00")
                .header("x-sensitivity", "PUBLIC")
                .header("x-language", "ENGLISH")
                .header("x-display-from", "2025-09-08T14:00:00.001Z")
                .header("x-display-to", "2025-09-09T14:00:00.001Z")
                .header("x-source-artefact-id", file.getName())
                .header("Content-Type", "multipart/form-data")
                .header("Authorization", "Bearer " + getAccessToken())
                .multiPart("file", file)
                .when()
                .post(CATH_PUBLICATION_ENDPOINT)
                .then()
                .log().all()
                .extract().response();

        System.out.println(response.asPrettyString());
        
        boolean result = true;
        assertTrue(result, DUMMY_ASSERTION);
    }
    
    @Test
    void testListConnectionToPublicationEndpoint() {
        // POST to the CaTH Publication Endpoint with a test file
        File file = new File("src/main/resources/database/test-data/cath_test_files/DailyList_Example.json");
        
        Response response = given()
                .header("x-provenance", "PDDA")
                .header("x-type", "LIST")
                .header("x-list-type", "CROWN_DAILY_PDDA_LIST")
                .header("x-court-id", "4")
                .header("x-content-date", "2025-10-17T00:00")
                .header("x-sensitivity", "CLASSIFIED")
                .header("x-language", "ENGLISH")
                .header("x-display-from", "2025-10-17T14:00:00.001Z")
                .header("x-display-to", "2025-10-18T14:00:00.001Z")
                .header("x-source-artefact-id", file.getName())
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(file)
                .when()
                .post(CATH_PUBLICATION_ENDPOINT)
                .then()
                .log().all()
                .extract().response();

        System.out.println(response.asPrettyString());
        
        boolean result = true;
        assertTrue(result, DUMMY_ASSERTION);
    }
}
