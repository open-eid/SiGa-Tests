package ee.openeid.siga.test.hashcode.mid

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.ErrorCode
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.restassured.response.Response
import spock.lang.Tag

import static ee.openeid.siga.test.util.EnumNameMatcher.matchesEnumName
import static org.hamcrest.Matchers.is

@Tag("mobileId")
@Epic("Hashcode")
@Feature("Mobile ID signing")
class MidSigningHashcodeSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "MID sign new container successful"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        hashcode.midSigning(flow, "60001019906", "+37200000766")

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", is(1))
    }

    def "MID sign successful with following user: #userDescription"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBody(TestData.DEFAULT_HASHCODE_CONTAINER))

        when:
        hashcode.midSigning(flow, personId, phoneNo)

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", is(2))

        where:
        userDescription                                                | personId      | phoneNo
        "Certificate issued under TEST of SK ID Solutions EID-Q 2021E" | "51307149560" | "+37269930366"
        "certificate issued under TEST of EID-SK 2016"                 | "60001017869" | "+37268000769"
        "certificate issued under TEST of ESTEID-SK 2015"              | "60001019906" | "+37200000766"
        "1 pair of RSA certificates"                                   | "39901019992" | "+37200001566"
        "MID user is over 21 years old"                                | "45001019980" | "+37200001466"
        "MID user is under 18 years old"                               | "61001019985" | "+37200001366"
    }

    def "MID sign per container with multiple users successful"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        hashcode.midSigning(flow, "60001019906", "+37200000766")
        hashcode.midSigning(flow, "60001018800", "+37200000566")

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", is(2))
    }

    def "MID singing with one valid and second #description: validSignaturesCount = #validSignaturesCount"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        //Valid signature
        Response responseValidSignature = hashcode.startMidSigning(flow,
                RequestData.midSigningRequestBodyMinimal("60001019906", "+37200000766"))
        //Valid or invalid signature
        Response responseSignature2 = hashcode.startMidSigning(flow,
                RequestData.midSigningRequestBodyMinimal(personId, phoneNo))

        hashcode.pollForMidSigningStatus(flow, responseValidSignature.path("generatedSignatureId"))
        hashcode.pollForMidSigningStatus(flow, responseSignature2.path("generatedSignatureId"))

        then:
        Response validationResponse = hashcode.validateContainerInSession(flow)

        validationResponse.then().body("validationConclusion.validSignaturesCount", is(validSignaturesCount))
        if (validSignaturesCount == 1) {
            validationResponse.then().body("validationConclusion.signatures[0].subjectDistinguishedName.serialNumber",
                    is("60001019906"))
        }

        where:
        description         | personId      | phoneNo        | validSignaturesCount
        "valid signature"   | "60001018800" | "+37200000566" | 2
        "invalid signature" | "60001019961" | "+37200000666" | 1
    }

    def "MID signing fails if #userDescription"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        hashcode.midSigning(flow, personId, phoneNo)

        then:
        flow.getMidStatus().then()
                .body("midStatus", matchesEnumName(errorCode))

        where:
        userDescription                                  | personId      | phoneNo        | errorCode
        "sending authentication request to phone failed" | "60001019947" | "+37207110066" | ErrorCode.SENDING_ERROR
        "user cancelled authentication"                  | "60001019950" | "+37201100266" | ErrorCode.USER_CANCEL
        "created signature is not valid"                 | "60001019961" | "+37200000666" | ErrorCode.NOT_VALID
        "SIM application error"                          | "60001019972" | "+37201200266" | ErrorCode.SENDING_ERROR
        "phone is not in coverage area"                  | "60001019983" | "+37213100266" | ErrorCode.PHONE_ABSENT
        "user does not react"                            | "50001018908" | "+37066000266" | ErrorCode.EXPIRED_TRANSACTION
    }

    def "MID signing if #description, then one signature is present"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        // User fails to sign
        hashcode.midSigning(flow, personId, phone)
        // User signs successfully
        hashcode.midSigning(flow, "60001019906", "+37200000766")

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.signaturesCount", is(1))
                .body("validationConclusion.validSignaturesCount", is(1))

        where:
        description                          | personId      | phone
        "one user cancels and second signs"  | "60001019950" | "+37201100266"
        "one user timeouts and second signs" | "50001018908" | "+37066000266"
    }

    def "MID signing not allowed if datafile #datafileAction after signing started"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile("hashcodeWithoutSignature.asice"))
        Response startResponse = hashcode.startMidSigning(flow, RequestData.midSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        when:
        switch (datafileAction) {
            case "added" -> hashcode.addDataFiles(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))
            case "deleted" -> hashcode.deleteDataFile(flow, hashcode.getDataFilesList(flow).path("dataFiles[0].fileName"))
        }
        hashcode.pollForMidSigningStatus(flow, signatureId)

        then:
        RequestErrorValidator.validate(flow.getMidStatus(), RequestError.INVALID_CHANGED_DATAFILE)

        where:
        datafileAction | _
        "added"        | _
        "deleted"      | _
    }
}
