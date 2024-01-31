package ee.openeid.siga.test;

import ee.openeid.siga.test.helper.TestBase;
import ee.openeid.siga.test.model.SigaApiFlow;
import ee.openeid.siga.webapp.json.CreateHashcodeContainerSmartIdCertificateChoiceResponse;
import ee.openeid.siga.webapp.json.CreateHashcodeContainerSmartIdSigningResponse;
import ee.openeid.siga.webapp.json.GetHashcodeContainerSmartIdCertificateChoiceStatusResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ee.openeid.siga.test.helper.TestData.CERTIFICATE_CHOICE;
import static ee.openeid.siga.test.helper.TestData.HASHCODE_CONTAINERS;
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

        String certificateEndpointWithSlash = getContainerEndpoint() + "/" + flow.getContainerId() + SMARTID_SIGNING + CERTIFICATE_CHOICE + "/";
        Response responseCertificate = post(certificateEndpointWithSlash, flow, smartIdCertificateChoiceRequest("30303039914", "EE").toString());
        responseCertificate.then().statusCode(200);

        String signingEndpointWithSlash = getContainerEndpoint() + "/" + flow.getContainerId() + SMARTID_SIGNING + "/";
        pollForSidCertificateStatus(flow, responseCertificate.as(CreateHashcodeContainerSmartIdCertificateChoiceResponse.class).getGeneratedCertificateId());
        String documentNumber = flow.getSidCertificateStatus().as(GetHashcodeContainerSmartIdCertificateChoiceStatusResponse.class).getDocumentNumber();
        Response responseSigning = post(signingEndpointWithSlash, flow, smartIdSigningRequestWithDefault("LT", documentNumber).toString());
        responseSigning.then().statusCode(200);

        String signatureId = responseSigning.as(CreateHashcodeContainerSmartIdSigningResponse.class).getGeneratedSignatureId();
        String statusEndpointWithSlash = getContainerEndpoint() + "/" + flow.getContainerId() + SMARTID_SIGNING + "/" + signatureId + STATUS + "/";
        Response responseStatus = get(statusEndpointWithSlash, flow);
        responseStatus.then().statusCode(200);
    }

    @Override
    public String getContainerEndpoint() {
        return HASHCODE_CONTAINERS;
    }
}
