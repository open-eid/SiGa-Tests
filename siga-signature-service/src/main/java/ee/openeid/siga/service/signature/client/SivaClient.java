package ee.openeid.siga.service.signature.client;

import ee.openeid.siga.common.HashcodeDataFile;
import ee.openeid.siga.common.HashcodeSignatureWrapper;
import ee.openeid.siga.common.Signature;
import ee.openeid.siga.common.SignatureHashcodeDataFile;
import ee.openeid.siga.common.exception.ClientException;
import ee.openeid.siga.common.exception.InvalidHashAlgorithmException;
import ee.openeid.siga.service.signature.configuration.SivaConfigurationProperties;
import ee.openeid.siga.service.signature.container.hashcode.HashcodeContainerService;
import ee.openeid.siga.webapp.json.ValidationConclusion;
import lombok.extern.slf4j.Slf4j;
import org.digidoc4j.DigestAlgorithm;
import org.digidoc4j.SignatureProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class SivaClient {
    private static final String SIGNATURE_FILE_NAME = "signatures0.xml";
    private static final String HASHCODE_VALIDATION_ENDPOINT = "/validateHashcode";
    private static final String VALIDATION_ENDPOINT = "/validate";

    private RestTemplate restTemplate;
    private SivaConfigurationProperties configurationProperties;
    private HashcodeContainerService hashcodeContainerService;

    public ValidationConclusion validateHashcodeContainer(HashcodeSignatureWrapper signatureWrapper, List<HashcodeDataFile> dataFiles) {
        SivaHashcodeValidationRequest request = createHashcodeRequest(signatureWrapper, dataFiles);
        try {
            return validate(request, HASHCODE_VALIDATION_ENDPOINT);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Unexpected exception was thrown by SiVa. Status: {}-{}, Response body: {} ", e.getRawStatusCode(), e.getStatusText(), e.getResponseBodyAsString());
            Signature signature = hashcodeContainerService.transformSignature(signatureWrapper);
            if (signature != null && SignatureProfile.LTA.name().equals(signature.getSignatureProfile())) {
                throw new ClientException("Unable to validate container! Container contains signature with unsupported signature profile: LTA");
            } else {
                throw new ClientException("Unable to get valid response from client");
            }
        }
    }

    public ValidationConclusion validateAsicContainer(String name, String container) {
        SivaValidationRequest request = new SivaValidationRequest();
        request.setFilename(name);
        request.setDocument(container);
        try {
            return validate(request, VALIDATION_ENDPOINT);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Unexpected exception was thrown by SiVa. Status: {}-{}, Response body: {} ", e.getRawStatusCode(), e.getStatusText(), e.getResponseBodyAsString());
            throw new ClientException("Unable to get valid response from client");
        }
    }

    private ValidationConclusion validate(Object request, String validationEndpoint) {
        ResponseEntity<ValidationResponse> responseEntity;
        responseEntity = restTemplate.exchange(configurationProperties.getUrl() + validationEndpoint,
                HttpMethod.POST, formHttpEntity(request), ValidationResponse.class);
        if (responseEntity.getBody() == null) {
            throw new ClientException("Unable to parse client empty response");
        }
        log.info("Container validation details received successfully");
        return responseEntity.getBody().getValidationReport().getValidationConclusion();
    }

    private SivaHashcodeValidationRequest createHashcodeRequest(HashcodeSignatureWrapper
                                                                        signatureWrapper, List<HashcodeDataFile> dataFiles) {
        SivaHashcodeValidationRequest request = new SivaHashcodeValidationRequest();
        request.setFilename(SIGNATURE_FILE_NAME);
        request.setSignatureFile(new String(Base64.getEncoder().encode(signatureWrapper.getSignature())));

        List<SivaDataFile> sivaDataFiles = new ArrayList<>();
        dataFiles.forEach(dataFile -> {
            String hash;
            String hashAlgorithm = getDataFileHashAlgorithm(signatureWrapper.getDataFiles(), dataFile);
            if (DigestAlgorithm.SHA256.name().equals(hashAlgorithm)) {
                hash = dataFile.getFileHashSha256();
            } else {
                hash = dataFile.getFileHashSha512();
            }
            SivaDataFile sivaDataFile = new SivaDataFile();
            sivaDataFile.setFilename(dataFile.getFileName());
            sivaDataFile.setHash(hash);
            sivaDataFile.setHashAlgo(hashAlgorithm);
            sivaDataFiles.add(sivaDataFile);
        });
        request.setDatafiles(sivaDataFiles);
        return request;
    }

    private String getDataFileHashAlgorithm(List<SignatureHashcodeDataFile> signatureDataFiles, HashcodeDataFile
            dataFile) {
        for (SignatureHashcodeDataFile signatureDataFile : signatureDataFiles) {
            if (signatureDataFile.getFileName().equals(dataFile.getFileName())) {
                String hashAlgorithm = signatureDataFile.getHashAlgo();
                if (DigestAlgorithm.SHA256.name().equals(hashAlgorithm) || DigestAlgorithm.SHA512.name().equals(hashAlgorithm)) {
                    return hashAlgorithm;
                }
            }
        }
        throw new InvalidHashAlgorithmException("Container contains invalid hash algorithms");
    }

    private HttpEntity<?> formHttpEntity(Object object) {
        return new HttpEntity<>(object);
    }

    @Autowired
    protected void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    protected void setConfigurationProperties(SivaConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    @Autowired
    public void setHashcodeContainerService(HashcodeContainerService
                                                    hashcodeContainerService) {
        this.hashcodeContainerService = hashcodeContainerService;
    }
}