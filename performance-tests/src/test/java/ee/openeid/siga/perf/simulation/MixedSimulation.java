package ee.openeid.siga.perf.simulation;

import ee.openeid.siga.perf.config.PerfConfig;
import ee.openeid.siga.perf.scenarios.CreateContainer;
import ee.openeid.siga.perf.scenarios.MidFlow;
import ee.openeid.siga.perf.scenarios.RemoteSigningFlow;
import ee.openeid.siga.perf.scenarios.SidFlow;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.percent;
import static io.gatling.javaapi.core.CoreDsl.randomSwitch;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class MixedSimulation extends BaseSimulation {
    {
        ChainBuilder pickRandomFlow = randomSwitch().on(
                percent(PerfConfig.WEIGHT_MID).then(MidFlow.mid()),
                percent(PerfConfig.WEIGHT_SID).then(SidFlow.sid()),
                percent(PerfConfig.WEIGHT_REMOTE).then(RemoteSigningFlow.remoteSigning())
        );

        ScenarioBuilder mixed = scenario("Mixed MID/SID/ID-Card happy flow")
                .exec(CreateContainer.datafileContainer())
                .exec(pickRandomFlow);

        setUp(inject(mixed)).protocols(httpProtocol());
    }
}
