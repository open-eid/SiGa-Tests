package ee.openeid.siga.test.datafile.containers

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.helper.TestData
import ee.openeid.siga.test.model.*
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

import static ee.openeid.siga.test.helper.TestData.RESULT
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.Matchers.notNullValue

@Tag("datafileContainer")
@Epic("Delete container (datafile)")
@Feature("Delete container validation")
class DeleteValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Deleting container is successful")
    def "Deleting created container is successful"() {
        given: "create container"
        datafile.createDefaultContainer(flow)

        when: "delete container"
        datafile.deleteContainer(flow)

        then: "container is not found and error message is returned"
        Response getContainerResponse = datafile.tryGetContainer(flow)
        RequestErrorValidator.validate(getContainerResponse, RequestError.INVALID_RESOURCE)
    }

    @Story("Deleting container is successful")
    def "Deleting uploaded container is successful: #containerType"() {
        given: "uploaded container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when: "delete container"
        datafile.deleteContainer(flow)

        then: "container is not found and error message is returned"
        Response getContainerResponse = datafile.tryGetContainer(flow)
        RequestErrorValidator.validate(getContainerResponse, RequestError.INVALID_RESOURCE)

        where:
        containerType | containerName
        "ASiC-E"      | TestData.DEFAULT_ASICE_CONTAINER_NAME
        "ASiC-S"      | TestData.DEFAULT_ASICS_CONTAINER_NAME
    }

    @Story("Deleting a container always returns success regardless of container existence")
    def "Deleting container returns success even if container does not exist"() {
        given: "create container"
        datafile.createDefaultContainer(flow)

        when: "delete container"
        datafile.deleteContainer(flow)

        then: "trying to delete already deleted container returns success"
        Response deleteResponse = datafile.deleteContainer(flow)
        deleteResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body(RESULT, equalTo("OK"))
    }

    @Story("Deleting another clients container is not possible")
    def "Uploaded container remains accessible for the owner after delete attempt by another client"() {
        given: "container is uploaded by client 1"
        datafile.uploadDefaultContainer(flow)

        when: "when client 2 tries to delete the container, success is always returned"
        flow.setServiceUuid(Service.SERVICE2.uuid)
        flow.setServiceSecret(Service.SERVICE2.secret)
        datafile.deleteContainer(flow)

        then: "flow switched back to client 1, who requests the container and container is still accessible"
        flow.setServiceUuid(Service.SERVICE1.uuid)
        flow.setServiceSecret(Service.SERVICE1.secret)
        Response response = datafile.getContainer(flow)
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("containerName", equalTo("containerSingleSignature.asice"))
                .body("container", notNullValue())
    }

    @Story("Deleting container is successful in different container states")
    def "Deleting container '#scenario' is successful"() {
        given: "uploaded container"
        datafile.uploadDefaultContainer(flow)

        and: "container is moved to state: #scenario"
        switch (setupType) {
            case "beforeSigning" ->
                datafile.startRemoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

            case "afterSigning" ->
                datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

            case "afterRetrievingContainer" -> {
                datafile.remoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())
                datafile.getContainer(flow)
            }

            case "afterRetrievingSignatures" ->
                datafile.tryGetSignatureList(flow)
        }

        when: "delete container"
        datafile.deleteContainer(flow)

        and: "request signatures"
        Response response = datafile.tryGetSignatureList(flow)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)

        where:
        scenario                      | setupType
        "before signing"              | "beforeSigning"
        "after signing"               | "afterSigning"
        "after retrieving container"  | "afterRetrievingContainer"
        "after retrieving signatures" | "afterRetrievingSignatures"
    }

}
