package ee.openeid.siga.test.hashcode.sidSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Retry
import spock.lang.Tag

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.lessThan

@Tag("smartId")
@Epic("Smart-ID signing (hashcode)")
@Feature("SID signing validation")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("SID sign existing container")
    def "SID sign existing container with Thales ID-card signature successful"() {
        given: "upload container with existing signatures"
        hashcode.uploadContainer(flow,
                RequestData.uploadHashcodeRequestBodyFromFile("hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LT+LTA.asice"))

        when: "SID sign"
        hashcode.sidSigningSuccessful(flow, RequestData.sidCertificateChoiceRequestDefaultBody())

        then: "validate container to have valid signatures"
        hashcode.validateContainerInSession(flow).then()
                .body("validationConclusion.validSignaturesCount", is(2))
    }

    @Story("SID signing not allowed when datafile changed during signing")
    @Retry(count = 2, delay = 500, exceptions = [AssertionError])
    def "SID sign container when datafile is #datafileChanges during signing fails"() {
        given: "upload unsigned container and start signing"
        hashcode.uploadContainerFromFile(flow, "hashcodeWithoutSignature.asice")
        Response startResponse = hashcode.startSidSigning(flow, RequestData.sidStartSigningRequestDefaultBody())

        when: "manipulate datafile after signing start and poll after manipulation"
        switch (datafileChanges) {
            case "deleted":
                hashcode.deleteDataFile(flow, hashcode.getDataFilesList(flow).path("dataFiles[0].fileName"))
                break
            case "added":
                hashcode.addDefaultDataFile(flow)
                break
        }

        hashcode.pollForSidSigningStatus(flow, startResponse.path("generatedSignatureId"))

        then: "signing returns error"
        RequestErrorValidator.validate(flow.getSidStatus(), RequestError.INVALID_CHANGED_DATAFILE)

        where:
        datafileChanges | _
        "deleted"       | _
        "added"         | _
    }

    @Story("SID sign container with 256 datafiles")
    def "SID sign hashcode container with 256 datafiles under 30 seconds"() {
        given: "upload unsigned container with 256 datafiles"
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile("hashcode-container-256.asice"))

        when: "Sign and measure the time"
        long startSignTime = System.nanoTime()
        hashcode.sidSigningSuccessful(flow, RequestData.sidCertificateChoiceRequestDefaultBody())
        long endSignTime = System.nanoTime()

        then: "Signing time is under 30 seconds"
        long elapsedMs = (long) ((endSignTime - startSignTime) / 1_000_000)
        assertThat(elapsedMs, lessThan(30000L))
    }
}
