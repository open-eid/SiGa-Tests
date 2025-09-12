package ee.openeid.siga.test.step

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
        return createContainer(flow, RequestData.createHashcodeRequestDefaultBody())
    }

    Response addDefaultDataFile(Flow flow) {
        return addDataFiles(flow, RequestData.createHashcodeRequestDefaultBody())
    }

    Response uploadContainerFromFile(Flow flow, String containerName) {
        return uploadContainer(flow, RequestData.uploadHashcodeRequestBodyFromFile(containerName))
    }

    Response uploadDefaultContainer(Flow flow) {
        return uploadContainer(flow, RequestData.uploadHashcodeRequestDefaultBody())
    }

}
