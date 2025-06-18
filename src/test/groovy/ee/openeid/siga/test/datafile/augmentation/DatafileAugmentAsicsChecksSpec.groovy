package ee.openeid.siga.test.datafile.augmentation

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response
import spock.lang.Tag

import static ee.openeid.siga.test.matcher.IsoZonedTimestampMatcher.withinOneHourOfCurrentTime
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is

@Tag("datafileContainer")
@Epic("Augmentation")
@Feature("ASiC-S augmentation checks")
class DatafileAugmentAsicsChecksSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("ASiC-S container must not contain any signatures")
    def "Augmenting ASiC-S with #description is not allowed"() {
        given: "upload composite container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        when: "try augmenting container in session"
        Response response = datafile.tryAugmentContainer(flow)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.NO_TIMESTAMPS)

        where:
        description                 | fileName
        "signature"                 | "asicsContainerWithLtSignatureWithoutTST.scs"
        "no signature or timestamp" | "0xSIG_0xTST_asics.asics"
    }

    @Story("Allowed nested container types in ASiC-S are ASiC, BDOC and DDOC")
    def "Augmenting timestamped ASiC-S container with nested #nestedContainerType container is allowed"() {
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
        nestedContainerType | fileName                                    | firstTsTime
        "ASiC-S"            | "asicsContainerWithAsicsAndTimestamp.asics" | "2024-10-24T08:33:03Z"
        "ASiC-E"            | "asicsContainerWithAsiceAndTimestamp.asics" | "2025-01-13T17:28:36Z"
        "BDOC"              | "asicsContainerWithBdocAndTimestamp.asics"  | "2024-03-27T12:42:57Z"
        "DDOC"              | "ValidDDOCinsideAsics.asics"                | "2025-04-04T08:23:44Z"
    }

    @Story("Allowed nested container types in ASiC-S are ASiC, BDOC and DDOC")
    def "Augmenting timestamped ASiC-S container with nested #nestedFileType datafile is not allowed"() {
        given: "upload composite container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        when: "try augmenting container in session"
        Response response = datafile.tryAugmentContainer(flow)

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_DATAFILE)

        where:
        nestedFileType | fileName
        "TXT"          | "asicsContainerWithTxtFileAndTimestamp.asics"
        "PDF"          | "asicsContainerWithSignedPdfAndTimestamp.asics"
        "DOCX"         | "asicsContainerWithDocxAndTimestamp.asics"
        "CDOC"         | "asicsContainerWithCdocAndTimestamp.asics"
    }


}
