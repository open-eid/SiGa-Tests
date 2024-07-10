package ee.openeid.siga.test

import ee.openeid.siga.test.request.DatafileRequests
import ee.openeid.siga.test.request.HashcodeRequests
import ee.openeid.siga.test.step.DatafileSteps
import spock.lang.Shared
import spock.lang.Specification

abstract class GenericSpecification extends Specification {
    static BeforeAll beforeAll = BeforeAll.instance
    static DatafileRequests datafileRequests = DatafileRequests.instance
    static HashcodeRequests hashcode = HashcodeRequests.instance
    static DatafileSteps datafile = DatafileSteps.instance

    @Shared
    TestConfig conf = ConfigHolder.getConf()
}
