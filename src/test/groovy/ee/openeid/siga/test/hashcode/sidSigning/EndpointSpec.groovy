package ee.openeid.siga.test.hashcode.sidSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.restassured.response.Response
import spock.lang.Tag

import static ee.openeid.siga.test.TestData.DEFAULT_SID_DEMO_ACCOUNT

@Tag("smartId")
@Epic("Smart-ID signing (hashcode)")
@Feature("SID endpoint validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "SID signing request not allowed with invalid profile: #profile"() {
        given: "Upload container and use default SID Demo account"
        hashcode.createDefaultContainer(flow)
        Map signingRequestBody = RequestData.sidSigningRequestDefaultBody(DEFAULT_SID_DEMO_ACCOUNT)

        when: "Try signing with invalid profile"
        signingRequestBody["signatureProfile"] = profile
        Response response = hashcode.tryStartSidSigning(flow, signingRequestBody)

        then: "Request validation error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_PROFILE)

        where:
        profile << ["", " ", "123", "@!*", "UNKNOWN", "B_BES", "B_EPES", "LT_TM", "lt_TM", "lt_tm", "LT-TM", "LT TM", "T", "LTA"]
    }
}
