package ee.openeid.siga.test.datafile.timestamps

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import spock.lang.Tag

import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Datafile")
@Feature("Timestamps")
class GetTimestampsSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Get timestamps of ASiC container")
    def "Get timestamps from uploaded timestamped composite ASiC-S container is successful"() {
        given: "upload composite ASiC-S container with single timestamp"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("asicsContainerWithDdocAndTimestamp.asics"))

        when: "get timestamps from container in session"
        def timestampsResponse = datafile.getTimestampList(flow)

        then: "List of retrieved timestamps contains a single timestamp"
        timestampsResponse.then()
                .body("timestamps", hasSize(1))
                .body("timestamps[0].id", is("T-519156403B8A19A11569455AA86FD01165C0209F55D6DB244333C001313AA5C9"))
                .body("timestamps[0].creationTime", is("2024-09-09T12:13:34Z"))
    }

    @Story("Get timestamps of ASiC container")
    def "Get timestamps from uploaded #containerType container without timestamps returns empty list"() {
        given: "upload container without timestamps"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        when: "get timestamps from container in session"
        def timestampsResponse = datafile.getTimestampList(flow)

        then: "List of retrieved timestamps is empty"
        timestampsResponse.then()
                .body("timestamps", hasSize(0))

        where:
        containerType | fileName
        "ASiC-S"      | "asicsContainerWithBdocWithoutTimestamp.asics"
        "ASiC-E"      | "validAsiceLta.asice"
        "BDOC"        | "valid-bdoc-tm-newer.bdoc"
    }
}
