package ee.openeid.siga.test

import ee.openeid.siga.test.step.Steps

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.notNullValue

class MonitoringSpec extends GenericSpecification {

    def "Verify version info response"() {
        expect:
        Steps.getVersionInfo().then()
                .body("version", notNullValue())
                .contentType("application/vnd.spring-boot.actuator.v3+json")
    }

    def "Verify component statuses"() {
        expect:
        Steps.getHealthInfo().then().body(
                "status", equalTo("UP"),
                "components.ignite.status", equalTo("UP"),
                "components.metaInfo.status", equalTo("UP"),
                "components.siva.status", equalTo("UP"),
                "components.db.status", equalTo("UP"))
    }

    def "Verify status response structure"() {
        expect:
        Steps.getHealthInfo().then().body(
                "status", notNullValue(),
                "components.ignite.status", notNullValue(),
                "components.ignite.status", notNullValue(),
                "components.ignite.details.igniteActiveContainers", notNullValue(),
                "components.db.status", notNullValue(),
                "components.db.details.database", notNullValue(),
                "components.db.details.validationQuery", notNullValue(),
                "components.metaInfo.status", notNullValue(),
                "components.metaInfo.details.webappName", notNullValue(),
                "components.metaInfo.details.version", notNullValue(),
                "components.metaInfo.details.buildTime", notNullValue(),
                "components.metaInfo.details.startTime", notNullValue(),
                "components.metaInfo.details.currentTime", notNullValue(),
                "components.siva.status", notNullValue())
                .contentType("application/vnd.spring-boot.actuator.v3+json")
    }
}
