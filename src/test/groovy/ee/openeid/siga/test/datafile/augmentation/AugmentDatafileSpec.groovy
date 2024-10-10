package ee.openeid.siga.test.datafile.augmentation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.SignatureFormat
import ee.openeid.siga.test.model.SignatureLevel
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.hamcrest.Matchers
import spock.lang.Tag

import static ee.openeid.siga.test.util.EnumNameMatcher.matchesEnumName

@Tag("datafileContainer")
@Epic("Datafile")
@Feature("Augmentation")
class AugmentDatafileSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Augmentation of ASIC-E container")
    def "Augmenting uploaded container with one LT signature is successful"() {
        given: "upload container with single LT signature for augmentation"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerSingleSignatureValidUntil-2026-01-22.asice"))

        when: "augment container in session"
        datafile.augmentContainer(flow)

        then: "augmentation is successful and signature is LTA"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings.content", Matchers.hasItem(TestData.TEST_ENV_VALIDATION_WARNING))

                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("JÕEORG,JAAK-KRISTJAN,38001085718"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("PNOEE-38001085718"))
                .body("signatures[0].signatureFormat", matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA))
                .body("signatures[0].signatureLevel", matchesEnumName(SignatureLevel.QESIG))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].claimedSigningTime", Matchers.is("2024-05-28T07:23:00Z"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2024-05-28T07:23:04Z"))
    }

    @Story("Augmentation of ASIC-E container")
    def "Augmenting created container with one LT signature is successful"() {
        given: "create container and remote sign it"
        datafile.createContainer(flow, RequestData.createDatafileRequestDefaultBody())
        datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        when: "augment container in session"
        datafile.augmentContainer(flow)

        then: "augmentation is successful and signature is LTA"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("signaturesCount", Matchers.is(1))
                .body("signatures[0].signatureFormat", matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
    }

    @Story("Augmentation of ASIC-E container")
    def "Augmenting uploaded container with #description is #result"() {
        given: "upload container with single LT signature for augmentation"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(dataFile))

        when: "augment container in session"
        datafile.augmentContainer(flow)

        then: "augmentation is successful and signature is LTA"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("signatures.signatureFormat", Matchers.everyItem(matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA)))
                .body("signaturesCount", Matchers.is(validSignatures))
                .body("signatures.indication", Matchers.everyItem(Matchers.is("TOTAL-PASSED")))
                .body("signatures.warnings", Matchers.everyItem(Matchers.hasSize(0)))

        where:
        description              | dataFile                                 | validSignatures || result
        "multiple LT signatures" | "TEST_ESTEID2018_ASiC-E_XAdES_LT+LT.sce" | 2               || "allowed"
    }
}
