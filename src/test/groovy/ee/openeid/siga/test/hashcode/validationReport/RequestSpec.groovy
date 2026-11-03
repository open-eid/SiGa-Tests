package ee.openeid.siga.test.hashcode.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.response.Response

import static org.hamcrest.Matchers.is

@Epic("Validation report (hashcode)")
@Feature("Validation report request validation")
class RequestSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Client supplied validation level is ignored")
    def "Hashcode container is validated at LongTermData validation level regardless of client supplied validation level #validationLevel"() {
        given: "validation request with client supplied validation level"
        Map requestBody = RequestData.uploadHashcodeRequestBodyFromFile("hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LTA_DigiDoc4.asice")
        requestBody.put("validationLevel", validationLevel)

        when: "validate container without session"
        Response response = hashcode.validateContainer(flow, requestBody)

        then: "container is validated at LongTermData validation level"
        response.then().body("validationConclusion.validationLevel", is("LONG_TERM_DATA"))

        where:
        validationLevel << ["ArchivalData", "LongTermData", "ValidationLevel"]
    }

}
