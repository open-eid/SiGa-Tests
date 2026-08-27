package ee.openeid.siga.test.datafile.remoteSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Remote signing (datafile)")
@Feature("Remote signing validation")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Remote sign existing container")
    def "Remote sign existing container with Thales ID-card signature successful"() {
        given: "upload container with existing signatures"
        datafile.uploadContainer(flow,
                RequestData.uploadDatafileRequestBodyFromFile("TEST_ESTEID2025_ASiC-E_XAdES_LT+LTA.asice"))

        when: "Remote sign"
        datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        then: "validate container to have valid signatures"
        datafile.validateContainerInSession(flow).then()
                .body("validationConclusion.validSignaturesCount", is(2))
    }

    @Story("Remote signing for ASiC-S containers is not allowed")
    def "Starting remote signing for #containerDesc ASiC-S is not allowed"() {
        given: "upload container"
        datafile.uploadContainerFromFile(flow, containerName)

        when: "try starting remote signing"
        Response response = datafile.tryStartRemoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_CONTAINER_TYPE)

        where:
        containerDesc                | containerName
        "timestamped"                | TestData.DEFAULT_ASICS_CONTAINER_NAME
        "signed"                     | "asicsContainerWithLtSignatureWithoutTST.scs"
        "unsigned and untimestamped" | "0xSIG_0xTST_asics.asics"
    }

}
