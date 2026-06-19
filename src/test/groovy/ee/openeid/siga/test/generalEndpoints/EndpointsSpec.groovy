package ee.openeid.siga.test.generalEndpoints

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import io.qameta.allure.*
import io.restassured.http.Method
import org.apache.http.HttpStatus

import static ee.openeid.siga.test.request.SigaRequests.sigaRequestBase

@Epic("General endpoints")
@Feature("General endpoints checks")
class EndpointsSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Not allowed endpoints return error")
    def "Non-exposed Spring Boot actuator endpoint #endpoint is not allowed"() {
        expect: "authenticated request to a non-exposed actuator endpoint returns 404"
        sigaRequestBase(flow, Method.GET, "/actuator/" + endpoint)
                .get()
                .then().statusCode(HttpStatus.SC_NOT_FOUND)

        where:
        endpoint << [
                "beans", "caches", "conditions", "configprops", "env", "heapdump", "loggers",
                "mappings", "metrics", "scheduledtasks", "shutdown", "threaddump", "sbom", "logfile"
        ]
    }
}
