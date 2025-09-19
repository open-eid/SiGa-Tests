package ee.openeid.siga.test.hashcode.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.module.jsv.JsonSchemaValidator
import io.restassured.response.Response

@Epic("Validation Report (hashcode)")
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
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("static/ValidationReportSchema.json"))

        where:
        containerType   | containerName
        "Signed ASiC-E" | "hashcode.asice"
        "Signed DDOC"   | "hashcodeDdocTest.ddoc" //In report validatedDocument field should be null
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

}
