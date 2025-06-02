package ee.openeid.siga.test.datafile.validation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Datafile")
@Feature("Validation Report")
class DatafileValidationReportSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    //TODO: SIGA-1098 - comment in errors paths, if fixed
    def "Timestamped ASiC-S validation report contains all new timestamp token info"() {
        when:
        Response validationResponse = datafile.validateContainer(
                flow, RequestData.uploadDatafileRequestBodyFromFile("2xTstFirstInvalidSecondNotCoveringNestedTimestampedAsics.asics"))

        then:
        validationResponse.then().rootPath("validationConclusion.")
                .body("timeStampTokens[0].indication", is("TOTAL-FAILED"))
                .body("timeStampTokens[0].subIndication", is("HASH_FAILURE"))
                .body("timeStampTokens[0].timestampLevel", is("QTSA"))
                .body("timeStampTokens[0].warning", hasSize(0))
//                .body("timeStampTokens[0].errors", hasSize(1))
//                .body("timeStampTokens[0].errors[0].content[0]", is("The time-stamp message imprint is not intact!"))
                .body("timeStampTokens[0].certificates", hasSize(3))
                .body("timeStampTokens[0].certificates[0].commonName", is("DEMO SK TIMESTAMPING AUTHORITY 2023E"))
                .body("timeStampTokens[0].certificates[0].type", is("CONTENT_TIMESTAMP"))
                .body("timeStampTokens[0].certificates[0].content", startsWith("MIIDEjCCApigAwIBAgIQM7BQCImkdt18qWDYdbfOtjAKBggqhkjOP"))

                .body("timeStampTokens[1].indication", is("TOTAL-PASSED"))
                .body("timeStampTokens[1].subIndication", is(emptyOrNullString()))
                .body("timeStampTokens[1].timestampLevel", is("QTSA"))
                .body("timeStampTokens[1].errors", hasSize(0))
                .body("timeStampTokens[1].warning.content[0]", is("The time-stamp token does not cover container datafile!"))
                .body("timeStampTokens[1].certificates", hasSize(3))
    }

}
