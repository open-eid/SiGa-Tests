package ee.openeid.siga.test.datafile.upload

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Upload container (datafile)")
@Feature("Upload container request validation")
class RequestSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Issue("SIGA-1276")
    @Story("Uploading invalid container is not allowed")
    def "Trying to upload #containerDesc is not allowed"() {
        when: "try uploading container"
        Response uploadResponse = datafile.tryUploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        then: "error is returned"
        RequestErrorValidator.validate(uploadResponse, RequestError.INVALID_CONTAINER, errorMessageDetails)

        where:
        containerDesc           | containerName              | errorMessageDetails
        "BDOC with no datafile" | "containerNoDataFile.bdoc" | ""
        "DDOC"                  | "ddocSingleSignature.ddoc" | " type: DDOC" // Different errorMessage for unallowed container type
        "PDF"                   | "pdfSingleSignature.pdf"   | ""
    }

    @Story("Uploading container with forbidden characters in containerName returns error")
    def "Uploading container returns error when containerName contains #invalidChar"() {
        given: "request body with invalid character in containerName"
        Map requestBody = RequestData.uploadDatafileRequestBody("cmFuZG9tdGV4dA==", "Char=${invalidChar}isInvalid")

        when: "try creating container with invalid char in containerName"
        Response response = datafile.tryUploadContainer(flow, requestBody)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_CONTAINER_NAME)

        where:
        invalidChar << ["/", "`", "?", "*", "\\", "<", ">", "|", "\"", ":", "\u0017", "\u0000", "\u0007"]
    }

    @Story("Uploading container with invalid input is not allowed")
    def "Trying to upload container with #description is not allowed"() {
        when: "try uploading container"
        Response uploadResponse = datafile.tryUploadContainer(flow, requestBody)

        then: "error is returned"
        RequestErrorValidator.validate(uploadResponse, error)

        where:
        description            | requestBody                                                                                         | error
        "empty body"           | [:]                                                                                                 | RequestError.INVALID_FILE_CONTENT
        "empty container"      | RequestData.uploadDatafileRequestBody("no container", "container.asice")                            | RequestError.INVALID_FILE_CONTENT
        "empty container name" | RequestData.uploadDatafileRequestBody(RequestData.uploadDatafileRequestDefaultBody().container, "") | RequestError.INVALID_CONTAINER_NAME
        "not base64 container" | RequestData.uploadDatafileRequestBody("-32/432+*", "container.asice")                               | RequestError.INVALID_FILE_CONTENT
    }
}
