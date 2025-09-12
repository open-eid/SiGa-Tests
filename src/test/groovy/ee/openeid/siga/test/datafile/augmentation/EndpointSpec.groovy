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
@Epic("Augmentation")
@Feature("Augmentation endpoint checks")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Augmentation HTTP method check")
    def "Augmentation with method #method is #result"() {
        given:
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerSingleSignatureValidUntil-2026-01-22.asice"))

        when:
        Response response = datafileRequests.augmentationContainerRequest(flow, method).request(method)

        then:
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

    @Story("Augmentation not supported in hashcode")
    def "Augmentation endpoint not allowed in hashcode mode"() {
        given: "upload container for augmentation"
        hashcode.uploadDefaultContainer(flow)

        when: "try augment request through hashcode endpoint"
        Response response = hashcodeRequests.augmentationContainerRequest(flow, Method.PUT).put()

        then: "status code is 404"
        response.then().statusCode(HttpStatus.SC_NOT_FOUND)
    }
}
