package ee.openeid.siga.test.datafile.augmentation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Augmentation")
@Feature("XAdES signature augmentation checks")
class XadesChecksSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Augmentation requires at least one signature")
    def "Augmenting unsigned container returns no signature error"() {
        given: "upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerWithoutSignatures.asice"))

        when: "try augmenting not suitable signature"
        Response response = datafile.tryAugmentContainer(flow)

        then: "augmentation is forbidden"
        RequestErrorValidator.validate(response, RequestError.NO_SIGNATURES)
    }

    @Story("Augmentation requires at least one signature")
    def "Augmenting container only with #description returns no personal signature error"() {
        given: "upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(container))

        when: "try augmenting not suitable signature"
        Response response = datafile.tryAugmentContainer(flow)

        then: "augmentation is forbidden"
        RequestErrorValidator.validate(response, RequestError.NO_PERSONAL_SIGNATURES)

        where:
        description           | container
        "e-seal"              | "asice_e-seal_ocsp_cert_expired.asice"
        "untrusted signature" | "SS-4_teadmataCA.4.asice"
    }

}
