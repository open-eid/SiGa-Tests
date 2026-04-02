package ee.openeid.siga.test.datafile.midSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.helper.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("datafileContainer")
@Tag("mobileId")
@Epic("Mobile-ID signing (datafile)")
@Feature("MID request validation")
class RequestSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("MID signing messageToDisplay parameter rules")
    def "MID start signing with messageToDisplay as #description is #result"() {
        given:
        datafile.createDefaultContainer(flow)
        Map startMidSigningRequestBody = RequestData.midStartSigningRequestDefaultBody()

        when:
        startMidSigningRequestBody["messageToDisplay"] = value
        Response startMidSigningResponse = datafile.tryStartMidSigning(flow, startMidSigningRequestBody)

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

    @Story("Deleting container cancels ongoing Mobile-ID signing process")
    def "Ongoing Mobile-ID signing cannot continue after container is deleted"() {
        given: "upload the container and start signing"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(TestData.DEFAULT_ASICE_CONTAINER_NAME))
        Response startResponse = datafile.tryStartMidSigning(flow, RequestData.midStartSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        and: "get status"
        datafile.getMidSigningStatus(flow, signatureId)

        when: "delete the container"
        datafile.deleteContainer(flow)

        and: "request status again"
        Response response = datafile.tryGetMidSigningStatus(flow, signatureId)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)
    }

}
