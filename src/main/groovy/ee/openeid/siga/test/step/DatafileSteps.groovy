package ee.openeid.siga.test.step


import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.DatafileRequests
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.request.SigaRequests
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
}
