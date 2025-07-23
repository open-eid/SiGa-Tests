package ee.openeid.siga.test.datafile.signatures

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.notNullValue
import static org.hamcrest.Matchers.hasSize

@Tag("datafileContainer")
@Epic("Signatures (datafile)")
@Feature("Get signature list validation")
class ListValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Signed container returns signature list")
    def "Signed ASiC-E returns signature list"() {
        given: "upload signed container"
        datafile.uploadContainerFromFile(flow, TestData.DEFAULT_ASICE_CONTAINER_NAME)

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", equalTo("id-8c2a30729f251c6cb8336844b97f0657"),
                        "signatures[0].signerInfo", equalTo("SERIALNUMBER=11404176865, GIVENNAME=MÄRÜ-LÖÖZ, SURNAME=ŽÕRINÜWŠKY, CN=\"ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865\", OU=digital signature, O=ESTEID, C=EE"),
                        "signatures[0].signatureProfile", equalTo("LT"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Signed container returns signature list")
    def "Signed ASiC-S returns signature list"() {
        given: "upload signed container"
        datafile.uploadContainerFromFile(flow, "asicsContainerWithLtSignatureWithoutTST.scs")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", equalTo("id-42f7f6960f18344d433c5578313b43e2"),
                        "signatures[0].signerInfo", equalTo("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatures[0].signatureProfile", equalTo("LT"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Signed container returns signature list")
    def "Signed container with invalid signature returns signature list"() {
        given: "upload container with invalid signature"
        datafile.uploadContainerFromFile(flow, "unknownOcspResponder.asice")

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", equalTo("S0"),
                        "signatures[0].signerInfo", equalTo("SERIALNUMBER=37101010021, GIVENNAME=IGOR, SURNAME=ŽAIKOVSKI, CN=\"ŽAIKOVSKI,IGOR,37101010021\", OU=digital signature, O=ESTEID (DIGI-ID), C=EE"),
                        "signatures[0].signatureProfile", equalTo("B_EPES"),
                        "signatures[0].generatedSignatureId", notNullValue())
    }

    @Story("Signed container returns signature list")
    def "Created signed container returns signature list"() {
        given: "create and sign a container"
        datafile.createDefaultContainer(flow)
        datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        when: "get signatures list"
        Response signatures = datafile.getSignatureList(flow)

        then: "signature list is returned"
        signatures.then()
                .body("signatures[0].id", notNullValue(),
                        "signatures[0].signerInfo", equalTo("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatures[0].signatureProfile", equalTo("LT"),
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

}
