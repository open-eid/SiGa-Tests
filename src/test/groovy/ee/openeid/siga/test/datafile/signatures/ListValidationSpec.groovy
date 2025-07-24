package ee.openeid.siga.test.datafile.signatures

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Ignore
import spock.lang.Tag

import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Signatures (datafile)")
@Feature("Get signature list validation")
class ListValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Signed container returns signatures")
    def "Signed ASiC-E returns signatures"() {
        given: "upload signed container"
        datafile.uploadContainerFromFile(flow, TestData.DEFAULT_ASICE_CONTAINER_NAME)

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", is("id-8c2a30729f251c6cb8336844b97f0657"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=11404176865, GIVENNAME=MÄRÜ-LÖÖZ, SURNAME=ŽÕRINÜWŠKY, CN=\"ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865\", OU=digital signature, O=ESTEID, C=EE"),
                        "signatures[0].signatureProfile", is("LT"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Signed container returns signatures")
    def "Signed BDOC returns signatures"() {
        given: "upload signed container"
        datafile.uploadContainerFromFile(flow, "valid-bdoc-tm-newer.bdoc")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", is("id-14077f4aa96f37fe59f65fbfcde8ea12"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=60001016970, GIVENNAME=MARY ÄNN, SURNAME=O’CONNEŽ-ŠUSLIK TESTNUMBER, CN=\"O’CONNEŽ-ŠUSLIK TESTNUMBER,MARY ÄNN,60001016970\", C=EE"),
                        "signatures[0].signatureProfile", is("LT_TM"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Signed container returns signatures")
    def "Signed ASiC-S returns signatures"() {
        given: "upload signed container"
        datafile.uploadContainerFromFile(flow, "asicsContainerWithLtSignatureWithoutTST.scs")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", is("id-42f7f6960f18344d433c5578313b43e2"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatures[0].signatureProfile", is("LT"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Signed container returns signatures")
    def "Signed container with multiple signatures returns all signatures"() {
        given: "upload signed container with multiple signatures"
        datafile.uploadContainerFromFile(flow, "3_signatures_TM_LT_LTA.bdoc")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "returned signature list contains all signatures"
        signatures.then()
                .body("signatures", hasSize(3))
                .body("signatures.id", containsInAnyOrder("id-48fa65512029c78c7f0148cc8dca387b",
                        "id-6b582d557c92dee4d77c3dbab3f72386",
                        "id-3fb7e808dc48f74d999d35a57c4d076f"))
                .body("signatures.signatureProfile", containsInAnyOrder("LT_TM", "LT", "LTA"))
    }

    @Story("Signed container returns signatures")
    def "Signed container with digital e-seal returns the e-seal in signature list"() {
        given: "upload signed container with e-seal"
        datafile.uploadContainerFromFile(flow, "LT_sig_and_LT_seal.asice")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "returned signature list contains e-seal"
        signatures.then()
                .body("signatures.id", hasItem("id-85fbac987cb67560513b2f3c47e88f34"),
                        "signatures.signerInfo", hasItem("CN=Nortal QSCD Test Seal, OU=QA, O=Nortal AS, C=EE, L=Tallinn, ST=Harjumaa, SERIALNUMBER=10391131, OID.2.5.4.97=NTREE-10391131"))
    }

    @Story("Signed container returns signatures")
    def "Signed container with invalid signature returns the signature"() {
        given: "upload container with invalid signature"
        datafile.uploadContainerFromFile(flow, "unknownOcspResponder.asice")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", is("S0"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=37101010021, GIVENNAME=IGOR, SURNAME=ŽAIKOVSKI, CN=\"ŽAIKOVSKI,IGOR,37101010021\", OU=digital signature, O=ESTEID (DIGI-ID), C=EE"),
                        "signatures[0].signatureProfile", is("B_EPES"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Signed container returns signatures")
    def "Created and signed container returns signatures"() {
        given: "create and sign a container"
        datafile.createDefaultContainer(flow)
        datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", notNullValue(),
                        "signatures[0].signerInfo", is("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatures[0].signatureProfile", is("LT"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Unsigned container returns empty signature list")
    def "Unsigned #containerType returns empty signature list"() {
        given: "upload unsigned container"
        datafile.uploadContainerFromFile(flow, fileName)

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then().body("signatures", hasSize(0))

        where:
        containerType          | fileName
        "ASiC-E"               | "containerWithoutSignatures.asice"
        "BDOC"                 | "bdocWithoutSignature.bdoc"
        "ASiC-S"               | "0xSIG_0xTST_asics.asics"
        "ASiC-S (timestamped)" | TestData.DEFAULT_ASICS_CONTAINER_NAME
    }

    @Issue("SIGA-1123")
    @Ignore
    @Story("Timestamped composite ASiC-S returns nested container signatures")
    def "Timestamped composite ASiC-S returns nested DDOC container signatures"() {
        given: "upload composite ASiC-S with DDOC"
        datafile.uploadContainerFromFile(flow, "timestampedAsicsWithDdocAndNoManifest.asics")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "inner DDOC signature list is returned"
        signatures.then()
                .body("signatures[0].id", is("S0"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=14212128025, CN=\"TESTNUMBER,SEITSMES,14212128025\", SURNAME=TESTNUMBER, GIVENNAME=SEITSMES, C=EE"),
                        "signatures[0].signatureProfile", is("DIGIDOC_XML_1.3"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Issue("SIGA-1123")
    @Ignore
    @Story("Timestamped composite ASiC-S returns nested container signatures")
    def "Timestamped composite ASiC-S returns nested BDOC container signatures"() {
        given: "upload composite ASiC-S with BDOC"
        datafile.uploadContainerFromFile(flow, "asicsContainerWithBdocAndTimestamp.asics")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "inner BDOC signature list is returned"
        signatures.then()
                .body("signatures[0].id", is("id-14077f4aa96f37fe59f65fbfcde8ea12"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=60001016970, GIVENNAME=MARY ÄNN, SURNAME=O’CONNEŽ-ŠUSLIK TESTNUMBER, CN=\"O’CONNEŽ-ŠUSLIK TESTNUMBER,MARY ÄNN,60001016970\", C=EE"),
                        "signatures[0].signatureProfile", is("LT_TM"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Issue("SIGA-1123")
    @Ignore
    @Story("Timestamped composite ASiC-S returns nested container signatures")
    def "Timestamped composite ASiC-S returns nested ASiC-E container signatures"() {
        given: "upload composite ASiC-S with ASiC-E"
        datafile.uploadContainerFromFile(flow, "timestampedAsicsWithAsice.asics")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "inner ASiC-E signature list is returned"
        signatures.then()
                .body("signatures[0].id", is("id-7b66710af77da83532ae5dfa1d5ad109"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatures[0].signatureProfile", is("LT"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Issue("SIGA-1123")
    @Ignore
    @Story("Timestamped composite ASiC-S returns nested container signatures")
    def "Timestamped composite ASiC-S returns only first-level nested container signatures"() {
        given: "upload timestamped composite container"
        datafile.uploadContainerFromFile(flow, "timestampedAsicsWithSignedAsiceWithSignedAsice.asics")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "only first-level container signature is returned"
        signatures.then()
                .body("signatures[0].id", is("id-001edbf02c3f24ad7783bf1d9437724d"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatures[0].signatureProfile", is("LT"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Timestamped composite ASiC-S returns nested container signatures")
    def "Timestamped composite ASiC-S returns empty signature list when nested signed #innerContainerType"() {
        given: "upload unsigned container"
        datafile.uploadContainerFromFile(flow, containerName)

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then().body("signatures", hasSize(0))

        where:
        innerContainerType | containerName
        "PDF"              | "timestampedAsicsWithSignedPdf.asics"
        "ASiC-S"           | "timestampedAsicsWithSignedAsics.asics"
    }

    @Story("Signed composite ASiC-S returns outer container signatures")
    def "Signed composite ASiC-S returns only outer container signatures"() {
        given: "upload signed  composite container"
        datafile.uploadContainerFromFile(flow, "signedAsicsWithSignedDdoc.scs")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "only outer container signature is returned"
        signatures.then()
                .body("signatures[0].id", is("id-f393ec793e67f6a2864d0ece84ecde7f"),
                        "signatures[0].signerInfo", is("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatures[0].signatureProfile", is("LT"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

}
