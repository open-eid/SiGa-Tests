package ee.openeid.siga.test.hashcode.datafiles

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus

@Epic("Get-add-delete data files (hashcode)")
@Feature("Datafiles endpoints checks")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Get-Add data files endpoint HTTP method check")
    def "Get-Add data files endpoint with method #method is #result"() {
        given: "upload container"
        hashcode.uploadContainerFromFile(flow, "hashcodeWithoutSignature.asice")

        when: "make /datafiles endpoint request with HTTP method"
        Response response

        if (Method.POST) { //POST is used to add datafile to a container
            Map requestBody = RequestData.addDatafileRequestBody([TestData.defaultHashcodeDataFile()])
            response = hashcodeRequests.addDataFilesRequest(flow, method, requestBody).request(method)
        } else {
            response = hashcodeRequests.getDataFilesRequest(flow, method).request(method)
        }

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.POST    || HttpStatus.SC_OK                 | "allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

    @Story("Delete data files endpoint HTTP method check")
    def "Delete data files endpoint with method #method is #result"() {
        given: "create default container"
        hashcode.createDefaultContainer(flow)

        when: "delete data file endpoint request with HTTP method"
        String dataFileName = TestData.defaultHashcodeDataFile().fileName
        Response response = hashcodeRequests.deleteDataFileRequest(flow, method, dataFileName).request(method)

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.DELETE  || HttpStatus.SC_OK                 | "allowed"
        Method.GET     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

}
