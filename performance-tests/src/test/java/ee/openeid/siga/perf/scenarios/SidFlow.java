package ee.openeid.siga.perf.scenarios;

import ee.openeid.siga.perf.config.PerfConfig;
import ee.openeid.siga.perf.data.PayloadTemplates;
import io.gatling.javaapi.core.ChainBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.asLongAs;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.pause;

public final class SidFlow {

    private SidFlow() {}

    public static ChainBuilder sid() {
        return exec(session -> session
                        .set("sidCertStatus", "OUTSTANDING_TRANSACTION")
                        .set("sidStatus",     "OUTSTANDING_TRANSACTION"))
                .exec(SignedRequest.prepare("POST",
                                session -> "/containers/" + session.getString("containerId") + "/smartidsigning/certificatechoice",
                                session -> PayloadTemplates.sidCertChoice(PerfConfig.SID_PERSON_ID, PerfConfig.SID_COUNTRY)))
                .exec(SignedRequest.signedPost("Start SID certificate choice")
                        .check(jsonPath("$.generatedCertificateId").saveAs("sidCertificateId")))
                .exec(pollCertChoice())
                .exec(SignedRequest.prepare("POST",
                                session -> "/containers/" + session.getString("containerId") + "/smartidsigning",
                                session -> PayloadTemplates.sidStart(session.getString("sidDocumentNumber"))))
                .exec(SignedRequest.signedPost("Start SID signing")
                        .check(jsonPath("$.generatedSignatureId").saveAs("sidSignatureId")))
                .exec(pollSigningStatus());
    }

    private static ChainBuilder pollCertChoice() {
        return asLongAs(
                session -> "OUTSTANDING_TRANSACTION".equals(session.getString("sidCertStatus"))
                        && session.getInt("sidCertPollIter") < PerfConfig.maxPollIterations(),
                "sidCertPollIter",
                false
        ).on(
                pause(Duration.ofMillis(PerfConfig.POLL_INTERVAL_MS)),
                exec(SignedRequest.prepare("GET",
                        session -> "/containers/" + session.getString("containerId")
                                + "/smartidsigning/certificatechoice/" + session.getString("sidCertificateId") + "/status",
                        session -> "")),
                exec(SignedRequest.signedGet("Get SID certificate-choice status")
                        .check(jsonPath("$.sidStatus").saveAs("sidCertStatus"))
                        .check(jsonPath("$.documentNumber").optional().saveAs("sidDocumentNumber")))
        );
    }

    private static ChainBuilder pollSigningStatus() {
        return asLongAs(
                session -> "OUTSTANDING_TRANSACTION".equals(session.getString("sidStatus"))
                        && session.getInt("sidPollIter") < PerfConfig.maxPollIterations(),
                "sidPollIter",
                false
        ).on(
                pause(Duration.ofMillis(PerfConfig.POLL_INTERVAL_MS)),
                exec(SignedRequest.prepare("GET",
                        session -> "/containers/" + session.getString("containerId")
                                + "/smartidsigning/" + session.getString("sidSignatureId") + "/status",
                        session -> "")),
                exec(SignedRequest.signedGet("Get SID signing status")
                        .check(jsonPath("$.sidStatus").saveAs("sidStatus")))
        );
    }
}
