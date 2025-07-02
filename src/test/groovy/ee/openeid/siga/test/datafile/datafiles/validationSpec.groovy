package ee.openeid.siga.test.datafile.datafiles

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.containsInAnyOrder

@Tag("datafileContainer")
@Epic("Get datafiles (datafile)")
@Feature("Get datafiles validation")
class validationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Get timestamped ASiC-S nested container datafiles")
    def "Timestamped ASiC-S containing #nestedContainer returns datafiles from the nested container"() {
        given: "upload ASiC-S container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when:
        Response response = datafile.getDataFilesList(flow)

        then:
        response.then()
                .body("dataFiles.fileName", contains("test.txt"))
                .body("dataFiles.fileContent", contains(fileContent))

        where:
        nestedContainer | containerName                               | fileContent
        "ASiC-E"        | "asicsContainerWithAsiceAndTimestamp.asics" | "dGVzdA=="
        "ASiC-S"        | "asicsContainerWithAsicsAndTimestamp.asics" | "VGhpcyBpcyB0ZXN0IGZpbGUgZm9yIHRlc3RpbmcgcHVycG9zZSE="
        "BDOC"          | "asicsContainerWithBdocAndTimestamp.asics"  | "VGhpcyBpcyBhIHRlc3QgZmlsZS4="
        "DDOC"          | "ValidDDOCinsideAsics.asics"                | "VGVzdCBhbmQgc29tZSBvdGhlciB0ZXN0"
    }

    @Story("Get timestamped ASiC-S nested container datafiles")
    def "Timestamped ASiC-S containing nested container returns datafiles from the first level nested container: #nestedContainer"() {
        given: "upload ASiC-S container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when:
        Response response = datafile.getDataFilesList(flow)

        then:
        response.then()
                .body("dataFiles.fileName", containsInAnyOrder(dataFiles as String[]))

        where:
        nestedContainer | containerName                                           | dataFiles
        "ASiC-E"        | "asicsWithAsiceWithMultipleDatafilesAndTimestamp.asics" | ["CSV_test.csv", "PNG testfail.png", "Test.pdf", "TEST_TwoDatafiles_LT.asice"]
        "ASiC-S"        | "asicsWithAsicsAndTimestampWithAsice.asics"             | ["TEST_ESTEID2018_ASiC-E_XAdES_LT.sce"]
    }

    @Story("Get timestamped ASiC-S nested container datafiles")
    def "Timestamped ASiC-S containing datafile returns it: #datafileType"() {
        given: "upload ASiC-S container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when:
        Response response = datafile.getDataFilesList(flow)

        then:
        response.then()
                .body("dataFiles.fileName", contains(dataFile))

        where:
        datafileType | containerName                                   | dataFile
        "CDOC"       | "asicsContainerWithCdocAndTimestamp.asics"      | "test.cdoc"
        "DOCX"       | "asicsContainerWithDocxAndTimestamp.asics"      | "Test.docx"
        "PDF"        | "asicsContainerWithSignedPdfAndTimestamp.asics" | "pdfSingleSignature.pdf"
    }

}
