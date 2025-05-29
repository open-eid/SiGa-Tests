package ee.openeid.siga.test.datafile.augmentation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.restassured.response.Response

class DatafileAugmentChecksSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "Augmenting container only with #description is forbidden"() {
        given: "upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(container))

        when: "try augmenting not suitable signature"
        Response response = datafile.tryAugmentContainer(flow)

        then: "augmentation is forbidden"
        RequestErrorValidator.validate(response, RequestError.NO_PERSONAL_SIGNATURES)

        where:
        description                            | container
        "e-seal"                               | "asice_e-seal_ocsp_cert_expired.asice"
        "signature with untrusted certificate" | "SS-4_teadmataCA.4.asice"
    }

}
