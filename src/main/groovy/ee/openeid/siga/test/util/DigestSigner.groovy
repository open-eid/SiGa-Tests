package ee.openeid.siga.test.util

import io.restassured.response.Response
import org.apache.http.client.methods.RequestBuilder
import org.digidoc4j.DigestAlgorithm
import org.digidoc4j.signers.PKCS12SignatureToken

class DigestSigner {
    static String signDigest(Response dataToSignResponse) {
        return signDigest(
                dataToSignResponse.path("dataToSign"),
                dataToSignResponse.path("digestAlgorithm")
        )
    }

    static String signDigest(String digestToSign, String algo) {
        return signDigestWithKeystore(digestToSign, algo, "sign_ECC_from_TEST_of_ESTEID2018.p12", "1234")
    }

    static String signDigestWithKeystore(String digestToSign, String algo, String keystoreName, String keystorePassword) {
        ClassLoader classLoader = RequestBuilder.class.getClassLoader()
        String path = classLoader.getResource(keystoreName).getPath()
        PKCS12SignatureToken signatureToken = new PKCS12SignatureToken(path, keystorePassword.toCharArray())
        byte[] signed = signatureToken.sign(DigestAlgorithm.valueOf(algo), Base64.getDecoder().decode(digestToSign))
        return Base64.getEncoder().encodeToString(signed)
    }
}
