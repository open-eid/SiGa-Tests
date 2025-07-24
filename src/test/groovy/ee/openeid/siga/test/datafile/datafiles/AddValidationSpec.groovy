package ee.openeid.siga.test.datafile.datafiles

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.ContainerUtil
import ee.openeid.siga.test.util.RequestErrorValidator
import eu.europa.esig.dss.enumerations.MimeType
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.Story
import io.restassured.path.xml.XmlPath
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Get-add-delete data files (datafile)")
@Feature("Add data files validation")
class AddValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Add data file to unsigned container")
    def "Adding data file to uploaded #containerDesc is successful"() {
        given: "upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))
        datafile.getDataFilesList(flow).then().body("dataFiles", hasSize(1))

        when: "add default data file"
        datafile.addDefaultDataFile(flow)

        then: "new data file is added"
        datafile.getDataFilesList(flow).then()
                .body("dataFiles", hasSize(2))
                .body("dataFiles", hasItem(TestData.defaultDataFile()))


        where:
        containerDesc     | containerName
        "unsigned ASiC-E" | "containerWithoutSignatures.asice"
        "unsigned BDOC"   | "bdocWithoutSignature.bdoc"
    }

    @Story("Adding data file to unsigned container")
    def "Adding data file to created unsigned ASiC-E is successful"() {
        given: "create container with default data file"
        datafile.createDefaultContainer(flow)

        when: "add new data file"
        Map newDatafile = ["fileName": "test.txt", "fileContent": "c2VlIG9uIHRlc3RmYWls"]
        datafile.addDataFiles(flow, RequestData.addDatafileRequestBody([newDatafile]))

        then: "new data file is added"
        datafile.getDataFilesList(flow).then()
                .body("dataFiles", containsInAnyOrder(newDatafile, TestData.defaultDataFile()))
    }

    @Story("Add data file to unsigned container")
    def "Adding multiple data files to unsigned #containerAction container is successful"() {
        given: "upload unsigned container"
        switch (containerAction) {
            case "uploaded" -> datafile.uploadContainer(flow,
                    RequestData.uploadDatafileRequestBodyFromFile("containerWithoutSignatures.asice"))
            case "created" -> datafile.createDefaultContainer(flow)
        }

        when: "add two data files"
        Map firstDataFile = ["fileName": "testFile1.xml", "fileContent": "cmFuZG9tdGV4dA=="]
        Map secondDataFile = ["fileName": "testFile2.xml", "fileContent": "dGVzdA=="]

        datafile.addDataFiles(flow, RequestData.addDatafileRequestBody([firstDataFile, secondDataFile]))

        then: "new data files are added"
        datafile.getDataFilesList(flow).then()
                .body("dataFiles", hasSize(3))
                .body("dataFiles", hasItems(firstDataFile, secondDataFile))

        where:
        containerAction | _
        "uploaded"      | _
        "created"       | _
    }

    @Story("Add data file to unsigned container")
    def "Adding data files with different MIME types is successful and correct MIME types are assigned"() {
        given: "create default container"
        datafile.createDefaultContainer(flow)
        String defaultFileContent = TestData.defaultDataFile().fileContent

        when: "add data files with different file extensions"
        List addedDataFiles = TestData.FILE_EXTENSIONS.collect { String ext ->
            [fileName: "filename.${ext}", fileContent: defaultFileContent]
        }
        datafile.addDataFiles(flow, RequestData.addDatafileRequestBody(addedDataFiles))

        then: "in manifest.xml for added data files correct MIME types are assigned"
        XmlPath manifest = ContainerUtil.manifestAsXmlPath(datafile.getContainer(flow).path("container").toString(),
                "META-INF/manifest.xml")

        TestData.FILE_EXTENSIONS.eachWithIndex { ext, i ->
            def expectedMimeType = MimeType.fromFileName("*.$ext").mimeTypeString
            assertThat(expectedMimeType, is(manifest.getString("manifest:manifest.manifest:file-entry[${2 + i}].@manifest:media-type")))
        }
    }

    @Story("Adding duplicate data file is not allowed")
    def "Adding a second data file with #fileName name and #fileContent content is #result"() {
        given: "create container"
        datafile.createDefaultContainer(flow)

        when: "try adding data file"
        Response response = datafile.tryAddDataFiles(flow, RequestData.addDatafileRequestBody([dataFile]))

        then: "result is returned"
        switch (result) {
            case "not allowed" -> RequestErrorValidator.validate(
                    response,
                    RequestError.DUPLICATE_DATAFILE.errorCode,
                    RequestError.DUPLICATE_DATAFILE.getMessage("testing.txt"))
            case "allowed" -> response.then().statusCode(200)
        }

        where:
        fileName    | fileContent | dataFile                                                        || result
        "duplicate" | "duplicate" | TestData.defaultDataFile()                                      || "not allowed"
        "duplicate" | "different" | ["fileName": "testing.txt", "fileContent": "dGVzdA=="]          || "not allowed"
        "unique"    | "duplicate" | ["fileName": "New Name.txt", "fileContent": "cmFuZG9tdGV4dA=="] || "allowed"
    }

    @Story("Adding data file to unsupported container types not allowed")
    def "Adding data file to #containerDesc is not allowed"() {
        given: "upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when: "try adding default data file"
        Response response = datafile.tryAddDataFiles(flow, RequestData.addDatafileRequestBody([TestData.defaultDataFile()]))

        then: "error is returned"
        RequestErrorValidator.validate(response, error)

        where:
        containerDesc          | containerName                                 || error
        "signed ASiC-E"        | TestData.DEFAULT_ASICE_CONTAINER_NAME         || RequestError.SIGNATURE_PRESENT
        "signed BDOC"          | "valid-bdoc-tm-newer.bdoc"                    || RequestError.SIGNATURE_PRESENT
        "signed ASiC-S"        | "asicsContainerWithLtSignatureWithoutTST.scs" || RequestError.SIGNATURE_PRESENT
        "timestamped ASiC-S"   | TestData.DEFAULT_ASICS_CONTAINER_NAME         || RequestError.TIMESTAMP_PRESENT
        "untimestamped ASiC-S" | "0xSIG_0xTST_asics.asics"                     || RequestError.INVALID_DATAFILE_CONTAINER
    }

    @Story("Adding data file with forbidden characters in fileName returns error")
    def "Trying to add data file returns error, when fileName contains #invalidChar"() {
        given: "upload unsigned container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerWithoutSignatures.asice"))

        when: "try adding data file with invalid char in fileName"
        Map dataFile = TestData.defaultDataFile()
        dataFile.fileName = "Char=${invalidChar}isInvalid"

        Response response = datafile.tryAddDataFiles(flow, RequestData.addDatafileRequestBody([dataFile]))

        then: "then error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_DATAFILE_NAME)

        where:
        invalidChar << ["/", "`", "?", "*", "\\", "<", ">", "|", "\"", ":", "\u0017", "\u0000", "\u0007"]
    }

    @Issue("SIGA-1121")
    @Issue("SIGA-1122")
    @Story("Adding data file with invalid data is not allowed")
    def "Trying to add a data file with #fileDescription is not allowed"() {
        given: "upload unsigned container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerWithoutSignatures.asice"))

        when: "try adding data file with invalid data"
        Response response = datafile.tryAddDataFiles(flow, RequestData.addDatafileRequestBody([dataFile]))

        then: "error is returned"
        RequestErrorValidator.validate(response, error)

        where:
        fileDescription   | dataFile                                           || error
        "empty content"   | ["fileName": "testing.txt", "fileContent": ""]      | RequestError.INVALID_DATAFILE_CONTENT
        "invalid content" | ["fileName": "testing.txt", "fileContent": "abc"]   | RequestError.INVALID_DATAFILE_CONTENT
        "empty name"      | ["fileName": "", "fileContent": "cmFuZG9tdGV4dA=="] | RequestError.INVALID_DATAFILE_NAME
        "empty list"      | []                                                  | RequestError.INVALID_JSON
//        "additional data" | ["fileName": "testing.txt", "fileContent": "cmFuZG9tdGV4dA==", "extraField": "extra"]
    }

}
