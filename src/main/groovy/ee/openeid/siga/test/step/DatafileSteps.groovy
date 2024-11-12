package ee.openeid.siga.test.step

import ee.openeid.siga.test.request.DatafileRequests
import ee.openeid.siga.test.request.SigaRequests

@Singleton
class DatafileSteps extends RequestSteps {

    @Override
    SigaRequests getInstance() {
        return DatafileRequests.instance
    }
}
