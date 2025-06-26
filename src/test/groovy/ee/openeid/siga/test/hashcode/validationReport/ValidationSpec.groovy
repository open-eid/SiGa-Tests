package ee.openeid.siga.test.hashcode.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response

@Epic("Validation Report (hashcode)")
@Feature("Get ASiC-E validation report")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
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
