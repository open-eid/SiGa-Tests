package ee.openeid.siga.test.datafile.datafiles

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Get-add-delete data files (datafile)")
@Feature("Get data files validation")
class GetValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Get timestamped ASiC-S nested container data files")
    def "Timestamped ASiC-S containing #nestedContainer returns data files from the nested container"() {
        given: "upload ASiC-S container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when: "get data files"
        Response response = datafile.getDataFilesList(flow)

        then: "then data files are returned"
        response.then()
                .body("dataFiles.fileName", contains("test.txt"))
                .body("dataFiles.fileContent", contains(fileContent))

        where:
        nestedContainer | containerName                                | fileContent
        "ASiC-E"        | "timestampedAsicsWithAsice.asics"            | "dGVzdA=="
        "ASiC-S"        | "timestampedAsicsWithTimestampedAsics.asics" | "VGhpcyBpcyB0ZXN0IGZpbGUgZm9yIHRlc3RpbmcgcHVycG9zZSE="
        "BDOC"          | "asicsContainerWithBdocAndTimestamp.asics"   | "VGhpcyBpcyBhIHRlc3QgZmlsZS4="
        "DDOC"          | "ValidDDOCinsideAsics.asics"                 | "VGVzdCBhbmQgc29tZSBvdGhlciB0ZXN0"
    }

    @Story("Get timestamped ASiC-S nested container data files")
    def "Timestamped ASiC-S containing nested container returns data files from the first level nested container: #nestedContainer"() {
        given: "upload ASiC-S container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when: "get data files"
        Response response = datafile.getDataFilesList(flow)

        then: "then data files are returned"
        response.then()
                .body("dataFiles.fileName", containsInAnyOrder(dataFiles as String[]))

        where:
        nestedContainer | containerName                                          | dataFiles
        "ASiC-E"        | "timestampedAsicsWithAsiceWithMultipleDatafiles.asics" | ["CSV_test.csv", "PNG testfail.png", "Test.pdf", "TEST_TwoDatafiles_LT.asice"]
        "ASiC-S"        | "timestampedAsicsWithTimestampedAsicsWithAsice.asics"  | ["TEST_ESTEID2018_ASiC-E_XAdES_LT.sce"]
    }

    @Story("Get timestamped ASiC-S nested container data files")
    def "Timestamped ASiC-S containing datafile returns it: #datafileType"() {
        given: "upload ASiC-S container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when: "get data files"
        Response response = datafile.getDataFilesList(flow)

        then: "then data files are returned"
        response.then()
                .body("dataFiles.fileName", contains(dataFile))

        where:
        datafileType | containerName                         | dataFile
        "CDOC"       | "timestampedAsicsWithCdoc.asics"      | "test.cdoc"
        "DOCX"       | "timestampedAsicsWithDocx.asics"      | "Test.docx"
        "PDF"        | "timestampedAsicsWithSignedPdf.asics" | "pdfSingleSignature.pdf"
    }

    @Story("Get ASiC-E container data files")
    def "Uploaded ASiC-E with #description returns data file"() {
        given: "upload ASiC-E container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName + ".asice"))

        when: "get data files"
        Response response = datafile.getDataFilesList(flow)

        then: "then data files are returned"
        response.then()
                .body("dataFiles", hasSize(1),
                        "dataFiles[0].fileName", is(fileName),
                        "dataFiles[0].fileContent", is(fileContent))

        where:
        description                      | containerName                                | fileName                 | fileContent
        "no signatures"                  | "containerWithoutSignatures"                 | "test.txt"               | "c2VlIG9uIHRlc3RmYWls"
        "invalid signature"              | "unknownOcspResponder"                       | "test.txt"               | "MTIzCg=="
        "plus in datafile name"          | "plusInFileNameReferenceWithInvalidEncoding" | "Müük+hüpo+pank+muu.txt" | "dGVzdCBwbHVzc2lk"
        "special chars in datafile name" | "NonconventionalCharactersInDataFile"        | "S !¤&()=+-,@£\$€.txt"   | "UmFuZG9tIGZpbGUgY29udGVudA=="
    }

    @Story("Get ASiC-E container data files")
    def "ASiC-E without data files returns empty data files list"() {
        given: "upload ASiC-E container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerWithoutDataFiles.asice"))

        when: "get data files"
        Response response = datafile.getDataFilesList(flow)

        then: "then data files are returned"
        response.then()
                .body("dataFiles", is(empty()))
    }

    @Story("Get ASiC-E container data files")
    def "ASiC-E with multiple data files returns all data files"() {
        given: "upload ASiC-E container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerSingleSignatureTwoDatafiles.asice"))

        when: "get data files"
        Response response = datafile.getDataFilesList(flow)

        then: "then data files are returned"
        response.then()
                .body("dataFiles[0].fileName", is("test.xml"),
                        "dataFiles[0].fileContent", startsWith("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz"),

                        "dataFiles[1].fileName", is("test.txt"),
                        "dataFiles[1].fileContent", is("c2VlIG9uIHRlc3RmYWls"))
    }

    @Story("Get ASiC-E container data files")
    def "Created ASiC-E returns all data files"() {
        given: "create ASiC-E container"
        datafile.createDefaultContainer(flow)
        Map defaultFile = RequestData.createDatafileRequestDefaultBody().dataFiles[0]

        when: "get data files"
        Response response = datafile.getDataFilesList(flow)

        then: "then data files are returned"
        response.then()
                .body("dataFiles", hasSize(1),
                        "dataFiles[0].fileName", is(defaultFile.fileName),
                        "dataFiles[0].fileContent", is(defaultFile.fileContent))
    }

    @Story("Get BDOC container data files")
    def "Uploaded BDOC returns data file"() {
        given: "upload BDOC container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("valid-bdoc-tm-newer.bdoc"))

        when: "get data files"
        Response response = datafile.getDataFilesList(flow)

        then: "then data files are returned"
        response.then()
                .body("dataFiles", hasSize(1),
                        "dataFiles[0].fileName", is("test.txt"),
                        "dataFiles[0].fileContent", is("VGhpcyBpcyBhIHRlc3QgZmlsZS4="))
    }

}
