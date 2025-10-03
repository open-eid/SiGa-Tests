package ee.openeid.siga.test.datafile.sidSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import spock.lang.Tag

import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Tag("smartId")
@Epic("Smart-ID signing (datafile)")
@Feature("SID endpoint validation")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("SID sign existing container")
    def "SID sign existing container with Thales ID-card signature successful"() {
        given: "upload container with existing signatures"
        datafile.uploadContainer(flow,
                RequestData.uploadDatafileRequestBodyFromFile("TEST_ESTEID2025_ASiC-E_XAdES_LT+LTA.asice"))

        when: "SID sign"
        datafile.sidSigningSuccessful(flow, RequestData.sidCertificateChoiceRequestDefaultBody())

        then: "validate container to have valid signatures"
        datafile.validateContainerInSession(flow).then()
                .body("validationConclusion.validSignaturesCount", is(2))
    }
}
