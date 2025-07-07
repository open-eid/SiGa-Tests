package ee.openeid.siga.test.datafile.upload

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

@Tag("datafileContainer")
@Epic("Upload container (datafile)")
@Feature("Upload container validation")
class ValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Uploading ASiC-S container")
    def "Uploading ASiC-S with #description is not allowed"() {
        when: "try uploading container"
        Response response = datafile.tryUploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(fileName))

        then: "error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_CONTAINER)

        where:
        description                     | fileName
        "XAdES signature and timestamp" | "XadesMixedWithTst.asics"
        "CAdES signature and timestamp" | "CadesMixedWithTst.asics"
        "CAdES signature"               | "cadesAsicsWithDdoc.asics"
        "additional folder"             | "AdditionalFolderInAsics.asics"
        "datafile missing"              | "DataFileMissingAsics.asics"
    }

}
