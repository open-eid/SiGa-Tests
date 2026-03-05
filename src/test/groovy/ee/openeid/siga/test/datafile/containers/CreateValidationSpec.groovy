package ee.openeid.siga.test.datafile.containers

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.ContainerUtil
import eu.europa.esig.dss.enumerations.MimeType
import io.qameta.allure.*
import io.restassured.path.xml.XmlPath
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Create container (datafile)")
@Feature("Create container validation")
class CreateValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Successful container creation should return a container ID")
    def "Creating a container should return a container ID"() {
        when: "create default container"
        Response response = datafile.createContainer(flow, RequestData.createDatafileRequestDefaultBody())

        then: "container is created and container ID should be returned"
        String containerId = response.path("containerId")
        assertThat(containerId.length(), is(36))
    }

    @Story("Creating container with special characters in fileNames is successful")
    def "Creating container is successful when fileNames contain special characters"() {
        given: "request container with special character in fileName"
        String fileName = "!#\$%&'()+,-.0123456789;=@ ABCDEFGHIJKLMNOPQRSTUVWXYZÕÄÖÜ[]^_abcdefghijklmnopqrstuvwxyzõäöü{}~"
        Map requestBody = RequestData.createDatafileRequestBody("containerTest.asice", fileName, "dGVzdGZhaWw=")

        when: "try creating container with special character in fileName"
        Response response = datafile.tryCreateContainer(flow, requestBody)

        then: "new container with with special character in fileName is created"
        response.then().statusCode(HttpStatus.SC_OK)
    }

    @Story("Setting datafile MIME type based on extension when creating a container")
    def "Correct datafile MIME type should be set in the manifest for #ext"() {
        given:
        String fileName = "test$ext"
        Map requestBody = RequestData.createDatafileRequestBody("containerTest.asice", fileName, "dGVzdGZhaWw=")

        when:
        datafile.createContainer(flow, requestBody)

        then:
        XmlPath manifest = ContainerUtil.manifestAsXmlPath(datafile.getContainer(flow).path("container").toString(),
                "META-INF/manifest.xml")

        def expectedMimeType = MimeType.fromFileName("*.$ext").mimeTypeString
        assertThat(manifest.getString("manifest:manifest.manifest:file-entry[1].@manifest:media-type"), is(expectedMimeType))

        where:
        ext << [".txt", ".xml", ".html", ".pkcs7", ".p7s", ".pdf", ".asics", ".scs", ".asice", ".sce", ".bdoc", ".odt", ".ods", ".png", ".jpg", ".jpeg", ".tst", ".unknown"]
    }
}
