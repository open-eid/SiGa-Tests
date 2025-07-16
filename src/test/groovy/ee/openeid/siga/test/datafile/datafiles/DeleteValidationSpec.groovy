package ee.openeid.siga.test.datafile.datafiles

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.helper.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response
import spock.lang.Tag

import static org.hamcrest.Matchers.*

@Tag("datafileContainer")
@Epic("Get-add-delete data files (datafile)")
@Feature("Delete data files validation")
class DeleteValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Delete unsigned container data file")
    def "When #containerDesc with data file #containerAction, then data file deletion is successful"() {
        given: "create/upload container"
        switch (containerAction) {
            case "created" -> datafile.createDefaultContainer(flow)
            case "uploaded" -> datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))
        }
        String dataFileName = datafile.getDataFilesList(flow).path("dataFiles[0].fileName")

        when: "delete data file"
        datafile.deleteDataFile(flow, dataFileName)

        then: "data file not present"
        datafile.getDataFilesList(flow).then().body("dataFiles", is(empty()))

        where:
        containerDesc          | containerAction | containerName
        "unsigned ASiC-E"      | "uploaded"      | "containerWithoutSignatures.asice"
        "unsigned BDOC"        | "uploaded"      | "bdocWithoutSignature.bdoc"
        "untimestamped ASiC-S" | "uploaded"      | "0xSIG_0xTST_asics.asics"
        "new ASiC-E"           | "created"       | _
    }

    @Story("Delete unsigned container data file")
    def "When multiple files present in unsigned container, then only requested data file is deleted"() {
        given: "upload container with multiple datafiles"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("asiceWithoutSignatureWithMultipleDataFiles.asice"))

        when: "delete one data file"
        datafile.deleteDataFile(flow, "test.txt")

        then: "deleted data file not in data files list"
        datafile.getDataFilesList(flow).then().body("dataFiles.fileName", containsInAnyOrder("test.xml", "S !¤&()=+-,@£\$€.txt"))
    }

    @Story("Delete unsigned container data file")
    def "Deleting data file with special characters in file name is successful"() {
        given: "upload ASiC-E container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("NonconventionalCharactersInDataFile.asice"))
        String dataFileName = datafile.getDataFilesList(flow).path("dataFiles[0].fileName")

        when: "delete data file"
        datafile.deleteDataFile(flow, dataFileName)

        then: "data file not present"
        datafile.getDataFilesList(flow).then().body("dataFiles", is(empty()))
    }

    @Story("Deleting datafiles from signed container is not allowed")
    def "Data file deletion not allowed, when #containerType is uploaded"() {
        given: "create/upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))
        String dataFileName = datafile.getDataFilesList(flow).path("dataFiles[0].fileName")

        when: "try deleting data file"
        Response response = datafile.tryDeleteDataFile(flow, dataFileName)

        then: "error is returned"
        RequestErrorValidator.validate(response, error)

        where:
        containerType        | containerName                                 || error
        "signed ASiC-E"      | TestData.DEFAULT_ASICE_CONTAINER_NAME         || RequestError.SIGNATURE_PRESENT
        "signed BDOC"        | "valid-bdoc-tm-newer.bdoc"                    || RequestError.SIGNATURE_PRESENT
        "signed ASiC-S"      | "asicsContainerWithLtSignatureWithoutTST.scs" || RequestError.SIGNATURE_PRESENT
        "timestamped ASiC-S" | TestData.DEFAULT_ASICS_CONTAINER_NAME         || RequestError.TIMESTAMP_PRESENT
    }

    @Story("Deleting not existing datafiles returns error")
    def "Trying to delete not existing data file on uploaded #containerType returns error"() {
        given: "create/upload container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))

        when: "try deleting data file"
        Response response = datafile.tryDeleteDataFile(flow, "random name.txt")

        then: "error is returned"
        response.then()
                .body("errorCode", is("RESOURCE_NOT_FOUND_EXCEPTION"),
                        "errorMessage", is("Data file named random name.txt not found"))

        where:
        containerType | containerName
        "ASiC-E"      | "containerWithoutSignatures.asice"
        "ASiC-S"      | "0xSIG_0xTST_asics.asics"
    }

    // Some invalid characters (e.g. /, ?, \, \u0000) are excluded from this test
    // because they appear in the URL and trigger different errors unrelated to filename validation.
    @Story("Deleting data file with forbidden characters in fileName returns error")
    def "Trying to delete data file returns error, when fileName contains #invalidChar"() {
        given: "upload ASiC-E container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile("containerWithoutSignatures.asice"))

        when: "try deleting data file with invalid char in fileName"
        Response response = datafile.tryDeleteDataFile(flow, "Char=${invalidChar}isInvalid")

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_DATAFILE_NAME)

        where:
        invalidChar << ["`", "*", "<", ">", "|", "\"", ":", "\u0017", "\u0007"]
    }

}
