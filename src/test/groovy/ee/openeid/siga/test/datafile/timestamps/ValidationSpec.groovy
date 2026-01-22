package ee.openeid.siga.test.datafile.timestamps

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.response.Response
import spock.lang.Tag

import static ee.openeid.siga.test.helper.TestData.DEFAULT_ASICS_CONTAINER_NAME
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Timestamps")
@Feature("Get ASiC container timestamps")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Timestamped ASiC-S returns timestamps")
    def "Get timestamp of timestamped ASiC-S container returns timestamp"() {
        given: "upload ASiC-S container with single timestamp"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(DEFAULT_ASICS_CONTAINER_NAME))

        when: "get timestamps"
        Response timestampsResponse = datafile.getTimestampList(flow)

        then: "list contains a single timestamp"
        timestampsResponse.then()
                .body("timestamps", hasSize(1))
                .body("timestamps[0].id", is("T-E55313866A885F31E979704B0771C4DE7A119441D4414B1BD827FDD8256913DD"))
                .body("timestamps[0].creationTime", is("2024-05-28T12:24:09Z"))
    }

    @Story("Timestamped ASiC-S returns timestamps")
    def "Get timestamps of ASiC-S with multiple timestamps returns all timestamps"() {
        given: "upload ASiC-S container with multiple timestamps"
        datafile.uploadContainer(flow,
                RequestData.uploadDatafileRequestBodyFromFile("2xTst-Baltstamp+Baltstamp.asics"))

        when: "get timestamps"
        def timestampsResponse = datafile.getTimestampList(flow)

        then: "list contains all timestamps"
        timestampsResponse.then()
                .body("timestamps", hasSize(2))
                .body("timestamps[0].id", is("T-E59CD473B5473BF3C94405B2FFE8C1C17AC036C46E42DF3809D3529BE4D3018A"))
                .body("timestamps[0].creationTime", is("2025-04-01T13:05:12.664Z"))
                .body("timestamps[1].id", is("T-6D36AD93D58456C1D80567C2F39D8AC3799CDCD60B7CADF3EA5FBB0B17390573"))
                .body("timestamps[1].creationTime", is("2025-04-01T13:05:16.566Z"))
    }

    @Story("Timestamped composite ASiC-S returns timestamps")
    def "Get timestamps of timestamped composite ASiC-S with timestamped ASiC-S returns both ASiC-S´s timestamps"() {
        given: "upload composite ASiC-S with timestamped ASiC-S"
        datafile.uploadContainer(flow,
                RequestData.uploadDatafileRequestBodyFromFile("timestampedAsicsWithTimestampedAsics.asics"))

        when: "get timestamps"
        def timestampsResponse = datafile.getTimestampList(flow)

        then: "list contains timestamps form outer and inner ASiC-S"
        timestampsResponse.then()
                .body("timestamps", hasSize(2))
                .body("timestamps[0].id", is("T-E6EA21B335D2777199B1012E5F91E46011C606522B9984BECC7232492E303A93"))
                .body("timestamps[0].creationTime", is("2024-10-24T08:33:03Z"))
                .body("timestamps[1].id", is("T-42947EFAAE38B6E849E3744679C60ED81F917AD942D9FCB838AD3DA5AE7701AE"))
                .body("timestamps[1].creationTime", is("2024-10-24T08:28:08Z"))
    }

    @Story("Timestamped ASiC-S returns timestamps")
    def "Get timestamps of timestamped ASiC-S with #description then #result"() {
        given: "upload timestamped ASiC-S container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        when: "get timestamps"
        Response timestampsResponse = datafile.getTimestampList(flow)

        then: "list contains needed timestamp"
        timestampsResponse.then().body("timestamps", hasSize(timestampCount))

        where:
        description         | result                                  | timestampCount | fileName
        "invalid timestamp" | "both containers all TS´s are returned" | 3              | "2xTstFirstInvalidSecondNotCoveringNestedTimestampedAsics.asics"
        "with ASiC-E"       | "outer ASiC-S TS is returned"           | 1              | "timestampedAsicsWithAsice.asics"
    }

    @Story("ASiC container without timestamps returns empty list")
    def "Get timestamps of #containerDesc container returns empty list"() {
        given: "upload container without timestamps"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        when: "get timestamps from container in session"
        Response timestampsResponse = datafile.getTimestampList(flow)

        then: "List of retrieved timestamps is empty"
        timestampsResponse.then()
                .body("timestamps", hasSize(0))

        where:
        containerDesc              | fileName
        "ASiC-S without timestamp" | "asicsContainerWithBdocWithoutTimestamp.asics"
        "ASiC-S with signature"    | "signedAsicsWithSignedDdoc.scs"
        "ASiC-E with signature"    | "validAsiceLta.asice"
        "BDOC with signature"      | "valid-bdoc-tm-newer.bdoc"
    }
}
