package ee.openeid.siga.test.datafile.midSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import spock.lang.Tag

import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Tag("mobileId")
@Epic("Mobile-ID signing (datafile)")
@Feature("MID signing validation")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("MID sign existing container")
    def "MID sign existing container with Thales ID-card signature successful"() {
        given: "upload container with existing signatures"
        datafile.uploadContainer(flow,
                RequestData.uploadDatafileRequestBodyFromFile("TEST_ESTEID2025_ASiC-E_XAdES_LT+LTA.asice"))

        when: "MID sign"
        datafile.midSigning(flow, RequestData.midStartSigningRequestDefaultBody())

        then: "validate container to have valid signatures"
        datafile.validateContainerInSession(flow).then()
                .body("validationConclusion.validSignaturesCount", is(2))
    }
}
