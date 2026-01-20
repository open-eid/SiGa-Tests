package ee.openeid.siga.test.datafile.datafiles

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
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

    @Story("Get-Add data files endpoint HTTP method check")
    def "Get-Add data files endpoint with method #method is #result"() {
        given: "upload container"
        datafile.uploadContainerFromFile(flow, "containerWithoutSignatures.asice")

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
        datafile.createDefaultContainer(flow)

        when: "delete data file endpoint request with HTTP method"
        String dataFileName = TestData.defaultDataFile().fileName
        Response response = datafileRequests.deleteDataFileRequest(flow, method, dataFileName).request(method)

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

    @Story("Adding data file with invalid request body is not allowed")
    def "Trying to add a data file with #description is #result"() {
        given: "upload unsigned container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerWithoutSignatures.asice"))

        when: "try adding data file"
        Response response = datafile.tryAddDataFiles(flow, RequestData.addDatafileRequestBody([dataFile]))

        then: "response is returned"
        if (result == "allowed") {
            response.then().statusCode(HttpStatus.SC_OK)
        } else {
            RequestErrorValidator.validate(response, (RequestError) error)
        }

        where:
        description            | dataFile                                           || result        | error
        "empty list"           | []                                                 || "not allowed" | RequestError.INVALID_JSON
        "invalid name type"    | TestData.defaultDataFile() + ["fileName": true]    || "not allowed" | RequestError.INVALID_JSON
        "invalid content type" | TestData.defaultDataFile() + ["fileContent": 1234] || "not allowed" | RequestError.INVALID_JSON
        "extra field"          | TestData.defaultDataFile() + [extraField: "extra"] || "allowed"     | _
    }

}
