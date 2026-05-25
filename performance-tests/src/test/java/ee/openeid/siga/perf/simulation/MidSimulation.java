package ee.openeid.siga.perf.simulation;

import ee.openeid.siga.perf.scenarios.CreateContainer;
import ee.openeid.siga.perf.scenarios.MidFlow;
import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public class MidSimulation extends BaseSimulation {
    {
        ScenarioBuilder mid = scenario("MID happy flow")
                .exec(CreateContainer.datafileContainer())
                .exec(MidFlow.mid());

        setUp(inject(mid)).protocols(httpProtocol());
    }
}
