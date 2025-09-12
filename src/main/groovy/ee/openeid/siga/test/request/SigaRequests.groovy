package ee.openeid.siga.test.request

import ee.openeid.siga.test.ConfigHolder
import ee.openeid.siga.test.TestConfig
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.utils.HmacSigner
import groovy.json.JsonOutput
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.http.Method
import io.restassured.specification.RequestSpecification
import org.springframework.web.util.UriUtils

import java.nio.charset.StandardCharsets
import java.time.Instant

import static io.restassured.config.EncoderConfig.encoderConfig

abstract class SigaRequests {

    static TestConfig conf = ConfigHolder.getConf()
    static String sigaServiceUrl = "${conf.sigaProtocol()}://${conf.sigaHostname()}:${conf.sigaPort()}${conf.sigaContextPath()}"

    abstract String getBasePath()

    static RequestSpecification sigaRequestBase(Flow flow, Method method, String endpoint, String requestBody) {
        RequestSpecification requestSpecification =
                RestAssured.given()
                        .header("X-Authorization-Signature", signRequest(flow, requestBody ?: "", method, endpoint))
                        .header("X-Authorization-Timestamp", flow.getSigningTime())
                        .header("X-Authorization-ServiceUUID", flow.getServiceUuid())
                        .header("X-Authorization-Hmac-Algorithm", flow.getHmacAlgorithm())
                        .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                        .contentType(ContentType.JSON)
                        .baseUri(sigaServiceUrl)
                        .basePath(endpoint)
        if (requestBody != null) {
            requestSpecification.body(requestBody)
        }
        requestSpecification.metaClass.allureStepName = "${method} ${endpoint}"
        return requestSpecification
    }

    static RequestSpecification sigaRequestBase(Flow flow, Method method, String endpoint, Map requestBody) {
        sigaRequestBase(flow, method, endpoint, JsonOutput.toJson(requestBody))
    }

    static RequestSpecification sigaRequestBase(Flow flow, Method method, String endpoint) {
        sigaRequestBase(flow, method, endpoint, null)
    }

    static String signRequest(Flow flow, String requestBody, Method method, String url) {
        if (!flow.getForceSigningTime()) {
            flow.setSigningTime(Instant.now().getEpochSecond().toString())
        }

        String urlEncodeString = UriUtils.encode(url.substring(url.lastIndexOf("/") + 1), StandardCharsets.UTF_8.toString())

        String signableString = flow.getServiceUuid() + ":" + flow.getSigningTime() + ":" + method.toString() + ":" + url.substring(0, url.lastIndexOf("/") + 1) + urlEncodeString + ":" + requestBody

        return HmacSigner.generateHmacSignature(flow.getServiceSecret(), signableString, flow.getHmacAlgorithm())
    }

    RequestSpecification createContainerRequest(Flow flow, Method method, Map requestBody) {
        String endpoint = getBasePath()
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification uploadContainerRequest(Flow flow, Method method, Map requestBody) {
        String endpoint = "/upload${getBasePath()}"
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification addDataFilesRequest(Flow flow, Method method, Map requestBody) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/datafiles"
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification getDataFilesRequest(Flow flow, Method method) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/datafiles"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification deleteDataFileRequest(Flow flow, Method method, String datafileName) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/datafiles/${datafileName}"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification startRemoteSigningRequest(Flow flow, Method method, Map requestBody) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/remotesigning"
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification finalizeRemoteSigningRequest(Flow flow, Method method, Map requestBody, String signatureId) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/remotesigning/${signatureId}"
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification startMidSigningRequest(Flow flow, Method method, Map requestBody) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/mobileidsigning"
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification getMidSigningStatusRequest(Flow flow, Method method, String signatureId) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/mobileidsigning/${signatureId}/status"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification startSidCertificateChoiceRequest(Flow flow, Method method, Map requestBody) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/smartidsigning/certificatechoice"
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification getSidCertificateStatusRequest(Flow flow, Method method, String certificateId) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/smartidsigning/certificatechoice/${certificateId}/status"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification startSidSigningRequest(Flow flow, Method method, Map requestBody) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/smartidsigning"
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification getSidSigningStatusRequest(Flow flow, Method method, String signatureId) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/smartidsigning/${signatureId}/status"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification getSignatureListRequest(Flow flow, Method method) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/signatures"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification getSignatureInfoRequest(Flow flow, Method method, String signatureId) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/signatures/${signatureId}"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification getTimestampListRequest(Flow flow, Method method) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/timestamps"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification getValidationReportInSessionRequest(Flow flow, Method method) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/validationreport"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification getValidationReportWithoutSessionRequest(Flow flow, Method method, Map requestBody) {
        String endpoint = "${getBasePath()}/validationreport"
        return sigaRequestBase(flow, method, endpoint, requestBody)
    }

    RequestSpecification getContainerRequest(Flow flow, Method method) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification deleteContainerRequest(Flow flow, Method method) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}"
        return sigaRequestBase(flow, method, endpoint)
    }

    RequestSpecification augmentationContainerRequest(Flow flow, Method method) {
        String endpoint = "${getBasePath()}/${flow.getContainerId()}/augmentation"
        return sigaRequestBase(flow, method, endpoint)
    }
}
