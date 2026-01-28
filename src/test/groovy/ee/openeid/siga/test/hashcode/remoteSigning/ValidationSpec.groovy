package ee.openeid.siga.test.hashcode.remoteSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.lessThan

@Epic("Remote signing (hashcode)")
@Feature("Remote signing validation")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Remote sign existing container")
    def "Remote sign existing container with Thales ID-card signature successful"() {
        given: "upload container with existing signatures"
        hashcode.uploadContainer(flow,
                RequestData.uploadHashcodeRequestBodyFromFile("hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LT+LTA.asice"))

        when: "Remote sign"
        hashcode.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        then: "validate container to have valid signatures"
        hashcode.validateContainerInSession(flow).then()
                .body("validationConclusion.validSignaturesCount", is(2))
    }

    @Story("Remote sign container with 256 datafiles")
    def "Remote sign hashcode container with 256 datafiles under 30 seconds"() {
        given: "upload unsigned container with 256 datafiles"
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile("hashcode-container-256.asice"))

        when: "Sign and measure the time"
        long startSignTime = System.nanoTime()
        hashcode.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())
        long endSignTime = System.nanoTime()

        then: "Signing time is under 30 seconds"
        long elapsedMs = (long) ((endSignTime - startSignTime) / 1_000_000)
        assertThat(elapsedMs, lessThan(30000L))
    }
}
