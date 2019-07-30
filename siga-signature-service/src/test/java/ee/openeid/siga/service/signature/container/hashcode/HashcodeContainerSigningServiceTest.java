package ee.openeid.siga.service.signature.container.hashcode;

import ee.openeid.siga.common.SigningType;
import ee.openeid.siga.common.event.SigaEvent;
import ee.openeid.siga.common.event.SigaEventLogger;
import ee.openeid.siga.common.exception.InvalidSessionDataException;
import ee.openeid.siga.common.exception.TechnicalException;
import ee.openeid.siga.common.session.DataToSignHolder;
import ee.openeid.siga.common.session.HashcodeContainerSessionHolder;
import ee.openeid.siga.common.session.Session;
import ee.openeid.siga.service.signature.container.ContainerSigningService;
import ee.openeid.siga.service.signature.container.ContainerSigningServiceTest;
import ee.openeid.siga.service.signature.test.RequestUtil;
import ee.openeid.siga.session.SessionService;
import org.digidoc4j.Configuration;
import org.digidoc4j.DataToSign;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;

import static ee.openeid.siga.service.signature.test.RequestUtil.CONTAINER_ID;
import static ee.openeid.siga.service.signature.test.RequestUtil.createSignatureParameters;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class HashcodeContainerSigningServiceTest extends ContainerSigningServiceTest {
    private static final String EXPECTED_DATATOSIGN_PREFIX = "<ds:SignedInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></ds:CanonicalizationMethod><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512\"></ds:SignatureMethod><ds:Reference Id=\"r-id-1\" URI=\"test.txt\"><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha512\"></ds:DigestMethod><ds:DigestValue>gRKArS6jBsPLF1VP7aQ8VZ7BA5QA66hj/ntmNcxONZG5899w2VFHg9psyEH4Scg7rPSJQEYf65BGAscMztSXsA==</ds:DigestValue></ds:Reference><ds:Reference Type=\"http://uri.etsi.org/01903#SignedProperties\"";
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @InjectMocks
    private HashcodeContainerSigningService signingService;

    @Mock
    private SessionService sessionService;

    @Mock
    private Configuration configuration;

    @Mock
    private SigaEventLogger sigaEventLogger;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        configuration = new Configuration(Configuration.Mode.TEST);
        signingService.setConfiguration(configuration);
        Mockito.when(sigaEventLogger.logStartEvent(any())).thenReturn(SigaEvent.builder().timestamp(0L).build());
        Mockito.when(sigaEventLogger.logEndEventFor(any())).thenReturn(SigaEvent.builder().timestamp(0L).build());
        Mockito.when(sessionService.getContainer(CONTAINER_ID)).thenReturn(RequestUtil.createHashcodeSessionHolder());
    }

    @Test
    public void createDataToSignSuccessfulTest() {
        assertCreateDataToSignSuccessful();
    }

    @Test
    public void invalidContainerIdTest() {
        exceptionRule.expect(TechnicalException.class);
        exceptionRule.expectMessage("Unable to parse session");
        invalidContainerId();
    }

    @Test
    public void noDataFilesInSession() throws IOException, URISyntaxException {
        exceptionRule.expect(InvalidSessionDataException.class);
        exceptionRule.expectMessage("Unable to create signature. Data files must be added to container");
        HashcodeContainerSessionHolder sessionHolder = RequestUtil.createHashcodeSessionHolder();
        sessionHolder.getDataFiles().clear();

        Mockito.when(sessionService.getContainer(CONTAINER_ID)).thenReturn(sessionHolder);
        signingService.createDataToSign(CONTAINER_ID, createSignatureParameters(pkcs12Esteid2018SignatureToken.getCertificate()));
    }

    @Test
    public void onlyRequiredSignatureParametersTest() {
        assertOnlyRequiredSignatureParameters();
    }

    @Test
    public void signAndValidateSignatureTest() {
        assertSignAndValidateSignature();
    }

    @Test
    public void finalizeAndValidateSignatureTest() throws IOException, URISyntaxException {
        assertFinalizeAndValidateSignature();
    }

    @Test
    public void noDataToSignInSessionTest() {
        exceptionRule.expect(InvalidSessionDataException.class);
        exceptionRule.expectMessage("Unable to finalize signature. No data to sign with signature Id: someUnknownSignatureId");
        noDataToSignInSession();
    }

    @Test
    public void noDataToSignInSessionForSignatureIdTest() throws IOException, URISyntaxException {
        exceptionRule.expect(InvalidSessionDataException.class);
        exceptionRule.expectMessage("Unable to finalize signature. No data to sign with signature Id: someUnknownSignatureId");
        noDataToSignInSessionForSignatureId();
    }

    @Test
    public void successfulMobileIdSigningTest() throws IOException {
        assertSuccessfulMobileIdSigning();
    }

    @Test
    public void invalidMobileSignHashStatusTest() throws IOException {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("Invalid DigiDocService response");
        invalidMobileSignHashStatus();
    }

    @Test
    public void successfulMobileIdSignatureStatusTest() throws IOException, URISyntaxException {
        assertSuccessfulMobileIdSignatureProcessing();
    }

    @Test
    public void noSessionFoundMobileSigning() {
        exceptionRule.expect(InvalidSessionDataException.class);
        exceptionRule.expectMessage("Unable to finalize signature. No data to sign with signature Id: someUnknownSignatureId");
        signingService.processMobileStatus(CONTAINER_ID, "someUnknownSignatureId");
    }

    @Test
    public void successfulSmartIdSignatureTest() throws IOException{
        assertSuccessfulSmartIdSigning();
    }

    @Test
    public void successfulSmartIdSignatureStatusTest() throws IOException, URISyntaxException {
        assertSuccessfulSmartIdSignatureProcessing(sessionService);
    }

    @Override
    protected ContainerSigningService getSigningService() {
        return signingService;
    }

    @Override
    protected String getExpectedDataToSignPrefix() {
        return EXPECTED_DATATOSIGN_PREFIX;
    }

    @Override
    protected void setSigningServiceParameters() {
        signingService.setConfiguration(Configuration.of(Configuration.Mode.TEST));
    }

    @Override
    protected void mockRemoteSessionHolder(DataToSign dataToSign) throws IOException, URISyntaxException {
        HashcodeContainerSessionHolder sessionHolder = RequestUtil.createHashcodeSessionHolder();
        sessionHolder.addDataToSign(dataToSign.getSignatureParameters().getSignatureId(), DataToSignHolder.builder().dataToSign(dataToSign).signingType(SigningType.REMOTE).build());
        Mockito.when(sessionService.getContainer(CONTAINER_ID)).thenReturn(sessionHolder);
    }

    @Override
    protected void mockMobileIdSessionHolder(DataToSign dataToSign) throws IOException, URISyntaxException {
        HashcodeContainerSessionHolder session = RequestUtil.createHashcodeSessionHolder();
        session.addDataToSign(dataToSign.getSignatureParameters().getSignatureId(), DataToSignHolder.builder().dataToSign(dataToSign).signingType(SigningType.MOBILE_ID).sessionCode("2342384932").build());
        Mockito.when(sessionService.getContainer(CONTAINER_ID)).thenReturn(session);
    }

    @Override
    protected Session getSessionHolder() throws IOException, URISyntaxException {
        return RequestUtil.createHashcodeSessionHolder();
    }

}