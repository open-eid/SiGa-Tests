package ee.openeid.siga.test.datafile.signatures

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue

@Tag("datafileContainer")
@Epic("Signatures (datafile)")
@Feature("Get signer info validation")
class InfoValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Signed container returns signer info")
    def "Signed ASiC-E returns signer info"() {
        given: "upload signed container and get signature ID"
        datafile.uploadContainerFromFile(flow, TestData.DEFAULT_ASICE_CONTAINER_NAME)
        String signatureId = datafile.getSignatureList(flow).path("signatures[0].generatedSignatureId")

        when: "get signer info"
        Response response = datafile.getSignatureInfo(flow, signatureId)

        then: "signer info is returned"
        response.then()
                .body("id", is("id-8c2a30729f251c6cb8336844b97f0657"),
                        "signerInfo", is("SERIALNUMBER=11404176865, GIVENNAME=MÄRÜ-LÖÖZ, SURNAME=ŽÕRINÜWŠKY, CN=\"ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865\", OU=digital signature, O=ESTEID, C=EE"),
                        "signatureProfile", is("LT"),
                        "ocspResponseCreationTime", is("2018-11-23T12:24:05Z"),
                        "timeStampCreationTime", is("2018-11-23T12:24:04Z"),
                        "trustedSigningTime", is("2018-11-23T12:24:04Z"),
                        "claimedSigningTime", is("2018-11-23T12:24:04Z"))
    }

    @Story("Signed container returns signer info")
    def "Signed ASiC-S returns signer info"() {
        given: "upload signed container and get signature ID"
        datafile.uploadContainerFromFile(flow, "asicsContainerWithLtSignatureWithoutTST.scs")
        String signatureId = datafile.getSignatureList(flow).path("signatures[0].generatedSignatureId")

        when: "get signer info"
        Response response = datafile.getSignatureInfo(flow, signatureId)

        then: "signer info is returned"
        response.then()
                .body("id", is("id-42f7f6960f18344d433c5578313b43e2"),
                        "signerInfo", is("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatureProfile", is("LT"),
                        "ocspResponseCreationTime", is("2024-09-11T10:20:32Z"),
                        "timeStampCreationTime", is("2024-09-11T10:20:32Z"),
                        "trustedSigningTime", is("2024-09-11T10:20:32Z"),
                        "claimedSigningTime", is("2024-09-11T10:20:31Z"))
    }

    @Story("Signed container returns signer info")
    def "Created and remotely signed container returns signer info"() {
        given: "create container, sign remotely and get generated signature ID"
        datafile.createDefaultContainer(flow)
        datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())
        String signatureId = datafile.getSignatureList(flow).path("signatures[0].generatedSignatureId")

        when: "get signer info"
        Response response = datafile.getSignatureInfo(flow, signatureId)

        then: "signer info is returned"
        response.then()
                .body("id", notNullValue(),
                        "signerInfo", is("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatureProfile", is("LT"),
                        "signingCertificate", is(TestData.SIGNER_CERT_ESTEID2018_PEM),
                        "ocspCertificate", notNullValue(),
                        "timeStampTokenCertificate", notNullValue(),
                        "ocspResponseCreationTime", notNullValue(),
                        "timeStampCreationTime", notNullValue(),
                        "trustedSigningTime", notNullValue(),
                        "claimedSigningTime", notNullValue())
    }

    @Story("Signer info includes roles and signature production place")
    def "Created and remotely signed container returns signer info set in signing request"() {
        given: "create container, sign remotely and set info (e.g role and production place)"
        datafile.createDefaultContainer(flow)
        Map signingRequest = RequestData.remoteSigningStartDefaultRequest() + [
                "roles"                   : ["Member of board"],
                "signatureProductionPlace": ["countryName"    : "Estonia",
                                             "city"           : "Tallinn",
                                             "stateOrProvince": "Harju",
                                             "postalCode"     : "4953"]]
        datafile.remoteSigning(flow, signingRequest)
        String signatureId = datafile.getSignatureList(flow).path("signatures[0].generatedSignatureId")

        when: "get signer info"
        Response response = datafile.getSignatureInfo(flow, signatureId)

        then: "signer info contains set info"
        response.then()
                .body("id", notNullValue(),
                        "signerInfo", is("SERIALNUMBER=PNOEE-38001085718, CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\", SURNAME=JÕEORG, GIVENNAME=JAAK-KRISTJAN, C=EE"),
                        "signatureProfile", is("LT"),
                        "roles[0]", is("Member of board"),
                        "signatureProductionPlace.countryName", is("Estonia"),
                        "signatureProductionPlace.city", is("Tallinn"),
                        "signatureProductionPlace.stateOrProvince", is("Harju"),
                        "signatureProductionPlace.postalCode", is("4953"))
    }

    @Story("Signed container returns signer info")
    def "Signed container with invalid signature returns signer info"() {
        given: "upload signed container and get signature ID"
        datafile.uploadContainerFromFile(flow, "unknownOcspResponder.asice")
        String signatureId = datafile.getSignatureList(flow).path("signatures[0].generatedSignatureId")

        when: "get signer info"
        Response response = datafile.getSignatureInfo(flow, signatureId)

        then: "signer info is returned"
        response.then()
                .body("id", is("S0"),
                        "signerInfo", is("SERIALNUMBER=37101010021, GIVENNAME=IGOR, SURNAME=ŽAIKOVSKI, CN=\"ŽAIKOVSKI,IGOR,37101010021\", OU=digital signature, O=ESTEID (DIGI-ID), C=EE"),
                        "signatureProfile", is("B_EPES"))
    }

}