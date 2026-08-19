package ee.openeid.siga.test.generalEndpoints

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.request.SigaRequests
import ee.openeid.siga.test.step.Steps
import io.qameta.allure.*
import io.restassured.http.ContentType
import io.restassured.response.Response

import static io.restassured.RestAssured.given
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath
import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.Matchers.is

@Epic("General endpoints")
@Feature("Monitoring endpoint validation")
class MonitoringSpec extends GenericSpecification {

    @Story("Version response validation")
    def "Version response structure matches schema"() {
        expect: "valid response is returned"
        Steps.getVersionInfo().then()
                .contentType("application/vnd.spring-boot.actuator.v3+json")
                .body(matchesJsonSchemaInClasspath("schemas/MonitorVersionSchema.json"))
    }

    @Story("Health response validation")
    def "Health #version response structure matches schema and statuses are UP"() {
        given: "v2/v3 spring boot actuator version is used"
        String mediaType = "application/vnd.spring-boot.actuator.${version}+json"

        expect: "valid health v2/v3 response and all statuses are UP"
        get("/actuator/health", mediaType).then()
                .contentType(mediaType)
                .body(matchesJsonSchemaInClasspath(schema))
                .body("${root}.sessionStorage.status", is("UP"))
                .body("${root}.metaInfo.status", is("UP"))
                .body("${root}.siva.status", is("UP"))
                .body("${root}.db.status", is("UP"))
                .body("status", is("UP"))

        where:
        version | root         | schema
        "v3"    | "components" | "schemas/MonitorHealthSchema.json"
        "v2"    | "details"    | "schemas/MonitorHealthV2Schema.json"
    }

    @Story("Heartbeat response validation")
    def "Heartbeat response structure matches schema and status is UP"() {
        expect: "valid response is returned"
        Steps.getHeartbeatInfo().then()
                .contentType("application/vnd.spring-boot.actuator.v3+json")
                .body(matchesJsonSchemaInClasspath("schemas/MonitorHeartbeatSchema.json"))
                .body("status", is("UP"))
    }

    @Story("Prometheus monitoring")
    def "Verify prometheus valid response"() {
        expect: "prometheus response returns valid response"
        Steps.getPrometheusInfo().then()
                .body(containsString("# HELP"))
                .body(containsString("jvm_memory_used_bytes"))
                .body(containsString("http_server_requests_seconds"))
                .body(containsString("tomcat_"))
    }

    @Step("GET {endpoint} with Accept {accept}")
    static Response get(String endpoint, String accept) {
        return given()
                .contentType(ContentType.JSON)
                .accept(accept)
                .when()
                .get(SigaRequests.sigaServiceUrl + endpoint)
    }
}