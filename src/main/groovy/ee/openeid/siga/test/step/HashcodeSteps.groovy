package ee.openeid.siga.test.step

import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.*
import io.restassured.response.Response

@Singleton
class HashcodeSteps extends RequestSteps {

    @Override
    SigaRequests getInstance() {
        return HashcodeRequests.instance
    }

    Response createDefaultContainer(Flow flow) {
        return createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultHashcodeDataFile()]))
    }

    Response addDefaultDataFile(Flow flow) {
        return addDataFiles(flow, RequestData.createHashcodeRequestBody([TestData.defaultHashcodeDataFile()]))
    }

    Response uploadContainerFromFile(Flow flow, String containerName) {
        return uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile(containerName))
    }

}
