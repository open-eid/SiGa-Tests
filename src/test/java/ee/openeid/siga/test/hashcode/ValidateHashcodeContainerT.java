package ee.openeid.siga.test.hashcode;

import ee.openeid.siga.test.helper.TestBase;
import ee.openeid.siga.test.model.SigaApiFlow;
import ee.openeid.siga.webapp.json.CreateHashcodeContainerRemoteSigningResponse;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static ee.openeid.siga.test.helper.TestData.*;
import static ee.openeid.siga.test.utils.DigestSigner.signDigest;
import static ee.openeid.siga.test.utils.RequestBuilder.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

class ValidateHashcodeContainerT extends TestBase {

    private SigaApiFlow flow;

    @BeforeEach
    void setUp() {
        flow = SigaApiFlow.buildForTestClient1Service1();
    }

    @Test
    void validateHashcodeContainer() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcodeMultipleSignatures.asice"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(3));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(3));
        assertThat(response.getBody().path("validationConclusion.policy.policyName"), equalTo("POLv4"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signedBy"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2019-02-22T11:04:25Z"));

        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[1].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[1].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[2].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[2].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void validateHashcodeContainerWithManySignatures() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcode105Signatures.asice"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(105));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(105));
        assertThat(response.getBody().path("validationConclusion.policy.policyName"), equalTo("POLv4"));

        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signedBy"), equalTo("O’CONNEŽ-ŠUSLIK TESTNUMBER,MARY ÄNN,60001019906"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].indication"), equalTo("TOTAL-PASSED"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2021-04-26T12:31:08Z"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[104].signedBy"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[104].indication"), equalTo("TOTAL-PASSED"));
    }

    @Test
    void validateDDOCHashcodeContainer() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcodeDdoc.ddoc"));

        assertThat(response.statusCode(), equalTo(200));

        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path("validationConclusion.policy.policyName"), equalTo("POLv4"));
        assertThat(response.getBody().path("validationConclusion.validationWarnings.content"), hasItem("The algorithm SHA1 used in DDOC is no longer considered reliable for signature creation!"));
        assertThat(response.getBody().path(REPORT_SIGNATURE_FORM), equalTo("DIGIDOC_XML_1.3_hashcode"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signatureFormat"), equalTo("DIGIDOC_XML_1.3"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2012-10-03T07:46:51Z"));
    }

    @Test
    void validateHashcodeContainerWithLTASignatureProfile() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        Response response = postContainerValidationReport(flow, hashcodeContainerRequest(HASHCODE_CONTAINER_WITH_LTA_SIGNATURE));
        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.getBody().path(ERROR_CODE), equalTo(CLIENT_EXCEPTION));
        assertThat(response.getBody().path(ERROR_MESSAGE), equalTo("Unable to validate container! Container contains signature with unsupported signature profile: LTA"));
    }

    @Test
    void uploadHashcodeContainerAndValidateInSession() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postUploadContainer(flow, hashcodeContainerRequest(DEFAULT_HASHCODE_CONTAINER));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].signedBy"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].info.bestSignatureTime"), equalTo("2019-02-22T11:04:25Z"));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void createHashcodeContainerSignRemotelyAndValidate() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault());
        CreateHashcodeContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT")).as(CreateHashcodeContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());

        Response validationResponse = getValidationReportForContainerInSession(flow);
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void validateRegularDDOCContainer() throws Exception {
        Response validationResponse = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("container.ddoc"));
        assertThat(validationResponse.statusCode(), equalTo(400));
        assertThat(validationResponse.getBody().path(ERROR_CODE), equalTo(INVALID_CONTAINER_EXCEPTION));
        assertThat(validationResponse.getBody().path(ERROR_MESSAGE), equalTo("EMBEDDED DDOC is not supported"));
    }

    @Test
    void validateHashcodeContainerWithoutSignatures() throws Exception {
        Response validationResponse = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcodeWithoutSignature.asice"));

        assertThat(validationResponse.statusCode(), equalTo(400));
        assertThat(validationResponse.getBody().path(ERROR_CODE), equalTo(INVALID_CONTAINER_EXCEPTION));
    }

    @Test
    void validateHashcodeContainerWithZeroFileSizeDataFiles() throws Exception {
        Response validationResponse = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcodeSignedContainerWithEmptyDatafiles.asice"));

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path("validationConclusion.signatures[0].warnings.content"), hasItems(
                containsString("Data file 'empty-file-2.txt' is empty"),
                containsString("Data file 'empty-file-4.txt' is empty")
        ));
        assertThat(validationResponse.getBody().path("validationConclusion.signatures[0].warnings.content"), everyItem(not(containsString("data-file-1.txt"))));
        assertThat(validationResponse.getBody().path("validationConclusion.signatures[0].warnings.content"), everyItem(not(containsString("data-file-3.txt"))));
        assertThat(validationResponse.getBody().path("validationConclusion.signatures[0].warnings.content"), everyItem(not(containsString("data-file-5.txt"))));

        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("PNOEE-38001085718"));
    }

    @Test
    void validateHashcodeContainerWithZeroFileSizeDataFilesAndWithoutSignatures() throws Exception {
        Response validationResponse = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcodeUnsignedContainerWithEmptyDatafiles.asice"));

        assertThat(validationResponse.statusCode(), equalTo(400));
        assertThat(validationResponse.getBody().path(ERROR_CODE), equalTo(INVALID_CONTAINER_EXCEPTION));
        assertThat(validationResponse.getBody().path(ERROR_MESSAGE), equalTo("Missing signatures"));
    }

    @Test
    void uploadHashcodeContainerWithoutSignaturesAndValidateInSession() throws Exception {
        postUploadContainer(flow, hashcodeContainerRequestFromFile("hashcodeWithoutSignature.asice"));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(400));
        assertThat(validationResponse.getBody().path(ERROR_CODE), equalTo(INVALID_CONTAINER_EXCEPTION));
    }

    @Test
    void uploadHashcodeContainerWithZeroFileSizeDataFilesAndValidateInSession() throws Exception {
        postUploadContainer(flow, hashcodeContainerRequestFromFile("hashcodeSignedContainerWithEmptyDatafiles.asice"));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(validationResponse.getBody().path("validationConclusion.signatures[0].warnings.content"), hasItems(
                containsString("Data file 'empty-file-2.txt' is empty"),
                containsString("Data file 'empty-file-4.txt' is empty")
        ));
        assertThat(validationResponse.getBody().path("validationConclusion.signatures[0].warnings.content"), everyItem(not(containsString("data-file-1.txt"))));
        assertThat(validationResponse.getBody().path("validationConclusion.signatures[0].warnings.content"), everyItem(not(containsString("data-file-3.txt"))));
        assertThat(validationResponse.getBody().path("validationConclusion.signatures[0].warnings.content"), everyItem(not(containsString("data-file-5.txt"))));
    }

    @Test
    void uploadHashcodeContainerWithZeroFileSizeDataFilesAndWithoutSignaturesAndValidateInSession() throws Exception {
        postUploadContainer(flow, hashcodeContainerRequestFromFile("hashcodeUnsignedContainerWithEmptyDatafiles.asice"));

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(400));
        assertThat(validationResponse.getBody().path(ERROR_CODE), equalTo(INVALID_CONTAINER_EXCEPTION));
        assertThat(validationResponse.getBody().path(ERROR_MESSAGE), equalTo("Missing signatures"));
    }

    @Test
    void createHashcodeContainerWithoutSignaturesAndValidate() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault());

        Response validationResponse = getValidationReportForContainerInSession(flow);

        assertThat(validationResponse.statusCode(), equalTo(400));
        assertThat(validationResponse.getBody().path(ERROR_CODE), equalTo(INVALID_CONTAINER_EXCEPTION));
    }

    @Test
    void getValidationReportForNotExistingContainer() throws NoSuchAlgorithmException, InvalidKeyException {
        Response response = getValidationReportForContainerInSession(flow);
        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.getBody().path(ERROR_CODE), equalTo(RESOURCE_NOT_FOUND));
    }

    @Test
    void createHashcodeContainerAndValidateContainerStructure() throws JSONException, NoSuchAlgorithmException, InvalidKeyException {
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault());
        CreateHashcodeContainerRemoteSigningResponse dataToSignResponse = postRemoteSigningInSession(flow, remoteSigningRequestWithDefault(SIGNER_CERT_ESTEID2018_PEM, "LT")).as(CreateHashcodeContainerRemoteSigningResponse.class);
        putRemoteSigningInSession(flow, remoteSigningSignatureValueRequest(signDigest(dataToSignResponse.getDataToSign(), dataToSignResponse.getDigestAlgorithm())), dataToSignResponse.getGeneratedSignatureId());
        Response containerResponse = getContainer(flow);

        Response validationResponse = postContainerValidationReport(flow, hashcodeContainerRequest(containerResponse.getBody().path("container")));
        assertThat(validationResponse.statusCode(), equalTo(200));
        assertThat(validationResponse.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
    }

    @Test //SIGA handles this as DELETE to containerId
    void deleteToValidateHashcodeContainer() throws NoSuchAlgorithmException, InvalidKeyException, JSONException {
        Response response = delete(getContainerEndpoint() + VALIDATIONREPORT, flow);

        assertThat(response.statusCode(), equalTo(200));
    }

    @Test
    void headToValidateHashcodeContainerInSession() throws NoSuchAlgorithmException, InvalidKeyException, JSONException, IOException {
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME));

        Response response = head(getContainerEndpoint() + "/" + flow.getContainerId() + VALIDATIONREPORT, flow);

        assertThat(response.statusCode(), equalTo(200));
    }

    @Test
    void validateDDOCHashcodeContainerSubjectDistinguishedName() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcodeDdocTest.ddoc"));

        assertThat(response.statusCode(), equalTo(200));

        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.commonName"), equalTo("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].subjectDistinguishedName.serialNumber"), equalTo("11404176865"));
    }

    @Test
    void validateHashcodeContainerPlusInFileNameReferenceWithValidEncoding() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcodePlusInFileNameReferenceWithValidEncoding.asice"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signatureScopes.name"),
                containsInAnyOrder(".txt", "+.txt", "Test Test + Test .txt", "test test test.txt", "test+test+test.txt", "Müük+hüpo+pank+muu.txt"));
    }

    @Test
    void validateHashcodeContainerPlusInFileNameReferenceWithInvalidEncoding() throws JSONException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Response response = postContainerValidationReport(flow, hashcodeContainerRequestFromFile("hashcodePlusInFileNameReferenceWithInvalidEncoding.asice"));

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.getBody().path(REPORT_SIGNATURES_COUNT), equalTo(1));
        assertThat(response.getBody().path(REPORT_VALID_SIGNATURES_COUNT), equalTo(0));
        assertThat(response.getBody().path(REPORT_SIGNATURES + "[0].signatureScopes.name"), contains("Müük+hüpo+pank+muu.txt"));
    }

    @Override
    public String getContainerEndpoint() {
        return HASHCODE_CONTAINERS;
    }
}
