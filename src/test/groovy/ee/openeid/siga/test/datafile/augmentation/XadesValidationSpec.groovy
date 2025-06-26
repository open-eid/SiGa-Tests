package ee.openeid.siga.test.datafile.augmentation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.SignatureFormat
import ee.openeid.siga.test.model.SignatureLevel
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.Utils
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import spock.lang.Tag

import static ee.openeid.siga.test.util.EnumNameMatcher.matchesEnumName
import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Augmentation")
@Feature("XAdES signature augmentation validation")
class XadesValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "Augmenting uploaded container with one LT signature is successful"() {
        given: "upload container with single LT signature for augmentation"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerSingleSignatureValidUntil-2026-01-22.asice"))

        when: "augment container in session"
        datafile.augmentContainer(flow)

        then: "augmentation is successful and signature is LTA"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("signaturesCount", is(1))
                .body("validSignaturesCount", is(1))
                .body("signatureForm", is("ASiC-E"))
                .body("validationWarnings", hasSize(1))
                .body("validationWarnings.content", hasItem(TestData.TEST_ENV_VALIDATION_WARNING))

                .body("signatures[0].subjectDistinguishedName.commonName", is("JÕEORG,JAAK-KRISTJAN,38001085718"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", is("PNOEE-38001085718"))
                .body("signatures[0].signatureFormat", matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA))
                .body("signatures[0].signatureLevel", matchesEnumName(SignatureLevel.QESIG))
                .body("signatures[0].indication", is("TOTAL-PASSED"))
                .body("signatures[0].warnings", hasSize(0))
                .body("signatures[0].errors", hasSize(0))
                .body("signatures[0].claimedSigningTime", is("2024-05-28T07:23:00Z"))
                .body("signatures[0].info.bestSignatureTime", is("2024-05-28T07:23:04Z"))
    }

    def "Augmenting created container with one LT signature is successful"() {
        given: "create container and remote sign it"
        datafile.createContainer(flow, RequestData.createDatafileRequestDefaultBody())
        datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        when: "augment container in session"
        sleep(10000)
        datafile.augmentContainer(flow)
        Utils.saveContainerFromResponse(datafile.getContainer(flow))

        then: "augmentation is successful and signature is LTA"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("signaturesCount", is(1))
                .body("signatures[0].signatureFormat", matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA))
                .body("signatures[0].indication", is("TOTAL-PASSED"))
                .body("signatures[0].warnings", hasSize(0))
    }

    def "Augmenting uploaded container with #description is #result"() {
        given: "upload container with signatures"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(dataFile))

        when: "augment container in session"
        datafile.augmentContainer(flow)

        then: "augmentation is successful and containing signatures have LTA profile"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("signatures.signatureFormat", everyItem(matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA)))
                .body("signaturesCount", is(validSignatures))
                .body("signatures.indication", everyItem(is("TOTAL-PASSED")))
                .body("signatures.warnings", everyItem(hasSize(0)))

        where:
        description              | dataFile                                 || validSignatures | result
        "multiple LT signatures" | "TEST_ESTEID2018_ASiC-E_XAdES_LT+LT.sce" || 2               | "allowed"
    }
}
