package ee.openeid.siga.test.datafile.sidSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Retry
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

    @Story("SID signing not allowed when datafile changed during signing")
    @Retry(count = 2, delay = 500, exceptions = [AssertionError])
    def "SID sign container when datafile is #datafileChanges during signing fails"() {
        given: "upload unsigned container and start signing"
        datafile.uploadContainer(flow,
                RequestData.uploadDatafileRequestBodyFromFile("containerWithoutSignatures.asice"))
        Response startResponse = datafile.startSidSigning(flow, RequestData.sidStartSigningRequestDefaultBody())

        when: "manipulate datafile after signing start and poll after manipulation"
        switch (datafileChanges) {
            case "deleted":
                datafile.deleteDataFile(flow, datafile.getDataFilesList(flow).path("dataFiles[0].fileName"))
                break
            case "added":
                datafile.addDefaultDataFile(flow)
                break
        }

        datafile.pollForSidSigningStatus(flow, startResponse.path("generatedSignatureId"))

        then: "signing returns error"
        RequestErrorValidator.validate(flow.getSidStatus(), RequestError.INVALID_CHANGED_DATAFILE)

        where:
        datafileChanges | _
        "deleted"       | _
        "added"         | _
    }
}
