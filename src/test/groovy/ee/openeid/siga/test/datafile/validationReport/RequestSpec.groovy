package ee.openeid.siga.test.datafile.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.helper.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Validation report (datafile)")
@Feature("Validation report request validation")
class RequestSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Validation report for non-existing container fails")
    def "Requesting validation report after deleting container returns an error"() {
        given: "uploaded the container and delete it"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(TestData.DEFAULT_ASICE_CONTAINER_NAME))
        datafile.deleteContainer(flow)

        when: "try getting validation report"
        Response response = datafile.tryValidateContainerInSession(flow)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)
    }

}
