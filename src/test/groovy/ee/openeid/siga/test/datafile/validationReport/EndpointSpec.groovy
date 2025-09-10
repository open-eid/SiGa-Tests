package ee.openeid.siga.test.datafile.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Validation Report (datafile)")
@Feature("Validation report endpoint validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Issue("SIGA-1091")
    @Story("Validate without session HTTP method check")
    def "Validation with method #method is #result"() {
        expect: "try validating container with HTTP method"
        datafileRequests.getValidationReportWithoutSessionRequest(
                flow,
                method,
                RequestData.uploadDatafileRequestDefaultBody())
                .request(method)
                .then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.POST    || HttpStatus.SC_OK                 | "allowed"
//        Method.GET     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
//        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

    @Story("Validate with session HTTP method check")
    def "Validating container in session with method #method is #result"() {
        given: "upload container"
        datafile.uploadDefaultContainer(flow)

        when: "try validating container with HTTP method"
        Response response = datafileRequests.getValidationReportInSessionRequest(flow, method).request(method)

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

}
