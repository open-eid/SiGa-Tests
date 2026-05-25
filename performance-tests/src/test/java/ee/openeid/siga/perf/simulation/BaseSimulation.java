package ee.openeid.siga.perf.simulation;

import ee.openeid.siga.perf.config.PerfConfig;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.http.HttpDsl.http;

public abstract class BaseSimulation extends Simulation {

    protected static HttpProtocolBuilder httpProtocol() {
        return http
                .baseUrl(PerfConfig.baseUrl())
                .acceptHeader("application/json")
                .contentTypeHeader("application/json")
                .disableWarmUp();
    }

    protected PopulationBuilder inject(ScenarioBuilder scenario) {
        return scenario.injectOpen(injectionSteps());
    }

    private static OpenInjectionStep[] injectionSteps() {
        if ("constant".equalsIgnoreCase(PerfConfig.MODE)) {
            double targetRate = PerfConfig.FLOWS_PER_MINUTE / 60.0;
            List<OpenInjectionStep> steps = new ArrayList<>();
            if (PerfConfig.WARMUP_SECONDS > 0) {
                steps.add(rampUsersPerSec(0.0).to(targetRate)
                        .during(Duration.ofSeconds(PerfConfig.WARMUP_SECONDS)));
            }
            steps.add(constantUsersPerSec(targetRate)
                    .during(Duration.ofSeconds(PerfConfig.DURATION_SECONDS)));
            return steps.toArray(new OpenInjectionStep[0]);
        }
        return new OpenInjectionStep[]{
                rampUsers(PerfConfig.USERS).during(Duration.ofSeconds(PerfConfig.RAMP_SECONDS))
        };
    }
}
