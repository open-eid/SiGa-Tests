package ee.openeid.siga.test.datafile.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.util.Utils
import io.qameta.allure.*
import io.restassured.module.jsv.JsonSchemaValidator
import io.restassured.response.Response
import spock.lang.Tag

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals
import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Validation Report (datafile)")
@Feature("Get container validation report")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Validation report corresponds to schema")
    def "Validation report corresponds to schema: #containerType"() {
        expect:
        datafile.validateContainerFromFile(flow, containerName).then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/ValidationReportSchema.json"))

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
                .body("timeStampTokens[0].errors", hasSize(1))
                .body("timeStampTokens[0].errors.content", contains("The time-stamp message imprint is not intact!"))
                .body("timeStampTokens[0].certificates", hasSize(1))
                .body("timeStampTokens[0].certificates[0].commonName", is("DEMO SK TIMESTAMPING AUTHORITY 2023E"))
                .body("timeStampTokens[0].certificates[0].type", is("CONTENT_TIMESTAMP"))
                .body("timeStampTokens[0].certificates[0].content", startsWith("MIIDEjCCApigAwIBAgIQM7BQCImkdt18qWDYdbfOtjAKBggqhkjOP"))

                .body("timeStampTokens[1].indication", is("TOTAL-PASSED"))
                .body("timeStampTokens[1].subIndication", is(emptyOrNullString()))
                .body("timeStampTokens[1].timestampLevel", is("QTSA"))
                .body("timeStampTokens[1].errors", hasSize(0))
                .body("timeStampTokens[1].warning.content[0]", is("The time-stamp token does not cover container datafile!"))
                .body("timeStampTokens[1].certificates", hasSize(1))
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

    def "Validation report of '#containerType' contains all relevant info"() {
        when:
        Response validationResponse = datafile.validateContainerFromFile(flow, containerName)

        then:
        String expectedReport = new String(Utils.readFileFromResources("${containerName}_Report.json"))
        String actualReport = validationResponse.then().extract().asString()
        assertJsonEquals(expectedReport, actualReport)

        where:
        containerType         | containerName
        "signed ASiC-E"       | "containerWithMultipleSignatures.asice"
        "signed BDOC"         | "valid-bdoc-tm-newer.bdoc"
        "signed DDOC"         | "ddocSingleSignature.ddoc"
        "signed PDF"          | "pdfSingleTestSignature.pdf"
        "signed CAdES ASiC-S" | "TEST_ESTEID2018_ASiC-S_CAdES_LT.scs"
        "signed XAdES ASiC-S" | "signedAsicsWithSignedDdoc.scs"
        "timestamped ASiC-S"  | "2xTST-both-valid-2nd-tst-not-covering-nested-container.asics"
    }

    @Story("Validate ASiC-S container in session")
    def "Signed ASiC-S validation report in session contains signature info"() {
        given: "upload container"
        datafile.uploadContainerFromFile(flow, "asicsContainerWithLtSignatureWithoutTST.scs")

        when: "validate container in session"
        Response validationResponse = datafile.validateContainerInSession(flow)

        then: "validation report contains signature, but no timestamps"
        validationResponse.then().rootPath("validationConclusion.")
                .body("signaturesCount", is(1))
                .body("validSignaturesCount", is(1))
                .body("signatures[0].signedBy", is("JÕEORG,JAAK-KRISTJAN,38001085718"))
                .body("timeStampTokens", hasSize(0))
    }

    @Story("Validate ASiC-S container in session")
    def "Timestamped ASiC-S validation report in session contains timestamp info"() {
        given: "upload container"
        datafile.uploadContainerFromFile(flow, TestData.DEFAULT_ASICS_CONTAINER_NAME)

        when: "validate container in session"
        Response validationResponse = datafile.validateContainerInSession(flow)

        then: "validation report contains timestamp, but no signatures"
        validationResponse.then().rootPath("validationConclusion.")
                .body("signaturesCount", is(0))
                .body("validSignaturesCount", is(0))
                .body("timeStampTokens", hasSize(1))
                .body("timeStampTokens[0].signedBy", is("DEMO SK TIMESTAMPING AUTHORITY 2023E"))
                .body("timeStampTokens[0].signedTime", is("2024-05-28T12:24:09Z"))
    }

    @Story("Validate ASiC-S container without session")
    def "Timestamped composite ASiC-S validation report contains nested signature and outer timestamp info"() {
        when: "validate container without session"
        Response validationResponse = datafile.validateContainerFromFile(flow, "asicsContainerWithBdocAndTimestamp.asics")

        then: "validation report contains nested signature and outer timestamp info"
        validationResponse.then().rootPath("validationConclusion.")
                .body("signaturesCount", is(1))
                .body("signatures[0].signatureFormat", is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].subjectDistinguishedName.commonName", is("O’CONNEŽ-ŠUSLIK TESTNUMBER,MARY ÄNN,60001016970"))
                .body("timeStampTokens", hasSize(1))
                .body("timeStampTokens[0].signedTime", is("2024-03-27T12:42:57Z"))
    }

    @Story("Validate ASiC-S container without session")
    def "Timestamped non-composite ASiC-S validation report contains only timestamp info"() {
        when: "validate container without session"
        Response validationResponse = datafile.validateContainerFromFile(flow, TestData.DEFAULT_ASICS_CONTAINER_NAME)

        then: "validation report contains only outer timestamp"
        validationResponse.then().rootPath("validationConclusion.")
                .body("signaturesCount", is(0))
                .body("timeStampTokens", hasSize(1))
                .body("timeStampTokens[0].signedTime", is("2024-05-28T12:24:09Z"))
    }

}
