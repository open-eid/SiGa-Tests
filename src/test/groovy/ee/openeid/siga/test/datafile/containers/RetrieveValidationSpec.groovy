package ee.openeid.siga.test.datafile.containers

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.*
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.ContainerUtil
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.path.xml.XmlPath
import io.restassured.response.Response
import spock.lang.Tag

import static ee.openeid.siga.test.TestData.*
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Retrieve container (datafile)")
@Feature("Retrieve container validation")
class RetrieveValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Retrieving container is successful")
    def "Retrieving created container is successful"() {
        given: "create container"
        Map defaultBody = RequestData.createDatafileRequestDefaultBody()
        datafile.createContainer(flow, defaultBody)

        when: "retrieve container"
        Response response = datafile.getContainer(flow)

        then: "retrieved container is unchanged"
        response.then()
                .body("container.length()", equalTo(DEFAULT_CREATED_ASICE_CONTAINER_LENGTH))
                .body("containerName", equalTo(defaultBody.containerName))

        and: "returned container is parseable"
        XmlPath manifest = ContainerUtil.manifestAsXmlPath(response.path("container").toString(), "META-INF/manifest.xml")
        assertThat(manifest.getString("manifest:manifest.manifest:file-entry[1].@manifest:media-type"), is("text/plain"))
    }

    @Story("Retrieving container is successful")
    def "Retrieving uploaded container is successful: #containerType"() {
        given: "upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when: "retrieve container"
        Response response = datafile.getContainer(flow)

        then: "retrieved container is unchanged"
        response.then()
                .body("container.length()", equalTo(lenght))
                .body("containerName", equalTo(containerName))

        and: "returned container is parseable"
        XmlPath manifest = ContainerUtil.manifestAsXmlPath(response.path("container").toString(), "META-INF/manifest.xml")
        assertThat(manifest.getString("manifest:manifest.manifest:file-entry[1].@manifest:media-type"), is("text/plain"))

        where:
        containerType | containerName                | lenght
        "ASiC-E"      | DEFAULT_ASICE_CONTAINER_NAME | DEFAULT_ASICE_CONTAINER_LENGTH
        "ASiC-S"      | DEFAULT_ASICS_CONTAINER_NAME | DEFAULT_ASICS_CONTAINER_LENGTH
    }

    @Story("Retrieving container is successful")
    def "Retrieving created container twice is successful"() {
        given: "create container"
        datafile.createContainer(flow, RequestData.createDatafileRequestDefaultBody())

        when: "retrieve container twice"
        Response firstResponse = datafile.getContainer(flow)
        Response secondResponse = datafile.getContainer(flow)

        then: "both retrieved containers are unchanged"
        firstResponse.then()
                .body("container.length()", equalTo(DEFAULT_CREATED_ASICE_CONTAINER_LENGTH))
        secondResponse.then()
                .body("container.length()", equalTo(DEFAULT_CREATED_ASICE_CONTAINER_LENGTH))
    }

    @Story("Retrieving deleted container is not possible")
    def "Retrieving deleted container returns error"() {
        given: "upload and delete container"
        datafile.uploadDefaultContainer(flow)
        datafile.deleteContainer(flow)

        when: "retrieve deleted container"
        Response response = datafile.tryGetContainer(flow)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)
    }

    @Story("Retrieving another clients container is not possible")
    def "Retrieving another clients container returns error"() {
        given: "container is uploaded by client 1"
        datafile.uploadDefaultContainer(flow)

        when: "client 2 tries to retrieve the container"
        flow.setServiceUuid(Service.SERVICE2.uuid)
        flow.setServiceSecret(Service.SERVICE2.secret)
        Response response = datafile.tryGetContainer(flow)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)
    }

    @Story("Retrieving container is successful in different container states")
    def "Retrieving container '#scenario' is successful"() {
        given: "upload container"
        datafile.uploadDefaultContainer(flow)

        and: "container is moved to state: #scenario"
        switch (setupType) {
            case "beforeSigning" ->
                datafile.startRemoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

            case "afterSigning" ->
                datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

            case "afterValidation" ->
                datafile.validateContainerInSession(flow)

            case "afterRetrievingSignatures" ->
                datafile.getSignatureList(flow)
        }

        when: "retrieve container"
        Response response = datafile.getContainer(flow)

        then: "retrieved container is unchanged #scenario"
        response.then()
                .body("container.length()", (lenght))

        where:
        scenario                      | setupType                   | lenght
        "before signing"              | "beforeSigning"             | equalTo(DEFAULT_ASICE_CONTAINER_LENGTH)
        "after signing"               | "afterSigning"              | greaterThan(20910)
        "after validation"            | "afterValidation"           | equalTo(DEFAULT_ASICE_CONTAINER_LENGTH)
        "after retrieving signatures" | "afterRetrievingSignatures" | equalTo(DEFAULT_ASICE_CONTAINER_LENGTH)
    }

}
