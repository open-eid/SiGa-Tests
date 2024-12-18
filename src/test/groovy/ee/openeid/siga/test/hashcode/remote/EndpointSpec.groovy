package ee.openeid.siga.test.hashcode.remote

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.restassured.response.Response

@Epic("Hashcode")
@Feature("Remote signing endpoints validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "Remote signing request not allowed with invalid profile: #profile"() {
        given: "Upload container"
        hashcode.createDefaultContainer(flow)
        Map requestBody = RequestData.remoteSigningStartDefaultRequest()

        when: "Try signing with invalid profile"
        requestBody["signatureProfile"] = profile
        Response response = hashcode.tryStartRemoteSigning(flow, requestBody)

        then: "Request validation error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_PROFILE)

        where:
        profile << ["", " ", "123", "@!*", "UNKNOWN", "B_BES", "B_EPES", "LT_TM", "lt_TM", "lt_tm", "LT-TM", "LT TM", "T", "LTA"]
    }
}
