package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: CathUtils Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CathUtilsTest {

    private static final String EQUALS = "Result is not equal";
    private static final String NOTNULL = "Result is null";

    private static final String DAILY_LIST_XML = "<cs:DailyList\r\n"
        + "    xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\"\r\n"
        + "    xmlns:apd=\"http://www.govtalk.gov.uk/people/AddressAndPersonalDetails\"\r\n"
        + "    xmlns=\"http://www.govtalk.gov.uk/people/AddressAndPersonalDetails\"\r\n"
        + "    xmlns:p2=\"http://www.govtalk.gov.uk/people/bs7666\"\r\n"
        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
        + "    xsi:schemaLocation=\"http://www.courtservice.gov.uk/schemas/courtservice DailyList-v5-9.xsd\">\r\n"
        + "    <cs:DocumentID>\r\n"
        + "        <cs:UniqueID>0149051e-9db2-4b47-999e-fef76909ee73</cs:UniqueID>\r\n"
        + "        <cs:DocumentType>TEST</cs:DocumentType>\r\n" + "    </cs:DocumentID>\r\n"
        + "    <cs:ListHeader>\r\n" + "        <cs:ListCategory>Criminal</cs:ListCategory>\r\n"
        + "        <cs:StartDate>2020-01-21</cs:StartDate>\r\n"
        + "        <cs:EndDate>2020-01-21</cs:EndDate>\r\n"
        + "        <cs:Version>NOT VERSIONED</cs:Version>\r\n"
        + "        <cs:PublishedTime>2020-01-07T18:45:03.000</cs:PublishedTime>\r\n"
        + "    </cs:ListHeader>\r\n" + "    <cs:CrownCourt>\r\n"
        + "        <cs:CourtHouseType>TEST</cs:CourtHouseType>\r\n"
        + "        <cs:CourtHouseCode>TEST</cs:CourtHouseCode>\r\n"
        + "        <cs:CourtHouseName>SNARESBROOK</cs:CourtHouseName>\r\n"
        + "        <cs:CourtHouseAddress>\r\n" + "            <cs:Line>TEST</cs:Line>\r\n"
        + "            <cs:Postcode>TEST</cs:Postcode>\r\n" + "        </cs:CourtHouseAddress>\r\n"
        + "        <cs:CourtHouseTelephone>TEST</cs:CourtHouseTelephone>\r\n"
        + "        <cs:Description>TEST</cs:Description>\r\n" + "    </cs:CrownCourt>\r\n"
        + "    <cs:CourtLists>\r\n" + "        <cs:CourtList>\r\n"
        + "            <cs:CourtHouse>\r\n"
        + "                <cs:Description>TEST</cs:Description>\r\n"
        + "            </cs:CourtHouse>\r\n" + "            <cs:Sittings>\r\n"
        + "                <cs:Sitting>\r\n"
        + "                    <cs:CourtRoomNumber>235</cs:CourtRoomNumber>\r\n"
        + "                    <cs:SittingAt>TEST</cs:SittingAt>\r\n"
        + "                    <cs:SittingPriority>T</cs:SittingPriority>\r\n"
        + "                    <cs:SittingNote>TEST</cs:SittingNote>\r\n"
        + "                    <cs:Judiciary>\r\n" + "                        <cs:Judge>\r\n"
        + "                            <apd:CitizenNameTitle>TEST</apd:CitizenNameTitle>\r\n"
        + "                            <apd:CitizenNameForename>TEST</apd:CitizenNameForename>\r\n"
        + "                            <apd:CitizenNameSurname>Van-JUDGE</apd:CitizenNameSurname>\r\n"
        + "                            <apd:CitizenNameSuffix>TEST</apd:CitizenNameSuffix>\r\n"
        + "                            <apd:CitizenNameRequestedName>Freddy</apd:CitizenNameRequestedName>\r\n"
        + "                            <apd:CRESTjudgeID>TEST</apd:CRESTjudgeID>\r\n"
        + "                        </cs:Judge>\r\n" + "                        <cs:Justice>\r\n"
        + "                            <cs:CitizenNameTitle>TEST</cs:CitizenNameTitle>\r\n"
        + "                            <cs:CitizenNameForename>TEST</cs:CitizenNameForename>\r\n"
        + "                            <cs:CitizenNameSurname>TEST</cs:CitizenNameSurname>\r\n"
        + "                            <cs:CitizenNameSuffix>TEST</cs:CitizenNameSuffix>\r\n"
        + "                            <cs:CitizenNameRequestedName>TEST</cs:CitizenNameRequestedName>\r\n"
        + "                            <cs:CRESTjudgeID>TEST</cs:CRESTjudgeID>\r\n"
        + "                        </cs:Justice>\r\n" + "                    </cs:Judiciary>\r\n"
        + "                    <cs:Hearings>\r\n" + "                        <cs:Hearing>\r\n"
        + "                            <cs:HearingDetails HearingType=\"TRL\">\r\n"
        + "                                <cs:ListNote>TEST</cs:ListNote>\r\n"
        + "                                <cs:HearingType>TEST</cs:HearingType>\r\n"
        + "                            </cs:HearingDetails>\r\n"
        + "                            <cs:TimeMakingNote>TEST</cs:TimeMakingNote>\r\n"
        + "                            <cs:CaseNumber>92AD685737</cs:CaseNumber>\r\n"
        + "                            <cs:Prosecution\r\n"
        + "                                ProsecutingAuthority=\"Crown Prosecution Service\">\r\n"
        + "                                <cs:ProsecutingReference>92AD685737</cs:ProsecutingReference>\r\n"
        + "                                <cs:ProsecutingOrganisation>\r\n"
        + "                                    <cs:OrganisationCode>TEST</cs:OrganisationCode>\r\n"
        + "                                    <cs:OrganisationName>TEST</cs:OrganisationName>\r\n"
        + "                                    <cs:OrganisationAddress>\r\n"
        + "                                        <cs:Line>TEST</cs:Line>\r\n"
        + "                                        <cs:Postcode>TEST</cs:Postcode>\r\n"
        + "                                    </cs:OrganisationAddress>\r\n"
        + "                                    <cs:OrganisationDX>TEST</cs:OrganisationDX>\r\n"
        + "                                    <cs:ContactDetails>\r\n"
        + "                                        <cs:Email>TEST</cs:Email>\r\n"
        + "                                        <cs:Telephone>TEST</cs:Telephone>\r\n"
        + "                                        <cs:Fax>TEST</cs:Fax>\r\n"
        + "                                    </cs:ContactDetails>\r\n"
        + "                                </cs:ProsecutingOrganisation>\r\n"
        + "                                <cs:ProsecutionAuthority>TEST</cs:ProsecutionAuthority>\r\n"
        + "                            </cs:Prosecution>\r\n"
        + "                            <cs:ComittingCourt>\r\n"
        + "                                <cs:CourtHouseType>TEST</cs:CourtHouseType>\r\n"
        + "                                <cs:CourtHouseCode>TEST</cs:CourtHouseCode>\r\n"
        + "                                <cs:CourtHouseCodeType>TEST</cs:CourtHouseCodeType>\r\n"
        + "                                <cs:CourtHouseShortName>TEST</cs:CourtHouseShortName>\r\n"
        + "                                <cs:CourtHouseName>TEST</cs:CourtHouseName>\r\n"
        + "                                <cs:CourtHouseAddress>\r\n"
        + "                                    <cs:Line>TEST</cs:Line>\r\n"
        + "                                    <cs:Postcode>TEST</cs:Postcode>\r\n"
        + "                                </cs:CourtHouseAddress>\r\n"
        + "                                <cs:CourtHouseDX>TEST</cs:CourtHouseDX>\r\n"
        + "                                <cs:CourtHouseTelephone>TEST</cs:CourtHouseTelephone>\r\n"
        + "                                <cs:CourtHouseFax>TEST</cs:CourtHouseFax>\r\n"
        + "                                <cs:Description>TEST</cs:Description>\r\n"
        + "                            </cs:ComittingCourt>\r\n"
        + "                            <cs:ListNote>TEST</cs:ListNote>\r\n"
        + "                            <cs:Defendants>\r\n"
        + "                                <cs:Defendant>\r\n"
        + "                                    <cs:PersonalDetails>\r\n"
        + "                                        <apd:CitizenNameForename>TEST</apd:CitizenNameForename>\r\n"
        + "                                        <apd:CitizenNameSurname>Van</apd:CitizenNameSurname>\r\n"
        + "                                        <apd:CitizenNameRequestedName>Sri</apd:CitizenNameRequestedName>\r\n"
        + "                                        <cs:IsMasked>no</cs:IsMasked>\r\n"
        + "                                    </cs:PersonalDetails>\r\n"
        + "                                    <cs:ContactDetails>\r\n"
        + "                                        <cs:Email>TEST</cs:Email>\r\n"
        + "                                        <cs:Telephone>TEST</cs:Telephone>\r\n"
        + "                                        <cs:Fax>TEST</cs:Fax>\r\n"
        + "                                    </cs:ContactDetails>\r\n"
        + "                                    <cs:Charges>\r\n"
        + "                                        <cs:Charge CJSoffenceCode=\"TH68001A\"\r\n"
        + "                                            IndictmentCountNumber=\"0\">\r\n"
        + "                                            <cs:CRN>TEST</cs:CRN>\r\n"
        + "                                            <cs:OffenceStatement>Attempted theft</cs:OffenceStatement>\r\n"
        + "                                            <cs:OffenceLocation>\r\n"
        + "                                                <cs:Line>TEST</cs:Line>\r\n"
        + "                                                <cs:Postcode>TEST</cs:Postcode>\r\n"
        + "                                            </cs:OffenceLocation>\r\n"
        + "                                            <cs:ArrestingPoliceForceCode>TEST"
        + "                                            </cs:ArrestingPoliceForceCode>\r\n"
        + "                                            <cs:ComittedOnBail>TEST</cs:ComittedOnBail>\r\n"
        + "                                            <cs:OffenceStartDateTime>TEST</cs:OffenceStartDateTime>\r\n"
        + "                                            <cs:OffenceEndDateTime>TEST</cs:OffenceEndDateTime>\r\n"
        + "                                            <cs:ArraingmentDate>TEST</cs:ArraingmentDate>\r\n"
        + "                                            <cs:ConvictionDate>TEST</cs:ConvictionDate>\r\n"
        + "                                            <cs:OffenceParticulars>TEST</cs:OffenceParticulars>\r\n"
        + "                                            <cs:CRESToffenceNumber>TEST</cs:CRESToffenceNumber>\r\n"
        + "                                            <cs:Plea>TEST</cs:Plea>\r\n"
        + "                                            <cs:Verdict>TEST</cs:Verdict>\r\n"
        + "                                            <cs:Disposals>\r\n"
        + "                                                <cs:Disposal>TEST</cs:Disposal>\r\n"
        + "                                                <cs:CRESTDisposalData>TEST</cs:CRESTDisposalData>\r\n"
        + "                                            </cs:Disposals>\r\n"
        + "                                            <cs:SentenceTerm>TEST</cs:SentenceTerm>\r\n"
        + "                                            <cs:TermType>TEST</cs:TermType>\r\n"
        + "                                            <cs:ForLife>TEST</cs:ForLife>\r\n"
        + "                                            <cs:ChargeType>TEST</cs:ChargeType>\r\n"
        + "                                            <cs:IndictmentNumber>TEST</cs:IndictmentNumber>\r\n"
        + "                                            <cs:IndictmentCountNumber>TEST</cs:IndictmentCountNumber>\r\n"
        + "                                            <cs:CJSoffenceCode>TEST</cs:CJSoffenceCode>\r\n"
        + "                                            <cs:PNCoffencecode>TEST</cs:PNCoffencecode>\r\n"
        + "                                            <cs:HOoffencecode>TEST</cs:HOoffencecode>\r\n"
        + "                                            <cs:Life>TEST</cs:Life>\r\n"
        + "                                            <cs:BreachMultiple>TEST</cs:BreachMultiple>\r\n"
        + "                                        </cs:Charge>\r\n"
        + "                                    </cs:Charges>\r\n"
        + "                                    <cs:OriginalCharges></cs:OriginalCharges>\r\n"
        + "                                </cs:Defendant>\r\n"
        + "                            </cs:Defendants>\r\n"
        + "                            <cs:Respondent></cs:Respondent>\r\n"
        + "                        </cs:Hearing>\r\n" + "                    </cs:Hearings>\r\n"
        + "                </cs:Sitting>\r\n" + "            </cs:Sittings>\r\n"
        + "        </cs:CourtList>\r\n" + "    </cs:CourtLists>\r\n" + "</cs:DailyList>";

    @Mock
    private Environment mockEnvironment;

    @BeforeEach
    public void setup() {
        Mockito.mockStatic(InitializationService.class);
    }

    @AfterEach
    public void tearDown() {
        // Clear down statics
        Mockito.clearAllCaches();
    }

    @Test
    void testGetDateTimeAsString() {
        String result = CathUtils.getDateTimeAsString(LocalDateTime.now());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetHttpPostRequest() {
        // Setup
        CourtelJson courtelJson = DummyCourtelUtil.getListJson();
        String url = "https://dummy.com/url";
        // Run
        HttpRequest result = CathUtils.getHttpPostRequest(url, courtelJson);
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testIsApimEnabled() {
        InitializationService mockInitializationService = Mockito.mock(InitializationService.class);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        Mockito.when(mockInitializationService.getEnvironment()).thenReturn(mockEnvironment);

        String[] expectedResults = {"false", "true"};
        for (String expectedResult : expectedResults) {
            Mockito.when(mockEnvironment.getProperty(Mockito.isA(String.class)))
                .thenReturn(expectedResult);
            Boolean result = CathUtils.isApimEnabled();
            assertEquals(expectedResult, result.toString().toLowerCase(Locale.getDefault()),
                EQUALS);
        }
    }

    @Test
    void testGetApimUri() {
        InitializationService mockInitializationService = Mockito.mock(InitializationService.class);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        Mockito.when(mockInitializationService.getEnvironment()).thenReturn(mockEnvironment);

        String expectedResult = "www.dummy/uri";
        Mockito.when(mockEnvironment.getProperty(Mockito.isA(String.class)))
            .thenReturn(expectedResult);
        String result = CathUtils.getApimUri();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGenerateJsonFromString() {
        JSONObject result = CathUtils.generateJsonFromString(DAILY_LIST_XML);
        String jsonAsString = result.toString(4); // This indentation value of 4 matches the indentation of the xml
        assertNotNull(jsonAsString, NOTNULL);
    }
}
