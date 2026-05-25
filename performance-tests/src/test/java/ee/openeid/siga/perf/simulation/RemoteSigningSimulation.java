package ee.openeid.siga.perf.simulation;

import ee.openeid.siga.perf.scenarios.CreateContainer;
import ee.openeid.siga.perf.scenarios.RemoteSigningFlow;
import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public class RemoteSigningSimulation extends BaseSimulation {
    {
        ScenarioBuilder remote = scenario("ID-Card / remote signing happy flow")
                .exec(CreateContainer.datafileContainer())
                .exec(RemoteSigningFlow.remoteSigning());

        setUp(inject(remote)).protocols(httpProtocol());
    }
}
