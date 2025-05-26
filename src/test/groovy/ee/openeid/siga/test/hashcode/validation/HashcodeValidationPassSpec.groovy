package ee.openeid.siga.test.hashcode.validation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response

@Epic("Hashcode")
@Feature("Validate hashcode container")
class HashcodeValidationPassSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Signatures file name check")
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
}
