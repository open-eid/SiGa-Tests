package ee.openeid.siga.test.step

import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.*
import io.restassured.response.Response

@Singleton
class DatafileSteps extends RequestSteps {

    @Override
    SigaRequests getInstance() {
        return DatafileRequests.instance
    }

    Response createDefaultContainer(Flow flow) {
        return createContainer(flow, RequestData.createDatafileRequestDefaultBody())
    }

    Response addDefaultDataFile(Flow flow) {
        return addDataFiles(flow, RequestData.addDatafileRequestBody([TestData.defaultDataFile()]))
    }

    Response uploadContainerFromFile(Flow flow, String containerName) {
        return uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(containerName))
    }

    Response uploadDefaultContainer(Flow flow) {
        return uploadContainer(flow, RequestData.uploadDatafileRequestDefaultBody())
    }

}
