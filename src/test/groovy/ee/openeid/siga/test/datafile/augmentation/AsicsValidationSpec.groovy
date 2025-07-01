package ee.openeid.siga.test.datafile.augmentation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.Story
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

import static ee.openeid.siga.test.matcher.IsoZonedTimestampMatcher.withinOneHourOfCurrentTime
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Augmentation")
@Feature("ASiC-S augmentation validation")
class AsicsValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Issue("SIGA-1112")
    @Story("Augmenting valid timestamped ASiC-S is allowed")
    def "Augmenting uploaded timestamped ASiC-S with #description is successful"() {
        given: "upload composite container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        when: "augment successfully container in session"
        datafile.augmentContainer(flow)

        then: "new timestamp is added"
        datafile.validateContainerInSession(flow).then().rootPath("validationConclusion.")
                .body("timeStampTokens", hasSize(2))
                .body("timeStampTokens[0].indication", is("TOTAL-PASSED"))
                .body("timeStampTokens[0].signedTime", is(firstTsTime))

                .body("timeStampTokens[1].indication", is("TOTAL-PASSED"))
                .body("timeStampTokens[1].signedTime", withinOneHourOfCurrentTime())

        where:
        description                           | fileName                                          | firstTsTime
        "valid nested container"              | "asicsContainerWithBdocAndTimestamp.asics"        | "2024-03-27T12:42:57Z"
        "without manifest file"               | "asicsWithDdocAndTimestampAndNoManifest.asics"    | "2025-06-18T17:21:06Z"
        "invalid nested container"            | "asicsContainerWithInvalidDdocAndTimestamp.asics" | "2024-09-09T12:13:34Z"
        "untrusted timestamp"                 | "asicsWithAsicsAndBaltstampTimestamp.asics"       | "2025-06-19T10:16:44Z"
//        "special chars in nested ASiC-S name" | "asicsWithAsicsWithSpecialCharsInFilename.asics"  | ""
    }

    @Issue("SIGA-1111")
    @Issue("SIGA-1110")
    @Story("Augmenting ASiC-S with invalid timestamp is not allowed")
    def "Augmenting ASiC-S with #description returns error"() {
        given: "upload composite container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        when: "try augmenting container in session"
        Response response = datafile.tryAugmentContainer(flow)

        then: "error is returned"
        response.then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body("errorCode", is("INTERNAL_SERVER_ERROR"))
                .body("errorMessage", is("General service error"))

        where:
        description                                                        | fileName
        "invalid timestamp"                                                | "1xTST-valid-bdoc-data-file-hash-failure-in-tst.asics"
        "first timestamp invalid and second not covering nested container" | "2xTstFirstInvalidSecondNotCoveringNestedTimestampedAsics.asics"
        "2 timestamps and second not covering nested container"            | "2xTST-both-valid-2nd-tst-not-covering-nested-container.asics"
    }

}
