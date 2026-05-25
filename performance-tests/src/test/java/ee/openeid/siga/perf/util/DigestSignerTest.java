package ee.openeid.siga.perf.util;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.*;

class DigestSignerTest {

    @Test
    void signs_with_sha512_and_signature_verifies() throws Exception {
        verifyForAlgorithm("SHA512");
    }

    @Test
    void signs_with_sha384_and_signature_verifies() throws Exception {
        verifyForAlgorithm("SHA384");
    }

    @Test
    void signs_with_sha256_and_signature_verifies() throws Exception {
        verifyForAlgorithm("SHA256");
    }

    @Test
    void exposes_signing_certificate_as_non_empty_base64() {
        String cert = DigestSigner.defaultSigner().signingCertificateBase64();
        assertNotNull(cert);
        assertTrue(cert.length() > 200, "Cert base64 should be substantial, was " + cert.length() + " chars");
        assertDoesNotThrow(() -> Base64.getDecoder().decode(cert));
    }

    private void verifyForAlgorithm(String digestAlgorithm) throws Exception {
        DigestSigner signer = DigestSigner.defaultSigner();

        byte[] data = ("perf-test-payload-" + digestAlgorithm + "-" + System.nanoTime()).getBytes();
        String base64Data = Base64.getEncoder().encodeToString(data);

        String base64Signature = signer.sign(base64Data, digestAlgorithm);
        byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);

        Signature verifier = Signature.getInstance(digestAlgorithm + "withECDSA");
        verifier.initVerify(loadDefaultPublicKey());
        verifier.update(data);
        assertTrue(verifier.verify(signatureBytes),
                digestAlgorithm + "withECDSA signature must verify against the keystore's certificate");
    }

    private static PublicKey loadDefaultPublicKey() throws Exception {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("sign_ECC_from_TEST_of_ESTEID2018.p12")) {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(in, "1234".toCharArray());
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String a = aliases.nextElement();
                if (ks.isKeyEntry(a)) {
                    Certificate cert = ks.getCertificate(a);
                    return cert.getPublicKey();
                }
            }
            throw new IllegalStateException("No key entry");
        }
    }
}
