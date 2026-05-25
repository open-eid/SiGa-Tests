package ee.openeid.siga.perf.scenarios;

import ee.openeid.siga.perf.data.PayloadTemplates;
import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;

public final class CreateContainer {

    private CreateContainer() {}

    public static ChainBuilder datafileContainer() {
        return exec(SignedRequest.prepare("POST",
                        session -> "/containers",
                        session -> PayloadTemplates.createDatafileContainer()))
                .exec(SignedRequest.signedPost("Create datafile container")
                        .check(jsonPath("$.containerId").saveAs("containerId")));
    }
}
