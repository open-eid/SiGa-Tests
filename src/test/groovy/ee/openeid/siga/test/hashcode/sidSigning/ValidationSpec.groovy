package ee.openeid.siga.test.hashcode.sidSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
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

    @Story("SID sign container with 256 datafiles")
    def "SID sign hashcode container with 256 datafiles under 5 seconds"() {
        given: "upload unsigned container with 256 datafiles"
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile("hashcode-container-256.asice"))

        when: "Sign and measure the time"
        long startSignTime = System.nanoTime()
        hashcode.sidSigningSuccessful(flow, RequestData.sidCertificateChoiceRequestDefaultBody())
        long endSignTime = System.nanoTime()

        then: "Signing time is under 5 seconds"
        long elapsedMs = (long) ((endSignTime - startSignTime) / 1_000_000)
        assertThat(elapsedMs, lessThan(5000L))

        println("\nAEG: ${elapsedMs}")
    }
}
