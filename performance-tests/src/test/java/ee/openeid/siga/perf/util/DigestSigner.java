package ee.openeid.siga.perf.util;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;

/**
 * ECDSA signer for the ID-Card / remote-signing flow.
 *
 * SiGa's remote-signing response returns:
 *   - dataToSign: base64-encoded canonicalized data (NOT a pre-computed digest)
 *   - digestAlgorithm: e.g. "SHA256", "SHA384", "SHA512"
 *
 * The signer must compute Hash(data) and sign — equivalent to digidoc4j's
 * PKCS12SignatureToken.sign(DigestAlgorithm, byte[]) for an EC key, which uses
 * "&lt;digest&gt;withECDSA" as the JCE algorithm.
 */
public final class DigestSigner {

    private static final String DEFAULT_KEYSTORE = "sign_ECC_from_TEST_of_ESTEID2018.p12";
    private static final String DEFAULT_PASSWORD = "1234";

    private final PrivateKey privateKey;
    private final String signingCertificateBase64;

    public DigestSigner(String keystoreResource, String password) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(keystoreResource)) {
            if (in == null) {
                throw new IllegalStateException("Keystore not found on classpath: " + keystoreResource);
            }
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(in, password.toCharArray());

            String alias = firstAlias(ks);
            this.privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
            Certificate cert = ks.getCertificate(alias);
            this.signingCertificateBase64 = Base64.getEncoder().encodeToString(cert.getEncoded());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load keystore " + keystoreResource, e);
        }
    }

    public static DigestSigner defaultSigner() {
        return new DigestSigner(DEFAULT_KEYSTORE, DEFAULT_PASSWORD);
    }

    public String signingCertificateBase64() {
        return signingCertificateBase64;
    }

    /**
     * Signs base64-encoded data using {@code <digestAlgorithm>withECDSA}.
     *
     * @param base64Data       base64 of the canonicalized bytes returned by SiGa as {@code dataToSign}
     * @param digestAlgorithm  e.g. "SHA256", "SHA384", "SHA512" — the {@code digestAlgorithm} field from SiGa's response
     * @return base64-encoded ECDSA signature, ready to send back as {@code signatureValue}
     */
    public String sign(String base64Data, String digestAlgorithm) {
        try {
            Signature ecdsa = Signature.getInstance(digestAlgorithm + "withECDSA");
            ecdsa.initSign(privateKey);
            ecdsa.update(Base64.getDecoder().decode(base64Data));
            return Base64.getEncoder().encodeToString(ecdsa.sign());
        } catch (Exception e) {
            throw new IllegalStateException("ECDSA signing failed (" + digestAlgorithm + "withECDSA)", e);
        }
    }

    private static String firstAlias(KeyStore ks) throws Exception {
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String a = aliases.nextElement();
            if (ks.isKeyEntry(a)) {
                return a;
            }
        }
        throw new IllegalStateException("No key entry found in keystore");
    }
}
