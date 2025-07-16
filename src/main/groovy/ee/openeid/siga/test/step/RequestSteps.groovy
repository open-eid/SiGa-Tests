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

    // CONTAINER ACTIONS
    @Step("Create container")
    Response createContainer(Flow flow, Map requestBody) {
        Response response = getInstance().createContainerRequest(flow, Method.POST, requestBody).post()
        response.then().statusCode(HttpStatus.SC_OK)
        flow.containerId = response.path("containerId")?.toString() ?: flow.containerId
        return response
    }

    @Step("Upload container")
    Response tryUploadContainer(Flow flow, Map requestBody) {
        Response response = getInstance().uploadContainerRequest(flow, Method.POST, requestBody).post()
        return response
    }

    Response uploadContainer(Flow flow, Map requestBody) {
        Response response = tryUploadContainer(flow, requestBody)
        response.then().statusCode(HttpStatus.SC_OK)
        flow.containerId = response.path("containerId")?.toString() ?: flow.containerId
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
        Response response = getInstance().deleteContainerRequest(flow, Method.GET).delete()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Add data files")
    Response tryAddDataFiles(Flow flow, Map requestBody) {
        Response response = getInstance().addDataFilesRequest(flow, Method.POST, requestBody).post()
        return response
    }

    Response addDataFiles(Flow flow, Map requestBody) {
        Response response = tryAddDataFiles(flow, requestBody)
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
    Response tryDeleteDataFile(Flow flow, String datafileName) {
        Response response = getInstance().deleteDataFileRequest(flow, Method.DELETE, datafileName).delete()
        return response
    }

    Response deleteDataFile(Flow flow, String datafileName) {
        Response response = tryDeleteDataFile(flow, datafileName)
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    // REMOTE SIGNING
    @Step("Start remote signing")
    Response tryStartRemoteSigning(Flow flow, Map requestBody) {
        return getInstance().startRemoteSigningRequest(flow, Method.POST, requestBody).post()
    }

    Response startRemoteSigning(Flow flow, Map requestBody) {
        Response response = tryStartRemoteSigning(flow, requestBody)
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

    // MID SIGNING
    @Step("Start MID signing")
    Response tryStartMidSigning(Flow flow, Map requestBody) {
        return getInstance().startMidSigningRequest(flow, Method.POST, requestBody).post()
    }

    Response startMidSigning(Flow flow, Map requestBody) {
        Response response = tryStartMidSigning(flow, requestBody)
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get MID signing status")
    Response tryGetMidSigningStatus(Flow flow, String signatureId) {
        Response response = getInstance().getMidSigningStatusRequest(flow, Method.GET, signatureId).get()
        flow.setMidStatus(response)
        return response
    }

    @Step("Poll MID signing status")
    pollForMidSigningStatus(Flow flow, String signatureId) {
        def conditions = new PollingConditions(timeout: 28, initialDelay: 0, delay: 3.5)
        conditions.eventually {
            assert tryGetMidSigningStatus(flow, signatureId).path("midStatus") != "OUTSTANDING_TRANSACTION"
        }
    }

    def midSigning(Flow flow, String personId, String phoneNo) {
        midSigning(flow, RequestData.midSigningRequestBodyMinimal(personId, phoneNo))
    }

    def midSigning(Flow flow, Map requestBody) {
        Response response = startMidSigning(flow, requestBody)
        pollForMidSigningStatus(flow, response.path("generatedSignatureId"))
    }

    // SID SIGNING
    @Step("Start Smart-ID Certificate Choice")
    Response tryStartSidCertificateChoice(Flow flow, Map requestBody) {
        return getInstance().startSidCertificateChoiceRequest(flow, Method.POST, requestBody).post()
    }

    Response startSidCertificateChoice(Flow flow, Map requestBody) {
        Response response = tryStartSidCertificateChoice(flow, requestBody)
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get Smart-ID Certificate Choice status")
    Response tryGetSidCertificateChoiceStatus(Flow flow, String certificateId) {
        Response response = getInstance().getSidCertificateStatusRequest(flow, Method.GET, certificateId).get()
        flow.setSidCertificateStatus(response)
        return response
    }

    @Step("Poll Smart-ID Certificate Choice status")
    pollForSidCertificateChoiceStatus(Flow flow, String certificateId) {
        def conditions = new PollingConditions(timeout: 28, initialDelay: 0, delay: 3.5)
        conditions.eventually {
            assert tryGetSidCertificateChoiceStatus(flow, certificateId).path("sidStatus") != "OUTSTANDING_TRANSACTION"
        }
    }

    @Step("Get Smart-ID document number")
    String getSidDocumentNumber(Flow flow, Map certificateChoiceRequestBody) {
        Response sidCertificateChoiceResponse = startSidCertificateChoice(flow, certificateChoiceRequestBody)
        String generatedCertificateId = sidCertificateChoiceResponse.path("generatedCertificateId")
        pollForSidCertificateChoiceStatus(flow, generatedCertificateId)
        return flow.getSidCertificateStatus().path("documentNumber")
    }

    @Step("Start Smart-ID signing")
    Response tryStartSidSigning(Flow flow, Map requestBody) {
        return getInstance().startSidSigningRequest(flow, Method.POST, requestBody).post()
    }

    Response startSidSigning(Flow flow, Map requestBody) {
        Response response = tryStartSidSigning(flow, requestBody)
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

    @Step("Sign with Smart-ID successfully")
    Response sidSigningSuccessful(Flow flow, Map certificateChoiceRequestBody) {
        String documentNumber = getSidDocumentNumber(flow, certificateChoiceRequestBody)
        Response startSigning = startSidSigning(flow, RequestData.sidSigningRequestDefaultBody(documentNumber))
        pollForSidSigningStatus(flow, startSigning.path("generatedSignatureId"))
        return flow.getSidStatus()
    }

    // SIG/TS LIST & INFO
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

    // VALIDATE
    @Step("Validate container in session")
    Response tryValidateContainerInSession(Flow flow) {
        Response response = getInstance().getValidationReportInSessionRequest(flow, Method.GET).get()
        return response
    }

    Response validateContainerInSession(Flow flow) {
        Response response = tryValidateContainerInSession(flow)
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Validate container without session")
    Response validateContainer(Flow flow, Map requestBody) {
        Response response = getInstance().getValidationReportWithoutSessionRequest(flow, Method.POST, requestBody).post()
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    // AUGMENTING
    @Step("Augment container")
    Response tryAugmentContainer(Flow flow) {
        Response response = getInstance().augmentationContainerRequest(flow, Method.PUT).put()
        return response
    }

    Response augmentContainer(Flow flow) {
        Response response = tryAugmentContainer(flow)
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }
}
