package ee.openeid.siga.testgroovy.hashcode

import ee.openeid.siga.test.model.SigaApiFlow
import ee.openeid.siga.testgroovy.helper.TestBaseSpecification
import io.restassured.response.Response
import spock.lang.Unroll

import static ee.openeid.siga.test.helper.TestData.*
import static ee.openeid.siga.test.utils.RequestBuilder.*
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.MatcherAssert.assertThat

class NegativeApiScenariosSpec extends TestBaseSpecification {

    private SigaApiFlow flow

    def setup() {
        flow = SigaApiFlow.buildForTestClient1Service1()
    }

    def "DELETE to create hashcode container should fail"() {
        expect:
        Response response = delete(HASHCODE_CONTAINERS, flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "DELETE to hashcode data files list should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile("hashcodeWithoutSignature.asice"))

        Response response = delete(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES, flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "DELETE to upload hashcode container should fail"() {
        expect:
        Response response = delete(UPLOAD + HASHCODE_CONTAINERS, flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "POST to hashcode data file should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = post(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES + "/" + getDataFileList(flow).getBody().path("dataFiles[0].fileName"), flow, "request")

        expectError(response, 405, INVALID_REQUEST)
    }

    def "POST to hashcode container should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequest(DEFAULT_HASHCODE_CONTAINER))

        Response response = post(getContainerEndpoint() + "/" + flow.getContainerId(), flow, "")

        expectError(response, 405, INVALID_REQUEST)
    }

    // PUT requests that should fail
    @Unroll
    def "PUT to hashcode #resourceUri should fail"() {
        expect:
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault())

        Response response = put(getContainerEndpoint() + "/" + flow.getContainerId() + resourceUri, flow, "request")

        expectError(response, status, errorCode)

        where:
        resourceUri                          || status || errorCode
        DATAFILES + "/" + "testing.txt"      || 405    || INVALID_REQUEST
    }

    def "PUT to create hashcode container should fail"() {
        expect:
        Response response = put(HASHCODE_CONTAINERS, flow, hashcodeContainersDataRequestWithDefault().toString())

        expectError(response, 405, INVALID_REQUEST)
    }

    def "PUT to hashcode data files list should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = put(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES, flow, "request")

        expectError(response, 405, INVALID_REQUEST)
    }

    def "PUT to hashcode data file should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = put(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES + "/" + getDataFileList(flow).getBody().path("dataFiles[0].fileName"), flow, "request")

        expectError(response, 405, INVALID_REQUEST)
    }

    def "PUT to upload hashcode container should fail"() {
        expect:
        Response response = put(UPLOAD + HASHCODE_CONTAINERS, flow, hashcodeContainerRequest(DEFAULT_HASHCODE_CONTAINER).toString())

        expectError(response, 405, INVALID_REQUEST)
    }

    //GET requests that should fail
    @Unroll
    def "GET to hashcode #resourceUri should fail"() {
        expect:
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault())

        Response response = get(getContainerEndpoint() + "/" + flow.getContainerId() + resourceUri, flow)

        expectError(response, status, errorCode)

        where:
        resourceUri                          || status || errorCode
        DATAFILES + "/" + "testing.txt"      || 405    || INVALID_REQUEST
    }

    def "GET to create hashcode container should fail"() {
        expect:
        Response response = get(HASHCODE_CONTAINERS, flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "GET to hashcode data fail should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = get(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES + "/" + getDataFileList(flow).getBody().path("dataFiles[0].fileName"), flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "GET to upload hashcode container should fail"() {
        expect:
        Response response = get(UPLOAD + HASHCODE_CONTAINERS, flow)

        expectError(response, 405, INVALID_REQUEST)
    }


    //HEAD requests that should fail
    @Unroll
    def "HEAD to hashcode #resourceUri should fail"() {
        expect:
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault())

        Response response = head(getContainerEndpoint() + "/" + flow.getContainerId() + resourceUri, flow)

        assertThat(response.statusCode(), equalTo(status))

        where:
        resourceUri                          || status
        DATAFILES + "/" + "testing.txt"      || 405
    }

    def "HEAD to create hashcode container should fail"() {
        expect:
        Response response = head(HASHCODE_CONTAINERS, flow)

        assertThat(response.statusCode(), equalTo(405))
    }

    def "HEAD to hashcode data file should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = head(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES + "/" + getDataFileList(flow).getBody().path("dataFiles[0].fileName"), flow)

        assertThat(response.statusCode(), equalTo(405))
    }

    def "HEAD to  upload hashcode container should fail"() {
        expect:
        Response response = head(UPLOAD + HASHCODE_CONTAINERS, flow)

        assertThat(response.statusCode(), equalTo(405))
    }

    //OPTIONS requests that should fail
    @Unroll
    def "OPTIONS to hashcode #resourceUri should fail"() {
        expect:
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault())

        Response response = options(getContainerEndpoint() + "/" + flow.getContainerId() + resourceUri, flow)

        expectError(response, status, errorCode)

        where:
        resourceUri                          || status || errorCode
        DATAFILES + "/" + "testing.txt"      || 405    || INVALID_REQUEST
    }

    def "OPTIONS to create hashcode container should fail"() {
        expect:
        Response response = options(HASHCODE_CONTAINERS, flow)

        assertThat(response.statusCode(), equalTo(405))
    }

    def "OPTIONS to hashcode data files list should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = options(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES, flow)

        assertThat(response.statusCode(), equalTo(405))
    }

    def "OPTIONS to hashcode data file should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = options(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES + "/" + getDataFileList(flow).getBody().path("dataFiles[0].fileName"), flow)

        assertThat(response.statusCode(), equalTo(405))
    }

    def "OPTIONS to get hashcode container should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequest(DEFAULT_HASHCODE_CONTAINER))

        Response response = options(getContainerEndpoint() + "/" + flow.getContainerId(), flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "OPTIONS to upload hashcode container should fail"() {
        expect:
        Response response = options(UPLOAD + HASHCODE_CONTAINERS, flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    //PATCH requests that should fail
    @Unroll
    def "PATCH to hashcode #resourceUri should fail"() {
        expect:
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault())

        Response response = patch(getContainerEndpoint() + "/" + flow.getContainerId() + resourceUri, flow)

        expectError(response, status, errorCode)

        where:
        resourceUri                          || status || errorCode
        DATAFILES + "/" + "testing.txt"      || 405    || INVALID_REQUEST
    }

    def "PATCH to create hashcode container should fail"() {
        expect:
        Response response = patch(HASHCODE_CONTAINERS, flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "PATCH to hashcode data files list should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = patch(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES, flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "PATCH to hashcode data file should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequestFromFile(DEFAULT_HASHCODE_CONTAINER_NAME))

        Response response = patch(getContainerEndpoint() + "/" + flow.getContainerId() + DATAFILES + "/" + getDataFileList(flow).getBody().path("dataFiles[0].fileName"), flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "PATCH to get hashcode container should fail"() {
        expect:
        postUploadContainer(flow, hashcodeContainerRequest(DEFAULT_HASHCODE_CONTAINER))

        Response response = patch(getContainerEndpoint() + "/" + flow.getContainerId(), flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    def "PATCH to upload hashcode container should fail"() {
        expect:
        Response response = patch(UPLOAD + HASHCODE_CONTAINERS, flow)

        expectError(response, 405, INVALID_REQUEST)
    }

    @Override
    String getContainerEndpoint() {
        HASHCODE_CONTAINERS
    }
}
