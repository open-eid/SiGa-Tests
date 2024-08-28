package ee.openeid.siga.test.hashcode.mid

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.restassured.response.Response
import org.hamcrest.Matchers
import spock.lang.Tag

@Tag("mobileId")
@Epic("Hashcode")
@Feature("Mobile ID signing")
class MobileSigningHashcodeSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "MID sign new container successful"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))

        when:
        hashcode.midSigning(flow, "60001019906", "+37200000766")

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
    }

    def "MID sign existing container successful"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBody(TestData.DEFAULT_HASHCODE_CONTAINER))

        when:
        hashcode.midSigning(flow, "60001019906", "+37200000766")

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", Matchers.is(2))
    }

    def "MID sign successful with following user: #userDescription"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBody(TestData.DEFAULT_HASHCODE_CONTAINER))

        when:
        hashcode.midSigning(flow, personId, phoneNo)

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", Matchers.is(2))

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
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))

        when:
        hashcode.midSigning(flow, "60001019906", "+37200000766")
        hashcode.midSigning(flow, "60001018800", "+37200000566")

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", Matchers.is(2))
    }

    def "MID singing with one valid and second #description: validSignaturesCount = #validSignaturesCount"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))

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

        validationResponse.then().body("validationConclusion.validSignaturesCount", Matchers.is(validSignaturesCount))
        if (validSignaturesCount == 1) {
            validationResponse.then().body("validationConclusion.signatures[0].subjectDistinguishedName.serialNumber",
                    Matchers.is("60001019906"))
        }

        where:
        description         | personId      | phoneNo        | validSignaturesCount
        "valid signature"   | "60001018800" | "+37200000566" | 2
        "invalid signature" | "60001019961" | "+37200000666" | 1
    }

    def "MID signing fails if #userDescription"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))

        when:
        hashcode.midSigning(flow, personId, phoneNo)

        then:
        flow.getMidStatus().then()
                .body("midStatus", Matchers.is(errorCode))

        where:
        userDescription                                  | personId      | phoneNo        | errorCode
        "sending authentication request to phone failed" | "60001019947" | "+37207110066" | TestData.SENDING_ERROR
        "user cancelled authentication"                  | "60001019950" | "+37201100266" | TestData.USER_CANCEL
        "created signature is not valid"                 | "60001019961" | "+37200000666" | TestData.NOT_VALID
        "SIM application error"                          | "60001019972" | "+37201200266" | TestData.SENDING_ERROR
        "phone is not in coverage area"                  | "60001019983" | "+37213100266" | TestData.PHONE_ABSENT
        "user does not react"                            | "50001018908" | "+37066000266" | TestData.EXPIRED_TRANSACTION
    }

    def "MID signing fails with invalid input: #inputDescription"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))

        when:
        Response response = hashcode.tryStartMidSigning(flow,
                RequestData.midSigningRequestBodyMinimal(personId, phoneNo))

        then:
        RequestErrorValidator.validate(response, expectedError)

        where:
        inputDescription                     | personId         | phoneNo        | expectedError
        "Invalid international calling code" | "60001019906"    | "+37100000766" | RequestError.INVALID_CALLING_CODE
        "Person identifier missing"          | ""               | "+37200000766" | RequestError.INVALID_PERSON_ID
        "Invalid person identifier"          | "P!NO-23a.31,23" | "+37200000766" | RequestError.INVALID_PERSON_ID
        "Phone number missing"               | "60001019906"    | ""             | RequestError.INVALID_PHONE
        "Phone number invalid"               | "60001019906"    | "-/ssasa"      | RequestError.INVALID_PHONE
    }

    def "MID signing if #description, then one signature is present"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))

        when:
        // User fails to sign
        hashcode.midSigning(flow, personId, phone)
        // User signs successfully
        hashcode.midSigning(flow, "60001019906", "+37200000766")

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))

        where:
        description                          | personId      | phone
        "one user cancels and second signs"  | "60001019950" | "+37201100266"
        "one user timeouts and second signs" | "50001018908" | "+37066000266"
    }
}
