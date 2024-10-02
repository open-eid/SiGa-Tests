package ee.openeid.siga.test.step

import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.HashcodeRequests
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.request.SigaRequests
import io.restassured.response.Response

@Singleton
class HashcodeSteps extends RequestSteps {

    @Override
    SigaRequests getIntance() {
        return HashcodeRequests.instance
    }

    Response createDefaultContainer(Flow flow) {
        return createContainer(flow, RequestData.createHashcodeRequestBody([TestData.defaultFile()]))
    }
}
