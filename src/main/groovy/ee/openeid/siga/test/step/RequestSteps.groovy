package ee.openeid.siga.test.step

import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.SigaRequests
import io.qameta.allure.Step
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.util.concurrent.PollingConditions

abstract class RequestSteps {
    abstract SigaRequests getIntance()

    @Step("Create container")
    Response createContainer(Flow flow, Map request) {
        Response response = getIntance().createContainerRequest(flow, Method.POST, request).post()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.containerId = response.path("containerId")?.toString() ?: flow.containerId
        return response
    }

    @Step("Upload container")
    Response uploadContainer(Flow flow, Map request) {
        Response response = getIntance().uploadContainerRequest(flow, Method.POST, request).post()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.containerId = response.path("containerId")?.toString() ?: flow.containerId
        return response
    }

    @Step("Add data file")
    Response addDataFiles(Flow flow, Map request) {
        Response response = getIntance().addDataFilesRequest(flow, Method.POST, request).post()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get data files list")
    Response getDataFilesList(Flow flow) {
        Response response = getIntance().getDataFilesRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Delete data file")
    Response deleteDataFile(Flow flow, String datafileName) {
        Response response = getIntance().deleteDataFileRequest(flow, Method.DELETE, datafileName).delete()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Start remote signing")
    Response startRemoteSigning(Flow flow, Map request) {
        Response response = getIntance().startRemoteSigningRequest(flow, Method.POST, request).post()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Finalize remote signing")
    Response finalizeRemoteSigning(Flow flow, Map request, String signatureId) {
        Response response = getIntance().finalizeRemoteSigningRequest(flow, Method.PUT, request, signatureId).put()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Start MID signing")
    Response startMidSigning(Flow flow, Map request) {
        Response response = getIntance().startMidSigningRequest(flow, Method.POST, request).post()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Poll MID signing status")
    pollForMidSigningStatus(Flow flow, String signatureId) {
        def conditions = new PollingConditions(timeout: 28, initialDelay: 0, delay: 3.5)
        conditions.eventually {
            assert getMidSigningStatus(flow, signatureId).path("midStatus") == "SIGNATURE"
        }
    }

    @Step("Get MID signing status")
    Response getMidSigningStatus(Flow flow, String signatureId) {
        Response response = getIntance().getMidSigningStatusRequest(flow, Method.GET, signatureId).get()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.setMidStatus(response)
        return response
    }

    @Step("Start Smart-ID certificate choice")
    Response startSidCertificateChoice(Flow flow, Map request) {
        Response response = getIntance().startSidCertificateChoiceRequest(flow, Method.POST, request).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get Smart-ID certificate selection status")
    Response getSidCertificateStatus(Flow flow, String certificateId) {
        Response response = getIntance().getSidCertificateStatusRequest(flow, Method.GET, certificateId).get()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.setSidCertificateStatus(response)
        return response
    }

    @Step("Start Smart-ID signing")
    Response startSidSigning(Flow flow, Map request) {
        Response response = getIntance().startSidSigningRequest(flow, Method.POST, request).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get Smart-ID signing status")
    Response getSmartIdSigningInSession(Flow flow, String signatureId) {
        Response response = getIntance().getSidSigningStatusRequest(flow, Method.GET, signatureId).get()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.setSidStatus(response)
        return response
    }

    @Step("Get signature list")
    Response getSignatureList(Flow flow) {
        Response response = getIntance().getSignatureListRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get signature info")
    Response getSignatureInfo(Flow flow, String signatureId) {
        Response response = getIntance().getSignatureInfoRequest(flow, Method.GET, signatureId).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Validate container in session")
    Response validateContainerInSession(Flow flow) {
        Response response = getIntance().getValidationReportInSessionRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Validate container")
    Response validateContainer(Flow flow, Map request) {
        Response response = getIntance().getValidationReportWithoutSessionRequest(flow, Method.GET, request).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get container")
    Response getContainer(Flow flow) {
        Response response = getIntance().getContainerRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Delete container")
    Response deleteContainer(Flow flow) {
        Response response = getIntance().deleteContainerRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }
}
