package ee.openeid.siga.test.hashcode.validation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response

@Epic("Hashcode")
@Feature("Validate hashcode container")
class HashcodeValidationFailSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Signatures file name check")
    def "Validating hashcode container with signature file name missing keyword 'signatures' is not allowed"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile("hashcodeInvalidSignatureFileName.asice"))

        when:
        Response response = hashcode.tryValidateContainerInSession(flow)

        then:
        RequestErrorValidator.validate(response, RequestError.MISSING_SIGNATURES)
    }

}
