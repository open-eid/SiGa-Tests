package ee.openeid.siga.test.hashcode.mid

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.CommonErrorCode
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Link
import io.restassured.response.Response
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Tag

import static org.hamcrest.Matchers.is

@Tag("mobileId")
@Epic("Hashcode")
@Feature("Mobile ID request validation")
class MidRequestHashcodeSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "MID signing request not allowed with invalid input: #description"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))
        Map signingRequestBody = RequestData.midSigningRequestBodyDefault()

        when:
        signingRequestBody.put(property, value)
        Response response = hashcode.tryStartMidSigning(flow, signingRequestBody)

        then:
        RequestErrorValidator.validate(response, expectedError)

        where:
        description                          | property           | value            | expectedError
        "Invalid phone number"               | "phoneNo"          | "-/ssasa"        | RequestError.INVALID_PHONE
        "Invalid international calling code" | "phoneNo"          | "+37100000766"   | RequestError.INVALID_CALLING_CODE
        "Phone number missing"               | "phoneNo"          | ""               | RequestError.INVALID_PHONE

        "Person identifier missing"          | "personIdentifier" | ""               | RequestError.INVALID_PERSON_ID
        "Invalid person identifier"          | "personIdentifier" | "P!NO-23a.31,23" | RequestError.INVALID_PERSON_ID

        "Language missing"                   | "language"         | ""               | RequestError.INVALID_LANGUAGE
        "Invalid language"                   | "language"         | "SOM"            | RequestError.INVALID_LANGUAGE
    }

    @Link("https://jira.ria.ee/browse/SIGA-915")
    def "MID signing request not allowed with invalid role"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))
        Map signingRequestBody = RequestData.midSigningRequestBodyDefault()

        when:
        signingRequestBody.put("roles", "")
        Response response = hashcode.tryStartMidSigning(flow, signingRequestBody)

        then:
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errorCode", is(CommonErrorCode.INVALID_REQUEST))
    }

    def "MID signing request not allowed with invalid profile: #profile"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))
        Map signingRequestBody = RequestData.midSigningRequestBodyDefault()

        when:
        signingRequestBody.put("signatureProfile", profile)
        Response response = hashcode.tryStartMidSigning(flow, signingRequestBody)

        then:
        RequestErrorValidator.validate(response, RequestError.INVALID_PROFILE)

        where:
        profile << ["", " ", "123", "@!*", "UNKNOWN", "B_BES", "B_EPES", "LT_TM", "lt_TM", "lt_tm", "LT-TM", "LT TM"]
    }

    def "MID signing not allowed with empty datafile in container"() {
        given:
        hashcode.uploadContainer(flow,
                RequestData.hashcodeRequestBodyFromFile("hashcodeUnsignedContainerWithEmptyDatafiles.asice"))

        when:
        Response response = hashcode.tryStartMidSigning(flow, RequestData.midSigningRequestBodyDefault())

        then:
        RequestErrorValidator.validate(response, RequestError.INVALID_DATAFILE)
    }

    def "MID signing successful with special char in messageToDisplay parameter"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))
        Map signingRequestBody = RequestData.midSigningRequestBodyDefault()

        when:
        signingRequestBody.put("messageToDisplay", "/ ` ? * \\ < > | \" : \u0017 \u0000 \u0007")
        hashcode.midSigning(flow, signingRequestBody)

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
    }

    def "MID signing successful with all parameters in request"() {
        given:
        hashcode.createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))

        when:
        Map signingRequestBody = RequestData.midSigningRequestBody(
                "60001019906",
                "+37200000766",
                "EST",
                "LT",
                ["Signer", "Signer 2", "Signer 3"],
                "Message to display",
                RequestData.signatureProductionPlace()
        )
        hashcode.midSigning(flow, signingRequestBody)

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
    }
}
