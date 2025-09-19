package ee.openeid.siga.test.datafile.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import io.qameta.allure.*
import io.restassured.module.jsv.JsonSchemaValidator
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Validation Report (datafile)")
@Feature("Get augmented container report")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Validation report corresponds to schema")
    def "Validation report corresponds to schema: #containerType"() {
        expect:
        datafile.validateContainerFromFile(flow, containerName).then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("static/ValidationReportSchema.json"))

        where:
        containerType                  | containerName
        "Signed ASiC-E"                | "TEST_ESTEID2018_ASiC-E_XAdES_LT+LT.sce"
        "Signed BDOC"                  | "valid-bdoc-tm-newer.bdoc"
        "Signed DDOC"                  | "ddocSingleSignature.ddoc"
        "Timestamped ASiC-S"           | "2xTstFirstInvalidSecondNotCoveringNestedTimestampedAsics.asics"
        "Timestamped composite ASiC-S" | "timestampedAsicsWithAsice.asics"
        "Signed ASiC-S"                | "signedAsicsWithSignedDdoc.scs"
        "Signed PDF"                   | "pdfSingleTestSignature.pdf"
    }

    //TODO: SIGA-1098 - comment in errors paths, if fixed
    def "Timestamped ASiC-S validation report contains all new timestamp token info"() {
        when:
        Response validationResponse = datafile.validateContainerFromFile(flow,
                "2xTstFirstInvalidSecondNotCoveringNestedTimestampedAsics.asics")

        then:
        validationResponse.then().rootPath("validationConclusion.")
                .body("timeStampTokens[0].indication", is("TOTAL-FAILED"))
                .body("timeStampTokens[0].subIndication", is("HASH_FAILURE"))
                .body("timeStampTokens[0].timestampLevel", is("QTSA"))
                .body("timeStampTokens[0].warning", hasSize(0))
//                .body("timeStampTokens[0].errors", hasSize(1))
//                .body("timeStampTokens[0].errors[0].content[0]", is("The time-stamp message imprint is not intact!"))
                .body("timeStampTokens[0].certificates", hasSize(3))
                .body("timeStampTokens[0].certificates[0].commonName", is("DEMO SK TIMESTAMPING AUTHORITY 2023E"))
                .body("timeStampTokens[0].certificates[0].type", is("CONTENT_TIMESTAMP"))
                .body("timeStampTokens[0].certificates[0].content", startsWith("MIIDEjCCApigAwIBAgIQM7BQCImkdt18qWDYdbfOtjAKBggqhkjOP"))

                .body("timeStampTokens[1].indication", is("TOTAL-PASSED"))
                .body("timeStampTokens[1].subIndication", is(emptyOrNullString()))
                .body("timeStampTokens[1].timestampLevel", is("QTSA"))
                .body("timeStampTokens[1].errors", hasSize(0))
                .body("timeStampTokens[1].warning.content[0]", is("The time-stamp token does not cover container datafile!"))
                .body("timeStampTokens[1].certificates", hasSize(3))
    }

    def "Augmented XAdES signature validation report contains new archiveTimeStamps info"() {
        when:
        Response validationResponse = datafile.validateContainerFromFile(flow,
                "TEST_ESTEID2018_ASiC-E_XAdES_LTA+LTA.sce")

        then:
        validationResponse.then().rootPath("validationConclusion.signatures.info.")
                .body("archiveTimeStamps[0].signedTime[0]", is("2025-06-09T14:43:20Z"))
                .body("archiveTimeStamps[0].indication[0]", is("PASSED"))
                .body("archiveTimeStamps[0].subIndication[0]", is(emptyOrNullString()))
                .body("archiveTimeStamps[0].signedBy[0]", is("DEMO SK TIMESTAMPING UNIT 2025E"))
                .body("archiveTimeStamps[0].country[0]", is("EE"))
                .body("archiveTimeStamps[0].content[0]", startsWith("MIIHPQYJKoZIhvcNAQcCoIIHLjCCByoCAQMxDTALBg"))

                .body("archiveTimeStamps[1].signedTime[0]", is("2025-06-09T14:43:20Z"))
                .body("archiveTimeStamps[1].indication[0]", is("PASSED"))
                .body("archiveTimeStamps[1].subIndication[0]", is(emptyOrNullString()))
                .body("archiveTimeStamps[1].signedBy[0]", is("DEMO SK TIMESTAMPING UNIT 2025E"))
                .body("archiveTimeStamps[1].country[0]", is("EE"))
                .body("archiveTimeStamps[1].content[0]", startsWith("MIIHPAYJKoZIhvcNAQcCoIIHLTCCBykCAQMxDTALBg"))
    }

}
