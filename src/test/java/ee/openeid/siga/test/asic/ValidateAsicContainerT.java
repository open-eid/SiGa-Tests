package ee.openeid.siga.test.asic;

import ee.openeid.siga.test.helper.EnabledIfSigaProfileActive;
import ee.openeid.siga.test.helper.TestBase;
import ee.openeid.siga.test.model.SigaApiFlow;
import ee.openeid.siga.webapp.json.CreateContainerRemoteSigningResponse;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static ee.openeid.siga.test.helper.TestData.CONTAINERS;
import static ee.openeid.siga.test.helper.TestData.DEFAULT_ASICE_CONTAINER_NAME;
import static ee.openeid.siga.test.helper.TestData.DEFAULT_ASICS_CONTAINER_NAME;
import static ee.openeid.siga.test.helper.TestData.ERROR_CODE;
import static ee.openeid.siga.test.helper.TestData.INVALID_REQUEST;
import static ee.openeid.siga.test.helper.TestData.REPORT_SIGNATURES;
import static ee.openeid.siga.test.helper.TestData.REPORT_SIGNATURES_COUNT;
import static ee.openeid.siga.test.helper.TestData.REPORT_TIMESTAMP_TOKENS;
import static ee.openeid.siga.test.helper.TestData.REPORT_VALID_SIGNATURES_COUNT;
import static ee.openeid.siga.test.helper.TestData.RESOURCE_NOT_FOUND;
import static ee.openeid.siga.test.helper.TestData.SIGNER_CERT_ESTEID2018_PEM;
import static ee.openeid.siga.test.helper.TestData.VALIDATIONREPORT;
import static ee.openeid.siga.test.utils.DigestSigner.signDigest;
import static ee.openeid.siga.test.utils.RequestBuilder.asicContainerRequest;
import static ee.openeid.siga.test.utils.RequestBuilder.asicContainerRequestFromFile;
import static ee.openeid.siga.test.utils.RequestBuilder.asicContainersDataRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.remoteSigningRequestWithDefault;
import static ee.openeid.siga.test.utils.RequestBuilder.remoteSigningSignatureValueRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

@EnabledIfSigaProfileActive("datafileContainer")
class ValidateAsicContainerT extends TestBase {

    private SigaApiFlow flow;

    @BeforeEach
    void setUp() {
        flow = SigaApiFlow.buildForTestClient1Service1();
    }

    @Test
    void validateAsiceContainer() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile(DEFAULT_ASICE_CONTAINER_NAME));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path("validationConclusion.policy.policyName"), equalTo("POLv4"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signedBy"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2018-11-23T12:24:04Z"));

        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("11404176865"));
    }

    @Test
    void validateAsicsContainerWithSignature() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile("asicsContainerWithLtSignatureWithoutTST.scs"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_TIMESTAMP_TOKENS), empty());
        assertThat(response.getBody().path("validationConclusion.policy.policyName"), equalTo("POLv4"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signedBy"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2024-09-11T10:20:32Z"));

        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void validateCompositeAsicsContainerWithTimestamp() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile("asicsContainerWithBdocAndTimestamp.asics"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES), hasSize(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LT_TM"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("O’CONNEŽ-ŠUSLIK TESTNUMBER,MARY ÄNN,60001016970"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("60001016970"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2023-07-07T11:48:32Z"));

        assertThat(response.getBody().path(REPORT_TIMESTAMP_TOKENS), hasSize(1));
        assertThat(response.getBody().path("validationConclusion.policy.policyName"), equalTo("POLv4"));
        assertThat(response.getBody().path(REPORT_TIMESTAMP_TOKENS + "[0].signedBy"), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023E"));
        assertThat(response.getBody().path(REPORT_TIMESTAMP_TOKENS + "[0].signedTime"), equalTo("2024-03-27T12:42:57Z"));
    }

    @Test
    void validateNonCompositeAsicsContainerWithTimestamp() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile(DEFAULT_ASICS_CONTAINER_NAME));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(0));
        assertThat(response.getBody().path(REPORT_SIGNATURES), hasSize(0));

        assertThat(response.getBody().path(REPORT_TIMESTAMP_TOKENS), hasSize(1));
        assertThat(response.getBody().path("validationConclusion.policy.policyName"), equalTo("POLv4"));
        assertThat(response.getBody().path(REPORT_TIMESTAMP_TOKENS + "[0].signedBy"), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023E"));
        assertThat(response.getBody().path(REPORT_TIMESTAMP_TOKENS + "[0].signedTime"), equalTo("2024-05-28T12:24:09Z"));
    }

    @Test
    void validateDDOCContainer() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile("ddocSingleSignature.ddoc"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));

        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("11404176865"));
    }

    @Test
    void validateDDOCHashcodeContainer() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile("hashcodeDdoc.ddoc"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
    }

    @Test
    void validatePadesContainer() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile("pdfSingleSignature.pdf"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
    }

    @Test
    void uploadAsiceContainerAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("containerWithMultipleSignatures.asice"));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(3));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(3));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signedBy"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2014-11-17T14:11:46Z"));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("11404176865"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[1].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[1].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[2].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[2].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void uploadAsicsContainerWithSignatureAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("asicsContainerWithLtSignatureWithoutTST.scs"));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_TIMESTAMP_TOKENS), empty());
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signedBy"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2024-09-11T10:20:32Z"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void uploadAsicsContainerWithTimestampAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile(DEFAULT_ASICS_CONTAINER_NAME));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(0));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(0));

        assertThat(validationResponse.getBody().path(REPORT_TIMESTAMP_TOKENS), hasSize(1));
        assertThat(validationResponse.getBody().path("validationConclusion.policy.policyName"), equalTo("POLv4"));
        assertThat(validationResponse.getBody().path(REPORT_TIMESTAMP_TOKENS + "[0].signedBy"), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023E"));
        assertThat(validationResponse.getBody().path(REPORT_TIMESTAMP_TOKENS + "[0].signedTime"), equalTo("2024-05-28T12:24:09Z"));
    }

    @Test
    void createAsicContainerSignRemotelyAndValidate() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        CreateContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT")).as(CreateContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void createAsicContainerWithLtaSignatureAndValidate() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        CreateContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LTA")).as(CreateContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
    }

    @Test
    void uploadAsicContainerWithLtaSignatureAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("validAsiceLta.asice"));

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("11404176865"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
    }

    @Test
    void validateAsicContainerWithLtaSignatureWithoutSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response validationResponse = postContainerValidationReport(flow, asicContainerRequestFromFile("validAsiceLta.asice"));

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("11404176865"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
    }

    @Test
    void validateAsicContainerSignedWithExpiredOcsp() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response validationResponse = postContainerValidationReport(flow, asicContainerRequestFromFile("esteid2018signerAiaOcspLT.asice"));

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].indication"), equalTo("INDETERMINATE"));
    }

    @Test
    void validateAsicContainerWithoutSignatures() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response validationResponse = postContainerValidationReport(flow, asicContainerRequestFromFile("containerWithoutSignatures.asice"));

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(0));
    }

    @Test
    void validateAsicContainerWithEmptyDataFiles() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response validationResponse = postContainerValidationReport(flow, asicContainerRequestFromFile("signedContainerWithEmptyDatafiles.asice"));

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        // TODO: when SiVa version is at least 3.5.0, then verify empty datafiles warnings
    }

    @Test
    void validateAsicContainerWithEmptyDataFilesAndWithoutSignatures() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response validationResponse = postContainerValidationReport(flow, asicContainerRequestFromFile("unsignedContainerWithEmptyDatafiles.asice"));

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(0));
    }

    @Test
    void uploadAsicContainerWithoutSignaturesAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("containerWithoutSignatures.asice"));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(0));
    }

    @Test
    void uploadAsicContainerWithEmptyDataFilesAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("signedContainerWithEmptyDatafiles.asice"));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        // TODO: when SiVa version is at least 3.5.0, then verify empty datafiles warnings
    }

    @Test
    void uploadAsicContainerWithEmptyDataFilesAndWithoutSignaturesAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        postUploadContainer(flow, asicContainerRequestFromFile("unsignedContainerWithEmptyDatafiles.asice"));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(0));
    }

    @Test
    void createAsicContainerWithoutSignaturesAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(0));
    }

    @Test
    void getValidationReportForNotExistingContainer() throws NoSuchAlgorithmException, InvalidKeyException {
        Response response = getValidationReportForContainerInSession(flow);
        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.getBody().path(ERROR_CODE), equalTo(RESOURCE_NOT_FOUND));
    }

    @Test
    void createAsicContainerAndValidateContainerStructure() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, asicContainersDataRequestWithDefault());
        CreateContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT")).as(CreateContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());
        Response containerResponse = getContainer(flow);

        Response validationResponse = postContainerValidationReport(flow, asicContainerRequest(containerResponse.getBody().path("container"), containerResponse.getBody().path("containerName")));
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
    }

    @Test
    void validatePadesContainerSubjectDistinguishedName() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile("pdfSingleTestSignature.pdf"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("11404176865"));
    }

    @Test
    void validateAsicContainerPlusInFileNameReferenceWithValidEncoding() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile("plusInFileNameReferenceWithValidEncoding.asice"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signatureScopes.name"),
                containsInAnyOrder(".txt", "+.txt", "Test Test + Test .txt", "test test test.txt", "test+test+test.txt", "Müük+hüpo+pank+muu.txt"));
    }

    @Test
    void validateAsicContainerPlusInFileNameReferenceWithInvalidEncoding() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, asicContainerRequestFromFile("plusInFileNameReferenceWithInvalidEncoding.asice"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(0));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signatureScopes.name"), contains("Müük+hüpo+pank+muu.txt"));
    }

    @Override
    public String getContainerEndpoint() {
        return CONTAINERS;
    }
}
