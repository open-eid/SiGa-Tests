package ee.openeid.siga.test.asic;

import ee.openeid.siga.test.helper.EnabledIfSigaProfileActive;
import ee.openeid.siga.test.helper.TestBase;
import ee.openeid.siga.test.model.SigaApiFlow;
import ee.openeid.siga.webapp.json.CreateContainerMobileIdSigningResponse;
import ee.openeid.siga.webapp.json.CreateContainerRemoteSigningResponse;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ee.openeid.siga.test.helper.TestData.CONTAINER;
import static ee.openeid.siga.test.helper.TestData.CONTAINERS;
import static ee.openeid.siga.test.helper.TestData.CONTAINER_NAME;
import static ee.openeid.siga.test.helper.TestData.DEFAULT_ASICE_CONTAINER_LENGHT;
import static ee.openeid.siga.test.helper.TestData.DEFAULT_ASICE_CONTAINER_NAME;
import static ee.openeid.siga.test.helper.TestData.DEFAULT_ASICS_CONTAINER_NAME;
import static ee.openeid.siga.test.helper.TestData.INVALID_REQUEST;
import static ee.openeid.siga.test.helper.TestData.MANIFEST;
import static ee.openeid.siga.test.helper.TestData.RESOURCE_NOT_FOUND;
import static ee.openeid.siga.test.helper.TestData.SERVICE_SECRET_2;
import static ee.openeid.siga.test.helper.TestData.SERVICE_UUID_2;
import static ee.openeid.siga.test.helper.TestData.SIGNER_CERT_ESTEID2018_PEM;
import static ee.openeid.siga.test.utils.ContainerUtil.extractEntryFromContainer;
import static ee.openeid.siga.test.utils.ContainerUtil.manifestAsXmlPath;
import static ee.openeid.siga.test.utils.DigestSigner.signDigest;
import static ee.openeid.siga.test.utils.RequestBuilder.asicContainerRequestFromFile;
import static ee.openeid.siga.test.utils.RequestBuilder.asicContainersDataRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.midSigningRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.remoteSigningRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.remoteSigningSignatureValueRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnabledIfSigaProfileActive("datafileContainer")
class RetrieveAsicContainerT extends TestBase {

    private SigaApiFlow flow;

    @BeforeEach
    void setUp() {
        flow = SigaApiFlow.buildForTestClient1Service1();
    }

    @Test
    void uploadAsiceContainerAndRetrieveIt() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        Response response = getContainer(flow);
        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", equalTo(DEFAULT_ASICE_CONTAINER_LENGHT))
                .body(CONTAINER_NAME, equalTo(DEFAULT_ASICE_CONTAINER_NAME));

        XmlPath manifest = manifestAsXmlPath(extractEntryFromContainer(MANIFEST, response.path(CONTAINER).toString()));

        assertEquals("text/plain", manifest.getString("manifest:manifest.manifest:file-entry[" + (1) + "].@manifest:media-type"));
    }

    @Test
    void uploadAsicsContainerAndRetrieveIt() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICS_CONTAINER_NAME));
        Response response = getContainer(flow);
        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", equalTo(2332))
                .body(CONTAINER_NAME, equalTo(DEFAULT_ASICS_CONTAINER_NAME));

        XmlPath manifest = manifestAsXmlPath(extractEntryFromContainer(MANIFEST, response.path(CONTAINER).toString()));

        assertEquals("text/plain", manifest.getString("manifest:manifest.manifest:file-entry[" + (1) + "].@manifest:media-type"));
    }

    @Test
    void createAsicContainerAndRetrieve() throws Exception {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        Response response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", greaterThan(800))
                .body(CONTAINER_NAME, equalTo(DEFAULT_ASICE_CONTAINER_NAME));

        XmlPath manifest = manifestAsXmlPath(extractEntryFromContainer(MANIFEST, response.path(CONTAINER).toString()));

        assertEquals("text/plain", manifest.getString("manifest:manifest.manifest:file-entry[" + (1) + "].@manifest:media-type"));
    }

    @Test
    void retrieveAsicContainerTwice() throws Exception {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());

        Response response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", greaterThan(800));

        response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", greaterThan(800));
    }

    @Test
    void retrieveAsicContainerBeforeSigning() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT"));

        Response response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", equalTo(DEFAULT_ASICE_CONTAINER_LENGHT));
    }

    @Test
    void retrieveAsicContainerAfterSigning() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        CreateContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT")).as(CreateContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());

        Response response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", greaterThan(20910));
    }

    @Test
    void retrieveAsicContainerBeforeFinishingMidSigning() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        postMidSigningInSession(flow, midSigningRequestWithDefault("60001019906", "+37200000766", "LT"));

        Response response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", equalTo(DEFAULT_ASICE_CONTAINER_LENGHT));
    }

    @Test
    @EnabledIfSigaProfileActive("mobileId")
    void retrieveAsicContainerDuringMidSigning() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        Response response = postMidSigningInSession(flow, midSigningRequestWithDefault("60001019906", "+37200000766", "LT"));
        String signatureId = response.as(CreateContainerMobileIdSigningResponse.class).getGeneratedSignatureId();
        getMidSigningInSession(flow, signatureId);

        response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", equalTo(DEFAULT_ASICE_CONTAINER_LENGHT));
    }

    @Test
    @EnabledIfSigaProfileActive("mobileId")
    void retrieveAsicContainerAfterMidSigning() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        Response response = postMidSigningInSession(flow, midSigningRequestWithDefault("60001019906", "+37200000766", "LT"));
        String signatureId = response.as(CreateContainerMobileIdSigningResponse.class).getGeneratedSignatureId();
        pollForMidSigning(flow, signatureId);

        response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", greaterThan(20910));
    }

    @Test
    void retrieveAsicContainerAfterValidation() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        getValidationReportForContainerInSession(flow);

        Response response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", equalTo(DEFAULT_ASICE_CONTAINER_LENGHT));
    }

    @Test
    void retrieveAsicContainerAfterRetrievingSignatures() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        getSignatureList(flow);

        Response response = getContainer(flow);

        response.then()
                .statusCode(200)
                .body(CONTAINER + ".length()", equalTo(DEFAULT_ASICE_CONTAINER_LENGHT));
    }

    @Test
    void retrieveAsicContainerForOtherClientNotPossible() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));

        flow.setServiceUuid(SERVICE_UUID_2);
        flow.setServiceSecret(SERVICE_SECRET_2);
        Response response = getContainer(flow);

        expectError(response, 400, RESOURCE_NOT_FOUND);
    }

    @Test
    void deleteAsicContainerAndRetrieveIt() throws Exception {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));
        deleteContainer(flow);

        Response response = getContainer(flow);

        expectError(response, 400, RESOURCE_NOT_FOUND);
    }

    @Override
    public String getContainerEndpoint() {
        return CONTAINERS;
    }
}
