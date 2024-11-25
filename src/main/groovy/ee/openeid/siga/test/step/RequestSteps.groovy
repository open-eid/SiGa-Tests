package ee.openeid.siga.test.step


import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.request.SigaRequests
import ee.openeid.siga.test.util.DigestSigner
import io.qameta.allure.Step
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.util.concurrent.PollingConditions

abstract class RequestSteps {
    abstract SigaRequests getInstance()

    @Step("Create container")
    Response createContainer(Flow flow, Map request) {
        Response response = getInstance().createContainerRequest(flow, Method.POST, request).post()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.containerId = response.path("containerId")?.toString() ?: flow.containerId
        return response
    }

    @Step("Upload container")
    Response uploadContainer(Flow flow, Map request) {
        Response response = getInstance().uploadContainerRequest(flow, Method.POST, request).post()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.containerId = response.path("containerId")?.toString() ?: flow.containerId
        return response
    }

    @Step("Add data file")
    Response addDataFiles(Flow flow, Map request) {
        Response response = getInstance().addDataFilesRequest(flow, Method.POST, request).post()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get data files list")
    Response getDataFilesList(Flow flow) {
        Response response = getInstance().getDataFilesRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Delete data file")
    Response deleteDataFile(Flow flow, String datafileName) {
        Response response = getInstance().deleteDataFileRequest(flow, Method.DELETE, datafileName).delete()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Start remote signing")
    Response tryStartRemoteSigning(Flow flow, Map requestBody) {
        return getInstance().startRemoteSigningRequest(flow, Method.POST, requestBody).post()
    }

    Response startRemoteSigning(Flow flow, Map request) {
        Response response = tryStartRemoteSigning(flow, request)
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Finalize remote signing")
    Response finalizeRemoteSigning(Flow flow, Map request, String signatureId) {
        Response response = getInstance().finalizeRemoteSigningRequest(flow, Method.PUT, request, signatureId).put()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    Response remoteSigning(Flow flow, Map requestBody) {
        Response response = startRemoteSigning(flow, requestBody)
        return finalizeRemoteSigning(
                flow,
                RequestData.remoteSigningFinalizeRequest(DigestSigner.signDigest(response)),
                response.path("generatedSignatureId")
        )
    }

    @Step("Start MID signing")
    Response startMidSigning(Flow flow, Map request) {
        Response response = tryStartMidSigning(flow, request)
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    Response tryStartMidSigning(Flow flow, Map request) {
        return getInstance().startMidSigningRequest(flow, Method.POST, request).post()
    }

    @Step("Poll MID signing status")
    pollForMidSigningStatus(Flow flow, String signatureId) {
        def conditions = new PollingConditions(timeout: 28, initialDelay: 0, delay: 3.5)
        conditions.eventually {
            assert tryGetMidSigningStatus(flow, signatureId).path("midStatus") != "OUTSTANDING_TRANSACTION"
        }
    }

    @Step("Get MID signing status")
    Response tryGetMidSigningStatus(Flow flow, String signatureId) {
        Response response = getInstance().getMidSigningStatusRequest(flow, Method.GET, signatureId).get()
        flow.setMidStatus(response)
        return response
    }

    def midSigning(Flow flow, String personId, String phoneNo) {
        midSigning(flow, RequestData.midSigningRequestBodyMinimal(personId, phoneNo))
    }

    def midSigning(Flow flow, Map requestBody) {
        Response response = startMidSigning(flow, requestBody)
        pollForMidSigningStatus(flow, response.path("generatedSignatureId"))
    }

    @Step("Start Smart-ID certificate choice")
    Response startSidCertificateChoice(Flow flow, Map request) {
        Response response = getInstance().startSidCertificateChoiceRequest(flow, Method.POST, request).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get Smart-ID certificate selection status")
    Response getSidCertificateStatus(Flow flow, String certificateId) {
        Response response = getInstance().getSidCertificateStatusRequest(flow, Method.GET, certificateId).get()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.setSidCertificateStatus(response)
        return response
    }

    @Step("Start Smart-ID signing")
    Response startSidSigning(Flow flow, Map request) {
        Response response = getInstance().startSidSigningRequest(flow, Method.POST, request).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get Smart-ID signing status")
    Response tryGetSmartIdSigningStatus(Flow flow, String signatureId) {
        Response response = getInstance().getSidSigningStatusRequest(flow, Method.GET, signatureId).get()
        flow.setSidStatus(response)
        return response
    }

    @Step("Poll Smart-ID signing status")
    pollForSidSigningStatus(Flow flow, String signatureId) {
        def conditions = new PollingConditions(timeout: 28, initialDelay: 0, delay: 3.5)
        conditions.eventually {
            assert tryGetSmartIdSigningStatus(flow, signatureId).path("sidStatus") != "OUTSTANDING_TRANSACTION"
        }
    }

    @Step("Get signature list")
    Response getSignatureList(Flow flow) {
        Response response = getInstance().getSignatureListRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get timestamp list")
    Response getTimestampList(Flow flow) {
        Response response = getInstance().getTimestampListRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get signature info")
    Response getSignatureInfo(Flow flow, String signatureId) {
        Response response = getInstance().getSignatureInfoRequest(flow, Method.GET, signatureId).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Validate container in session")
    Response validateContainerInSession(Flow flow) {
        Response response = getInstance().getValidationReportInSessionRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Validate container")
    Response validateContainer(Flow flow, Map request) {
        Response response = getInstance().getValidationReportWithoutSessionRequest(flow, Method.GET, request).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get container")
    Response getContainer(Flow flow) {
        Response response = getInstance().getContainerRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Delete container")
    Response deleteContainer(Flow flow) {
        Response response = getInstance().deleteContainerRequest(flow, Method.GET).get()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Augment container")
    Response augmentContainer(Flow flow) {
        Response response = getInstance().augmentationContainerRequest(flow, Method.PUT).put()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }
}
