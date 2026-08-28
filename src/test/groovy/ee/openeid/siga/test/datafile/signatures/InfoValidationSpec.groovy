package ee.openeid.siga.test.datafile.signatures

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.Utils
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals
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
    def "Signed #containerType returns signer info"() {
        given: "upload signed container and get signature ID"
        datafile.uploadContainerFromFile(flow, containerName)
        String signatureId = datafile.getSignatureList(flow).path("signatures[0].generatedSignatureId")

        when: "get signer info"
        Response actualSignerInfo = datafile.getSignatureInfo(flow, signatureId)

        then: "expected and actual signer info match"
        String expectedSignerInfo = new String(Utils.readFileFromResources("${containerName}_SignerInfo.json"))
        assertJsonEquals(expectedSignerInfo, actualSignerInfo.asString())

        where:
        containerType                   | containerName
        "ASiC-E"                        | TestData.DEFAULT_ASICE_CONTAINER_NAME
        "BDOC"                          | "valid-bdoc-tm-newer.bdoc"
        "ASiC-S"                        | "asicsContainerWithLtSignatureWithoutTST.scs"
        "ASiC-E with invalid signature" | "unknownOcspResponder.asice"
    }

    @Story("Signed container returns signer info")
    def "Created and remotely signed container returns signer info"() {
        given: "create container, sign remotely and get generated signature ID"
        datafile.createDefaultContainer(flow)
        String signatureId = datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

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
        String signatureId = datafile.remoteSigning(flow, signingRequest)

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

}
