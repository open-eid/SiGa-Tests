package ee.openeid.siga.test.asic

import ee.openeid.siga.test.{BaseSimulation, HmacSignatureCalculator}

import java.io.FileInputStream
import java.util.Properties
import ee.openeid.siga.test.helper.TestData.SIGNER_CERT_PEM
import ee.openeid.siga.test.utils.RequestBuilder.{hashcodeContainersDataRequestWithDefault, smartIdSigningRequestWithDefault}
import ee.openeid.siga.test.utils.{DigestSigner, RequestBuilder}
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{status, _}
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class SmartIdSigningLoadSimulation extends BaseSimulation {
  private final val ASIC_SID_SIGNING_INIT: String = "/containers/${containerId}/smartidsigning"
  private final val ASIC_SID_SIGNING_STATUS: String = "/containers/${containerId}/smartidsigning/${generatedSignatureId}/status"

  private val loadTestScenario: ScenarioBuilder = scenario("Asic container SmartId signing flow load test")
    .feed(uuidFeeder)
      .exec(asicCreateContainer)
        .doIf("${containerId.exists()}") {
          exec(asicSmartIdSigningInit)
            .doIf("${generatedSignatureId.exists()}") {
              doWhileDuring(session => session("sidStatus").as[String].equals("OUTSTANDING_TRANSACTION"), 30 seconds, "counter", false) {
                exec(asicSmartIdSigningStatus)
                .doIf(session => session("sidStatus").as[String].equals("OUTSTANDING_TRANSACTION")) {
                  pause(3000 millis)
                }
              }
              .exitHereIfFailed
              .exec(asicGetContainer).exitHereIfFailed
              .exec(asicDeleteContainer)
            }
        }

  def asicSmartIdSigningInit = {
    http("SID_SIGNING_INIT")
      .post(ASIC_SID_SIGNING_INIT)
      .body(StringBody(smartIdSigningRequestWithDefault("LT", "PNOEE-30303039914-1Q3P-Q").toString)).asJson
      .check(
        status.is(200),
        jsonPath("$.generatedSignatureId").optional.saveAs("generatedSignatureId")
      )
      .sign(session => new HmacSignatureCalculator(session))
  }

  def asicSmartIdSigningStatus = {
    http("SID_SIGNING_STATUS")
      .get(ASIC_SID_SIGNING_STATUS)
      .check(status.is(200),
        jsonPath("$.sidStatus").saveAs("sidStatus")
      )
      .sign(session => new HmacSignatureCalculator(session))
  }

  setUp(loadTestScenario.inject(
    rampUsersPerSec(0) to 5 during (90 seconds),
    constantUsersPerSec(5) during (5 minutes)))
    .assertions(
      details("CREATE_CONTAINER").responseTime.mean.lt(500),
      details("CREATE_CONTAINER").successfulRequests.percent.gte(99.9),
      details("SID_SIGNING_INIT").responseTime.mean.lt(150),
      details("SID_SIGNING_INIT").successfulRequests.percent.gte(99.9),
      details("SID_SIGNING_STATUS").responseTime.mean.lt(150),
      details("GET_CONTAINER").responseTime.mean.lt(150),
      details("GET_CONTAINER").successfulRequests.percent.gte(99.9),
      details("DELETE_CONTAINER").responseTime.mean.lt(150),
      details("DELETE_CONTAINER").successfulRequests.percent.gte(99.9),
    )
}