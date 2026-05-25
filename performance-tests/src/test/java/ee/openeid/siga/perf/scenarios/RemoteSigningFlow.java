package ee.openeid.siga.perf.scenarios;

import ee.openeid.siga.perf.data.PayloadTemplates;
import ee.openeid.siga.perf.util.DigestSigner;
import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;

public final class RemoteSigningFlow {

    private static final DigestSigner SIGNER = DigestSigner.defaultSigner();

    private RemoteSigningFlow() {}

    public static ChainBuilder remoteSigning() {
        return exec(SignedRequest.prepare("POST",
                        session -> "/containers/" + session.getString("containerId") + "/remotesigning",
                        session -> PayloadTemplates.remoteSigningStart(SIGNER.signingCertificateBase64())))
                .exec(SignedRequest.signedPost("Start remote signing")
                        .check(jsonPath("$.generatedSignatureId").saveAs("remoteSignatureId"))
                        .check(jsonPath("$.dataToSign").saveAs("dataToSign"))
                        .check(jsonPath("$.digestAlgorithm").saveAs("digestAlgorithm")))
                .exec(session -> session.set("signatureValue",
                        SIGNER.sign(session.getString("dataToSign"), session.getString("digestAlgorithm"))))
                .exec(SignedRequest.prepare("PUT",
                        session -> "/containers/" + session.getString("containerId")
                                + "/remotesigning/" + session.getString("remoteSignatureId"),
                        session -> PayloadTemplates.remoteSigningFinalize(session.getString("signatureValue"))))
                .exec(SignedRequest.signedPut("Finalize remote signing")
                        .check(jsonPath("$.result").is("OK")));
    }
}
