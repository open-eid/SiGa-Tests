package ee.openeid.siga.test.hashcode.upload

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.response.Response

@Epic("Upload container (hashcode)")
@Feature("Upload container")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Upload container checks")
    def "Upload invalid container #description returns error: '#error.getMessage()'"() {
        expect:
        Response validateResponse = hashcode.tryUploadContainerFromFile(flow, containerName)
        RequestErrorValidator.validate(validateResponse, error)

        where:
        description                       | containerName                                        || error
        "with empty SHA256/512 XML files" | "hashcodeSignedContainerWithEmptyHashXmlFiles.asice" || RequestError.INVALID_HASH_CONTAINER
        "without SHA256/512 XML files"    | "hashcodeMissingShaXmlFiles.asice"                   || RequestError.INVALID_CONTAINER_NO_HASHES
        "which is completely empty"       | "hashcodeEmptyContainer.asice"                       || RequestError.INVALID_HASH_CONTAINER_2
        "as compromised ZIP"              | "hashcodeInvalidZipEocdCompromised.asice"            || RequestError.INVALID_ZIP_FORMAT
        "format PDF"                      | "pdfSingleTestSignature.pdf"                         || RequestError.INVALID_ZIP_FORMAT
    }
}
