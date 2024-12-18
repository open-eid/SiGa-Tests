package ee.openeid.siga.test;

import ee.openeid.siga.test.helper.TestBase;
import ee.openeid.siga.test.model.SigaApiFlow;
import ee.openeid.siga.webapp.json.CreateHashcodeContainerSmartIdCertificateChoiceResponse;
import ee.openeid.siga.webapp.json.CreateHashcodeContainerSmartIdSigningResponse;
import ee.openeid.siga.webapp.json.GetHashcodeContainerSmartIdCertificateChoiceStatusResponse;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static ee.openeid.siga.test.helper.TestData.CERTIFICATE_CHOICE;
import static ee.openeid.siga.test.helper.TestData.HASHCODE_CONTAINERS;
import static ee.openeid.siga.test.helper.TestData.RESOURCE_NOT_FOUND;
import static ee.openeid.siga.test.helper.TestData.SMARTID_SIGNING;
import static ee.openeid.siga.test.helper.TestData.STATUS;
import static ee.openeid.siga.test.utils.RequestBuilder.hashcodeContainersDataRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.smartIdCertificateChoiceRequest;
import static ee.openeid.siga.test.utils.RequestBuilder.smartIdSigningRequestWithDefault;

class EndpointT extends TestBase {

    private SigaApiFlow flow;

    @BeforeEach
    void setUp() {
        flow = SigaApiFlow.buildForTestClient1Service1();
    }

    @Test
    void urlPathWithTrailingSlashIsAllowed() throws Exception {
        String containerEndpointWithSlash = getContainerEndpoint() + "/";
        Response responseContainer = post(containerEndpointWithSlash, flow, hashcodeContainersDataRequestWithDefault().toString());
        responseContainer.then().statusCode(200);

        String smartIdSigningUrl = getContainerEndpoint() + "/" + flow.getContainerId() + SMARTID_SIGNING;

        String certificateEndpointWithSlash = smartIdSigningUrl + CERTIFICATE_CHOICE + "/";
        Response responseCertificate = post(certificateEndpointWithSlash, flow, smartIdCertificateChoiceRequest("30303039914", "EE").toString());
        responseCertificate.then().statusCode(200);

        String signingEndpointWithSlash = smartIdSigningUrl + "/";
        pollForSidCertificateStatus(flow, responseCertificate.as(CreateHashcodeContainerSmartIdCertificateChoiceResponse.class).getGeneratedCertificateId());
        String documentNumber = flow.getSidCertificateStatus().as(GetHashcodeContainerSmartIdCertificateChoiceStatusResponse.class).getDocumentNumber();
        Response responseSigning = post(signingEndpointWithSlash, flow, smartIdSigningRequestWithDefault("LT", documentNumber).toString());
        responseSigning.then().statusCode(200);

        String signatureId = responseSigning.as(CreateHashcodeContainerSmartIdSigningResponse.class).getGeneratedSignatureId();
        String statusEndpointWithSlash = smartIdSigningUrl + "/" + signatureId + STATUS + "/";
        Response responseStatus = get(statusEndpointWithSlash, flow);
        responseStatus.then().statusCode(200);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/error", "/actuator/error"})
    void errorEndpointNotAllowed(String endponint) throws Exception {
        Response response = get(endponint, flow);
        expectError(response, 404, "RESOURCE_NOT_FOUND_EXCEPTION");
    }

    @Test
    void augmentHashcodeContainerNotAllowed() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault());

        Response response = put(getContainerEndpoint() + "/" + flow.getContainerId() + "/augmentation", flow, "request");

        expectError(response, 404, RESOURCE_NOT_FOUND);
    }

    @Override
    public String getContainerEndpoint() {
        return HASHCODE_CONTAINERS;
    }
}
