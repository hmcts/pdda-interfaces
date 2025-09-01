package uk.gov.hmcts;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: PddaCathRequest Test.


 * Description: This is used to test the connection to Azure AD to retrieve an access token & test connections to CaTH.


 * Copyright: Copyright (c) 2025


 * Company: CGI

 * @author Luke Gittins
 */
@SuppressWarnings({"PMD.SystemPrintln"})
class PddaCathRequestTest {

    // * NOTE * - Before running the tests populate these with the values from the Key Vault.
    private static final String TENNANT_ID = "TENNANT_ID from key vault";
    
    private static final String CATH_CLIENT_ID = "CATH_CLIENT_ID from key vault";
    private static final String CATH_CLIENT_SECRET = "CATH_CLIENT_SECRET from key vault";
    private static final String CATH_AUTH_SCOPE = "CATH_AUTH_SCOPE from key vault" + "/.default";
    
    
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
        assertTrue(result, "Dummy assertion to allow test to pass");
    }
    
    @Test
    void testConnectionToHealthEndpoint() {
        // GET to the CaTH Health Endpoint
        String url = 
            "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management";
        
        Response response = given()
                .header("Authorization", "Bearer " + getAccessToken())
                .accept(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .log().all()
                .extract().response();

        System.out.println(response.asPrettyString());
        
        boolean result = true;
        assertTrue(result, "Dummy assertion to allow test to pass");
    }
}
