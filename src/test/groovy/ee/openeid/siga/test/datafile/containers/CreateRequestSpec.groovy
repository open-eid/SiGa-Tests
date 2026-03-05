package ee.openeid.siga.test.datafile.containers

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Create container (datafile)")
@Feature("Create container request validation")
class CreateRequestSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Creating container with invalid input is not allowed")
    def "Trying to create container with #description is not allowed"() {
        when:
        Response createResponse = datafile.tryCreateContainer(flow, requestBody)

        then:
        RequestErrorValidator.validate(createResponse, error)

        where:
        description                | requestBody                                                                                         || error
        "empty body"               | [:]                                                                                                 || RequestError.NO_DATAFILE
        "empty datafile"           | [containerName: "containerTest.asice", dataFiles: []]                                               || RequestError.NO_DATAFILE
        "null datafile"            | RequestData.createDatafileRequestBody("containerTest.asice", "testFile.txt", null)                  || RequestError.INVALID_DATAFILE_CONTENT
        "empty container name"     | RequestData.createDatafileRequestBody("", "testFile.txt", "dGVzdGZhaWw=")                           || RequestError.INVALID_CONTAINER_NAME
        "null container name"      | RequestData.createDatafileRequestBody(null, "testFile.txt", "dGVzdGZhaWw=")                         || RequestError.INVALID_CONTAINER_NAME
        "empty file name"          | RequestData.createDatafileRequestBody("containerTest.asice", "", "dGVzdGZhaWw=")                    || RequestError.INVALID_DATAFILE_NAME
        "null file name"           | RequestData.createDatafileRequestBody("containerTest.asice", null, "dGVzdGZhaWw=")                  || RequestError.INVALID_DATAFILE_NAME
        "empty file content"       | RequestData.createDatafileRequestBody("containerTest.asice", "testFile.txt", "")                    || RequestError.INVALID_DATAFILE_CONTENT
        "invalid datafile content" | RequestData.createDatafileRequestBody("containerTest.asice", "testFile.txt", "=")                   || RequestError.INVALID_DATAFILE_CONTENT
        "file in folder"           | RequestData.createDatafileRequestBody("containerTest.asice", "folder/testFile.txt", "dGVzdGZhaWw=") || RequestError.INVALID_DATAFILE_NAME
    }

    @Story("Creating container with forbidden characters in fileName returns error")
    def "Creating container returns error when fileName contain #invalidChar"() {
        given: "request container with invalid char in fileName"
        Map requestBody = RequestData.createDatafileRequestBody("containerTest.asice", "Char=${invalidChar}isInvalid", "dGVzdGZhaWw=")

        when: "try creating container with invalid char in fileName"
        Response response = datafile.tryCreateContainer(flow, requestBody)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_DATAFILE_NAME)

        where:
        invalidChar << ["/", "`", "?", "*", "\\", "<", ">", "|", "\"", ":", "\u0017", "\u0000", "\u0007"]
    }

    @Story("Creating container with forbidden characters in containerName returns error")
    def "Creating container returns error when containerName contain #invalidChar"() {
        given: "request container with invalid char in containerName"
        Map requestBody = RequestData.createDatafileRequestBody("Char=${invalidChar}isInvalid", "testFile.txt", "dGVzdGZhaWw=")

        when: "try creating container with invalid char in containerName"
        Response response = datafile.tryCreateContainer(flow, requestBody)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_CONTAINER_NAME)

        where:
        invalidChar << ["/", "`", "?", "*", "\\", "<", ">", "|", "\"", ":", "\u0017", "\u0000", "\u0007"]
    }

}
