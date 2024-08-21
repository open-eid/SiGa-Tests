package ee.openeid.siga.test

import ee.openeid.siga.test.request.DatafileRequests
import ee.openeid.siga.test.request.HashcodeRequests
import ee.openeid.siga.test.step.DatafileSteps
import ee.openeid.siga.test.step.HashcodeSteps
import spock.lang.Shared
import spock.lang.Specification

abstract class GenericSpecification extends Specification {
    static BeforeAll beforeAll = BeforeAll.instance
    static DatafileRequests datafileRequests = DatafileRequests.instance
    static DatafileSteps datafile = DatafileSteps.instance
    static HashcodeRequests hashcodeRequests = HashcodeRequests.instance
    static HashcodeSteps hashcode = HashcodeSteps.instance

    @Shared
    TestConfig conf = ConfigHolder.getConf()
}
