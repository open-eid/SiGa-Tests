package ee.openeid.siga.test.datafile.augmentation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.*
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.ContainerUtil
import ee.openeid.siga.test.util.Utils
import eu.europa.esig.dss.enumerations.MimeTypeEnum
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

import static ee.openeid.siga.test.matcher.IsoZonedTimestampMatcher.withinOneHourOfCurrentTime
import static ee.openeid.siga.test.util.EnumNameMatcher.matchesEnumName
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Augmentation")
@Feature("XAdES signature augmentation validation")
class XadesValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Container with LT signatures is extended to LTA in place")
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

    @Story("Container with LT signatures is extended to LTA in place")
    def "Augmenting created container with one LT signature is successful"() {
        given: "create container and remote sign it"
        datafile.createContainer(flow, RequestData.createDatafileRequestDefaultBody())
        datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        when: "augment container in session"
        datafile.augmentContainer(flow)

        then: "augmentation is successful and signature is LTA"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("signaturesCount", is(1))
                .body("signatures[0].signatureFormat", matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA))
                .body("signatures[0].indication", is("TOTAL-PASSED"))
                .body("signatures[0].warnings", hasSize(0))
    }

    @Story("Container with LT signatures is extended to LTA in place")
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

    @Story("Container that cannot be extended in place is wrapped into timestamped ASiC-S")
    def "Augmenting #description wraps container into timestamped ASiC-S"() {
        given: "upload container that cannot be augmented in place"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when: "augment container in session"
        datafile.augmentContainer(flow)

        then: "validation report shows ASiC-S with the original signatures and a new timestamp"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("signatureForm", is("ASiC-S"))
                .body("signaturesCount", is(signaturesCount))
                .body("timeStampTokens", hasSize(1))
                .body("timeStampTokens[0].signedTime", withinOneHourOfCurrentTime())

        and: "returned container is an ASiC-S with the .asics extension"
        Response containerResponse = datafile.getContainer(flow)
        containerResponse.then().body("containerName", is(expectedContainerName))
        String augmentedContainer = containerResponse.path("container").toString()
        String mimeType = new String(ContainerUtil.extractEntryBytesFromBase64Container(augmentedContainer, "mimetype"))
        assertThat(mimeType, is(MimeTypeEnum.ASICS.mimeTypeString))

        and: "the original container is preserved unchanged inside the ASiC-S"
        // TODO: SIGA-897: if the original container is preserved as an exact byte-level copy inside
        //       the resulting ASiC-S, replace this call with a direct byte array comparison
        //       (Arrays.equals) — the zip entry comparison is then no longer needed
        ContainerUtil.assertZipFilesEqualInExactOrder(
                Utils.readFileFromResources(containerName),
                ContainerUtil.extractEntryBytesFromBase64Container(augmentedContainer, containerName))

        where:
        description                                      | containerName                                                      || signaturesCount | expectedContainerName
        "BDOC with LT_TM signature"                      | "valid-bdoc-tm-newer.bdoc"                                         || 1               | "valid-bdoc-tm-newer.asics"
        "ASiC-E with invalid signature"                  | "esteid2018signerAiaOcspLT.asice"                                  || 1               | "esteid2018signerAiaOcspLT.asics"
        "ASiC-E with T and LT level signatures"          | "tAndLtLevelSignatures.asice"                                      || 2               | "tAndLtLevelSignatures.asics"
        "ASiC-E with expired signer and TS certificates" | "containerSingleSignatureWithExpiredSignerAndTsCertificates.asice" || 1               | "containerSingleSignatureWithExpiredSignerAndTsCertificates.asics"
    }

}
