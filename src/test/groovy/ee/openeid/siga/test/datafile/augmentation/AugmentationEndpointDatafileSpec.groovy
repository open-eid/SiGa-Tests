package ee.openeid.siga.test.datafile.augmentation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Datafile")
@Feature("Augmentation")
class AugmentationEndpointDatafileSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Augmentation endpoint checks")
    def "Augmentation with method #method is #result"() {
        given: "upload container for augmentation"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerSingleSignatureValidUntil-2026-01-22.asice"))

        when: "#method request"
        Response response = datafile.getIntance().augmentationContainerRequest(flow, method).request(method)

        then: "status code is #httpStatus"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_OK                 | "allowed"
    }

    @Story("Augmentation endpoint checks")
    def "Augmentation endpoint not allowed in hashcode mode"() {
        given: "upload container for augmentation"
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBody(TestData.DEFAULT_HASHCODE_CONTAINER))

        when: "try augment request through hashcode endpoint"
        Response response = hashcode.getIntance().augmentationContainerRequest(flow, Method.PUT).put()

        then: "status code is #httpStatus and body is present only with PUT method"
        response.then().statusCode(HttpStatus.SC_NOT_FOUND)
    }
}
