package ee.openeid.siga.test.asic;

import ee.openeid.siga.test.accounts.SmartIdAccount;
import ee.openeid.siga.test.helper.EnabledIfSigaProfileActive;
import ee.openeid.siga.test.helper.TestBase;
import ee.openeid.siga.test.model.SigaApiFlow;
import ee.openeid.siga.webapp.json.CreateContainerMobileIdSigningResponse;
import ee.openeid.siga.webapp.json.CreateContainerRemoteSigningResponse;
import ee.openeid.siga.webapp.json.CreateContainerSmartIdSigningResponse;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static ee.openeid.siga.test.helper.TestData.CONTAINERS;
import static ee.openeid.siga.test.helper.TestData.REPORT_SIGNATURES;
import static ee.openeid.siga.test.helper.TestData.REPORT_SIGNATURES_COUNT;
import static ee.openeid.siga.test.helper.TestData.REPORT_VALID_SIGNATURES_COUNT;
import static ee.openeid.siga.test.helper.TestData.SID_EE_DEFAULT_DOCUMENT_NUMBER;
import static ee.openeid.siga.test.helper.TestData.SIGNER_CERT_ESTEID2018_PEM;
import static ee.openeid.siga.test.utils.DigestSigner.signDigest;
import static ee.openeid.siga.test.utils.RequestBuilder.asicContainerRequestFromFile;
import static ee.openeid.siga.test.utils.RequestBuilder.asicContainersDataRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.midSigningRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.remoteSigningRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.remoteSigningSignatureValueRequest;
import static ee.openeid.siga.test.utils.RequestBuilder.smartIdSigningRequestWithDefault;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

@EnabledIfSigaProfileActive("datafileContainer")
@Epic("/containers/{containerId}/augmentation")
class AugmentAsicContainerT extends TestBase {

    private SigaApiFlow flow;

    @BeforeEach
    void setUp() {
        flow = SigaApiFlow.buildForTestClient1Service1();
    }

    @Test
    void createRemotelySignedAsicContainerAndAugmentSucceeds() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        CreateContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT")).as(CreateContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());

        augment(flow)
                .then()
                .statusCode(200)
                .body("result", equalTo("OK"));

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
        // TODO SIGA-865: Signature with expired OCSP should maybe not be augmented
    void uploadAsicContainerWithSignatureWithExpiredOcspAndAugmentSucceeds() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("asice_ocsp_cert_expired.asice"));

        augment(flow)
                .then()
                .statusCode(200);

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void uploadAsicContainerWithNonEstonianSignatureOnlyAndTryAugmentingFails() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("latvian_LT_signature_with_7min_difference_between_TS_and_OCSP.asice"));

        Response response = augment(flow);

        expectError(response, 400, "INVALID_SESSION_DATA_EXCEPTION", "Unable to augment. Container does not contain any Estonian signatures");
    }

    @Test
    void uploadAsicContainerWithSignatureAndESealAndOnlySignatureGetsAugmented() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("LT_sig_and_LT_seal.asice"));

        augment(flow)
                .then()
                .statusCode(200);

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(2));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(2));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[1].signatureFormat"), equalTo("XAdES_BASELINE_LT"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[1].subjectDistinguishedName.commonName"), equalTo("Nortal QSCD Test Seal"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[1].subjectDistinguishedName.serialNumber"), equalTo("10391131"));
    }

    @Test
    void uploadAsicContainerWithTLevelSignatureOnlyAndTryAugmentingFails() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("tLevelSignature.asice"));

        Response response = augment(flow);

        expectError(response, 400, "INVALID_SESSION_DATA_EXCEPTION", "Unable to augment. Container does not contain any Estonian signatures with LT or LTA profile");
    }

    @Test
    void getSignatureListAndGetDatafileListReturnCorrectResponseForAugmentedContainer() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("containerSingleSignatureValidUntil-2026-01-22.asice"));

        augment(flow)
                .then()
                .statusCode(200);

        Response signaturesResponse = getSignatureList(flow);

        signaturesResponse.then()
                .statusCode(200)
                .body("signatures[0].signatureProfile", equalTo("LTA"))
                .body("signatures[0].signerInfo", startsWith("SERIALNUMBER=PNOEE-38001085718"))
                .body("signatures[1]", nullValue());

        Response dataFilesResponse = getDataFileList(flow);

        dataFilesResponse.then()
                .statusCode(200)
                .body("dataFiles[0].fileName", equalTo("test-textfile.txt"))
                .body("dataFiles[0].fileContent", equalTo("VGhpcyBpcyBhIHRlc3QuCg=="))
                .body("dataFiles[1]", nullValue());
    }

    @Test
    void createRemotelySignedAsicContainerWithLtProfileAndAugmentTwiceSucceeds() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        CreateContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT")).as(CreateContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());

        augment(flow)
                .then()
                .statusCode(200);
        augment(flow)
                .then()
                .statusCode(200);

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void createMobileIdSignedAsicContainerWithLtProfileAndAugmentSucceeds() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        Response response = postMidSigningInSession(flow, midSigningRequestWithDefault("60001019906", "+37200000766", "LT"));
        String signatureId = response.as(CreateContainerMobileIdSigningResponse.class).getGeneratedSignatureId();
        pollForMidSigning(flow, signatureId);

        augment(flow)
                .then()
                .statusCode(200);

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("MARY ÄNN,O’CONNEŽ-ŠUSLIK TESTNUMBER"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-60001019906"));
    }

    @Test
    void createSmartIdSignedAsicContainerWithLtProfileAndAugmentSucceeds() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        Response response = postSmartIdSigningInSession(flow, smartIdSigningRequestWithDefault("LT", SID_EE_DEFAULT_DOCUMENT_NUMBER));
        String signatureId = response.as(CreateContainerSmartIdSigningResponse.class).getGeneratedSignatureId();
        pollForSidSigning(flow, signatureId);

        augment(flow)
                .then()
                .statusCode(200);

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo(SmartIdAccount.defaultSigner().getCommonName()));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo(SmartIdAccount.defaultSigner().getSerialNumber()));
    }

    @Test
    void createSignedAsicContainerWithLtAndLtaSignaturesAndAugmentSucceeds() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        CreateContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT")).as(CreateContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());
        CreateContainerRemoteSigningResponse dataToSignResponse2 = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LTA")).as(CreateContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse2.getDataToSign(), dataToSignResponse2.getDigestAlgorithm())), dataToSignResponse2.getGeneratedSignatureId());

        augment(flow)
                .then()
                .statusCode(200);

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(2));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(2));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[1].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[1].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[1].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void uploadAsicContainerWithExpiredOcspCertificateAndAugmentSucceeds() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("asice_ocsp_cert_expired.asice"));

        augment(flow).then()
                .statusCode(200);

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Override
    public String getContainerEndpoint() {
        return CONTAINERS;
    }
}
