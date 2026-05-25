package ee.openeid.siga.perf.scenarios;

import ee.openeid.siga.perf.config.PerfConfig;
import ee.openeid.siga.perf.data.PayloadTemplates;
import io.gatling.javaapi.core.ChainBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.asLongAs;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.pause;

public final class MidFlow {

    private MidFlow() {}

    public static ChainBuilder mid() {
        return exec(session -> session.set("midStatus", "OUTSTANDING_TRANSACTION"))
                .exec(SignedRequest.prepare("POST",
                                session -> "/containers/" + session.getString("containerId") + "/mobileidsigning",
                                session -> PayloadTemplates.midStart(PerfConfig.MID_PERSON_ID, PerfConfig.MID_PHONE)))
                .exec(SignedRequest.signedPost("Start MID signing")
                        .check(jsonPath("$.generatedSignatureId").saveAs("midSignatureId")))
                .exec(pollMidStatus());
    }

    private static ChainBuilder pollMidStatus() {
        return asLongAs(
                session -> "OUTSTANDING_TRANSACTION".equals(session.getString("midStatus"))
                        && session.getInt("midPollIter") < PerfConfig.maxPollIterations(),
                "midPollIter",
                false
        ).on(
                pause(Duration.ofMillis(PerfConfig.POLL_INTERVAL_MS)),
                exec(SignedRequest.prepare("GET",
                        session -> "/containers/" + session.getString("containerId")
                                + "/mobileidsigning/" + session.getString("midSignatureId") + "/status",
                        session -> "")),
                exec(SignedRequest.signedGet("Get MID status")
                        .check(jsonPath("$.midStatus").saveAs("midStatus")))
        );
    }
}
