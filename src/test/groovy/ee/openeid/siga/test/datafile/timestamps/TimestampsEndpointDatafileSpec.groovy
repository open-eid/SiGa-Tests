package ee.openeid.siga.test.datafile.timestamps

import ee.openeid.siga.test.GenericSpecification
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
@Feature("Timestamps")
class TimestampsEndpointDatafileSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Timestamps endpoint checks")
    def "Timestamps with method #method is #result"() {
        given: "upload container for retrieving the timestamps"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("asicsContainerWithDdocAndTimestamp.asics"))

        when: "make HTTP request"
        Response response = datafileRequests.getTimestampListRequest(flow, method).request(method)

        then: "status code is correct"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.HEAD    || HttpStatus.SC_OK                 | "allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }
}
