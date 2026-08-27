package ee.openeid.siga.test.datafile.upload

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Upload container (datafile)")
@Feature("Upload container validation")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Uploading ASiC-S container")
    def "Uploading ASiC-S with #description is not allowed"() {
        when: "try uploading container"
        Response response = datafile.tryUploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_CONTAINER)

        where:
        description                         | fileName
        "XAdES signature and timestamp"     | "XadesMixedWithTst.asics"
        "CAdES signature and timestamp"     | "CadesMixedWithTst.asics"
        "CAdES signature"                   | "cadesAsicsWithDdoc.asics"
        "additional folder"                 | "AdditionalFolderInAsics.asics"
        "datafile missing"                  | "DataFileMissingAsics.asics"
        "two datafiles"                     | "TwoDataFilesAsics.asics"
        "META-INF not in root"              | "MetaInfNotInRoot.asics"
        "evidence record xml and timestamp" | "evidencerecordXmlMixedWithTST.asics"
        "evidence record ers and timestamp" | "evidencerecordErsMixedWithTST.asics"
    }

    @Story("Successful container upload should return a container ID")
    def "Uploading container with #containerType should return a container ID"() {
        when: "upload the container"
        Response response = datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        then: "container is created and container ID should be returned"
        String containerId = response.path("containerId")
        assertThat(containerId.length(), is(36))

        where:
        containerType | containerName
        "ASiC-E"      | TestData.DEFAULT_ASICE_CONTAINER_NAME
        "ASiC-S"      | TestData.DEFAULT_ASICS_CONTAINER_NAME
        "BDOC"        | "valid-bdoc-tm-newer.bdoc"
    }

    @Issue("SIGA-1277")
    @Story("Uploading container with edge-case input is allowed")
    def "Uploading container with #containerDesc is allowed"() {
        expect: "try upload container"
        datafile.tryUploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))
                .then().statusCode(HttpStatus.SC_OK)

        where:
        containerDesc                  | containerName
        "empty datafiles signed"       | "signedContainerWithEmptyDatafiles.asice"
        "empty datafiles unsigned"     | "unsignedContainerWithEmptyDatafiles.asice"
        "no signatures"                | "containerWithoutSignatures.asice"
        "ASiC-E with missing manifest" | "containerMissingManifest.asice" // Containers with missing manifest aren't declined
    }

    @Story("Uploading container with duplicate dataFiles is not allowed")
    def "Uploading container with #containerDesc is not allowed"() {
        when: "try uploading container"
        Response uploadResponse = datafile.tryUploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        then: "error is returned"
        RequestErrorValidator.validate(uploadResponse, error, errorMessageDetails)

        where:
        containerDesc                    | containerName                                  | error                                     | errorMessageDetails
        "duplicate dataFiles"            | "asice_duplicate_data_files.asice"             | RequestError.DUPLICATE_DATAFILE_CONTAINER | "readme.txt"
        "duplicate dataFile in manifest" | "asice_duplicate_data_files_in_manifest.asice" | RequestError.DUPLICATE_DATAFILE_MANIFEST  | "test.xml"
    }

    @Story("Uploading container with random string as container returns error")
    def "Uploading container returns error when container contains #randomString"() {
        given: "request body with random string as container"
        Map requestBody = RequestData.uploadDatafileRequestBody("random string", "containerName")

        when: "try creating container with random string as container"
        Response response = datafile.tryUploadContainer(flow, requestBody)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_FILE_CONTENT)
    }

}
