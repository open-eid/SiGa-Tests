package ee.openeid.siga.test.datafile.containers

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.helper.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Upload container (datafile)")
@Feature("Upload container validation")
class UploadValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Uploading container with edge-case input is allowed")
    def "Uploading container with #containerDesc is allowed"() {
        when:
        Response response = datafile.tryUploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        then:
        String containerId = response.path("containerId")
        assertThat(containerId.length(), is(36))
        response.then().statusCode(HttpStatus.SC_OK)

        where:
        containerDesc              | containerName
        "empty datafiles signed"   | "signedContainerWithEmptyDatafiles.asice"
        "empty datafiles unsigned" | "unsignedContainerWithEmptyDatafiles.asice"
        "no signatures"            | "containerWithoutSignatures.asice"
    }

    @Story("Successful ASiC-E container upload should return a container ID")
    def "Uploading ASiC-E container should return a container ID"() {
        when: "upload the container"
        Response response = datafile.tryUploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(TestData.DEFAULT_ASICE_CONTAINER_NAME))

        then: "container is created and container ID should be returned"
        response.then().statusCode(HttpStatus.SC_OK)
        String containerId = response.path("containerId")
        assertThat(containerId.length(), is(36))
    }

    @Story("Successful ASiC-S container upload should return a container ID")
    def "Uploading ASiC-S container should return a container ID"() {
        when: "upload the container"
        Response response = datafile.tryUploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(TestData.DEFAULT_ASICE_CONTAINER_NAME))

        then: "container is created and container ID should be returned"
        response.then().statusCode(HttpStatus.SC_OK)
        String containerId = response.path("containerId")
        assertThat(containerId.length(), is(36))
    }
}
