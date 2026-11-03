package ee.openeid.siga.test.hashcode.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.module.jsv.JsonSchemaValidator
import io.restassured.response.Response
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals
import static net.javacrumbs.jsonunit.JsonAssert.whenIgnoringPaths
import static org.hamcrest.Matchers.*

@Epic("Validation report (hashcode)")
@Feature("Get ASiC-E validation report")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Issue("SIGA-1149")
    @Story("Validation report corresponds to schema")
    def "Validation report corresponds to schema: #containerType"() {
        expect:
        hashcode.validateContainerFromFile(flow, containerName).then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/ValidationReportSchema.json"))

        where:
        containerType                              | containerName
        "Signed ASiC-E"                            | "hashcode.asice"
        "Signed DDOC"                              | "hashcodeDdocTest.ddoc" //In report validatedDocument field should be null
        "Signed ASiC-E with LTA signature"         | "hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LTA.asice"
        "Signed ASiC-E with invalid LTA signature" | "hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LTA_InvalidDatafileHash.asice"
    }

    @Story("Signature filename check")
    def "Validating hashcode container with #description in signature file name is allowed"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile(fileName + ".asice"))

        when:
        Response response = hashcode.validateContainerInSession(flow)

        then:
        response.then().statusCode(200)

        where:
        description         | fileName                                  | result
        "special chars"     | "hashcodeSpecialCharsInSignatureFileName" | "allowed"
        "prefix and suffix" | "hashcodePrefixSuffixInSignatureFileName" | "allowed"
    }

    @Story("Signature filename check")
    def "Validating hashcode container with signature file name missing keyword 'signatures' is not allowed"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile("hashcodeInvalidSignatureFileName.asice"))

        when:
        Response response = hashcode.tryValidateContainerInSession(flow)

        then:
        RequestErrorValidator.validate(response, RequestError.MISSING_SIGNATURES)
    }

    @Story("Validation report is the same in session and without session")
    def "Validation report of uploaded '#containerType' is the same in session and without session"() {
        given: "upload container"
        hashcode.uploadContainerFromFile(flow, containerName)

        when: "validate container in session and without session"
        String inSessionReport = hashcode.validateContainerInSession(flow).asString()
        String withoutSessionReport = hashcode.validateContainerFromFile(flow, containerName).asString()

        then: "reports are the same apart from validation time"
        assertJsonEquals(withoutSessionReport, inSessionReport, whenIgnoringPaths("validationConclusion.validationTime"))

        where:
        containerType                                         | containerName
        "signed ASiC-E with LT signature"                     | "hashcode.asice"
        "signed ASiC-E with LTA signature"                    | "hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LTA.asice"
        "signed ASiC-E with LT and LTA signatures"            | "hashcode_TEST_ESTEID2018_ASiC-E_XAdES_LT+LTA.asice"
        "signed ASiC-E with LTA signature and empty datafile" | "hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LTA_EmptyDatafileSize.asice"
    }

    @Story("Hashcode DDOC validation has no validation level")
    def "Validating hashcode DDOC container reports no validation level"() {
        when: "validate DDOC container without session"
        Response response = hashcode.validateContainerFromFile(flow, "hashcodeDdoc.ddoc")

        then: "DDOC is validated without validation level"
        response.then().rootPath("validationConclusion.")
                .body("signatureForm", is("DIGIDOC_XML_1.3_hashcode"))
                .body("validationLevel", nullValue())
                .body("signatures", hasSize(1))
    }

}
