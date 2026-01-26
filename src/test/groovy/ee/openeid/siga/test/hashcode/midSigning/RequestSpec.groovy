package ee.openeid.siga.test.hashcode.midSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.*
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

import static org.hamcrest.Matchers.is

@Tag("mobileId")
@Epic("Mobile-ID signing (hashcode)")
@Feature("MID request validation")
class RequestSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "MID signing request not allowed with invalid input: #description"() {
        given:
        hashcode.createDefaultContainer(flow)
        Map signingRequestBody = RequestData.midStartSigningRequestDefaultBody()

        when:
        signingRequestBody[property] = value
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
        hashcode.createDefaultContainer(flow)
        Map signingRequestBody = RequestData.midStartSigningRequestDefaultBody()

        when:
        signingRequestBody["roles"] = ""
        Response response = hashcode.tryStartMidSigning(flow, signingRequestBody)

        then:
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errorCode", is(CommonErrorCode.INVALID_REQUEST))
    }

    def "MID signing not allowed with empty datafile in container"() {
        given:
        hashcode.uploadContainer(flow,
                RequestData.uploadHashcodeRequestBodyFromFile("hashcodeUnsignedContainerWithEmptyDatafiles.asice"))

        when:
        Response response = hashcode.tryStartMidSigning(flow, RequestData.midStartSigningRequestDefaultBody())

        then:
        RequestErrorValidator.validate(response, RequestError.INVALID_EMPTY_DATAFILE)
    }

    @Story("MID signing not allowed with invalid profile")
    def "MID signing request not allowed with invalid profile: #profile"() {
        given: "Upload container"
        hashcode.createDefaultContainer(flow)
        Map signingRequestBody = RequestData.midStartSigningRequestDefaultBody()

        when: "Try signing with invalid profile"
        signingRequestBody["signatureProfile"] = profile
        Response response = hashcode.tryStartMidSigning(flow, signingRequestBody)

        then: "Request validation error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_PROFILE)

        where:
        profile << ["", " ", "123", "@!*", "UNKNOWN", "B_BES", "B_EPES", "LT_TM", "lt_TM", "lt_tm", "LT-TM", "LT TM", "T", "LTA"]
    }

    @Story("MID signing messageToDisplay parameter rules")
    def "MID start signing with messageToDisplay as #description is #result"() {
        given:
        hashcode.createDefaultContainer(flow)
        Map startMidSigningRequestBody = RequestData.midStartSigningRequestDefaultBody()

        when:
        startMidSigningRequestBody["messageToDisplay"] = value
        Response startMidSigningResponse = hashcode.tryStartMidSigning(flow, startMidSigningRequestBody)

        then:
        switch (result) {
            case "allowed":
                startMidSigningResponse.then().statusCode(HttpStatus.SC_OK)
                break
            case "not allowed":
                RequestErrorValidator.validate(startMidSigningResponse, RequestError.INVALID_MID_MESSAGE_LENGTH)
        }

        where:
        description                     | value                                                                                                   || result
        "empty"                         | ""                                                                                                      || "allowed"
        "special chars"                 | "/ ` ? * \\ < > | \" : \u0017 \u0000 \u0007"                                                            || "allowed"
        "GSM-7 extension chars"         | "€[]^|{}\\"                                                                                             || "allowed"
        "length < 100 (ASCII) chars"    | "99 CHARS Auth#123,45 EUR. Ref:INV-2026-01-26; OK? Auth#123,45 EUR. Ref:INV-2026-01-26; OK? Auth#123"   || "allowed"
        "length = 100 (ASCII) chars"    | "100 CHARS Auth#123,45 EUR. Ref:INV-2026-01-26; OK? Auth#123,45 EUR. Ref:INV-2026-01-26; OK? Auth#123"  || "allowed"
        "length > 100 (ASCII) chars"    | "101 CHARS Auth#123,45 EUR. Ref:INV-2026-01-26; OK? Auth#123,45 EUR. Ref:INV-2026-01-26; OK? Auth#123," || "not allowed"
        "length < 100 (Cyrillic) chars" | "99 CHARS Подтвердите#1,23. Код:2026-01-26; ОК? Подтвердите#1,23. Код:2026-01-26; ОК? Подтвердите#1,"   || "allowed"
        "length = 100 (Cyrillic) chars" | "100 CHARS Подтвердите#1,23. Код:2026-01-26; ОК? Подтвердите#1,23. Код:2026-01-26; ОК? Подтвердите#1,"  || "allowed"
        "length > 100 (Cyrillic) chars" | "101 CHARS Подтвердите#1,23. Код:2026-01-26; ОК? Подтвердите#1,23. Код:2026-01-26; ОК? Подтвердите#1,1" || "not allowed"
    }

    def "MID signing successful with all parameters in request"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        Map signingRequestBody = RequestData.midStartSigningRequestBody(
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
                .body("validationConclusion.validSignaturesCount", is(1))
    }
}
