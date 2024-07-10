package ee.openeid.siga.test.step


import ee.openeid.siga.test.request.HashcodeRequests
import ee.openeid.siga.test.request.SigaRequests

@Singleton
class HashcodeSteps extends RequestSteps {

    @Override
    SigaRequests getIntance() {
        return HashcodeRequests.instance
    }
}
