package ee.openeid.siga.test.datafile.datafiles

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.Story
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Get-add-delete data files (datafile)")
@Feature("Datafiles endpoints checks")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Issue("SIGA-1091")
    @Story("Get-Add data files endpoint HTTP method check")
    def "Get-Add data files endpoint with method #method is #result"() {
        given: "upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerWithoutSignatures.asice"))

        when: "make /datafiles endpoint request with HTTP method"
        Response response

        if (Method.POST) { //POST is used to add datafile to a container
            Map requestBody = RequestData.addDatafileRequestBody([TestData.defaultDataFile()])
            response = datafileRequests.addDataFilesRequest(flow, method, requestBody).request(method)
        } else {
            response = datafileRequests.getDataFilesRequest(flow, method).request(method)
        }

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.POST    || HttpStatus.SC_OK                 | "allowed"
//        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }
}
