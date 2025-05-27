package ee.openeid.siga.test.hashcode.validation

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

import static ee.openeid.siga.test.helper.TestData.DEFAULT_HASHCODE_CONTAINER

@Epic("Hashcode")
@Feature("Validate hashcode container")
@Story("Validation endpoint checks")
class HashcodeValidationEndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Tag("SIGA-1091 - only POST should return 200")
    def "Validation with method #method is #result"() {
        when:
        Response response = hashcodeRequests.getValidationReportWithoutSessionRequest(flow, method, RequestData.uploadHashcodeRequestBody(DEFAULT_HASHCODE_CONTAINER)).request(method)

        then:
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_BAD_REQUEST        | "not allowed"
        Method.HEAD    || HttpStatus.SC_BAD_REQUEST        | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_OK                 | "allowed" //SIGA handles this as DELETE to containerId
        Method.POST    || HttpStatus.SC_OK                 | "allowed"
    }

    @Tag("SIGA-1091 - only GET should return 200")
    def "Validating container in session with method #method is #result"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBody(DEFAULT_HASHCODE_CONTAINER))

        when:
        Response response = hashcodeRequests.getValidationReportInSessionRequest(flow, method).request(method)

        then:
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.HEAD    || HttpStatus.SC_OK                 | "allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
    }

}
