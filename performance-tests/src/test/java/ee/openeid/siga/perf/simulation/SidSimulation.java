package ee.openeid.siga.perf.simulation;

import ee.openeid.siga.perf.scenarios.CreateContainer;
import ee.openeid.siga.perf.scenarios.SidFlow;
import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public class SidSimulation extends BaseSimulation {
    {
        ScenarioBuilder sid = scenario("SID happy flow")
                .exec(CreateContainer.datafileContainer())
                .exec(SidFlow.sid());

        setUp(inject(sid)).protocols(httpProtocol());
    }
}
