package ee.openeid.siga.test.model

import groovy.transform.Canonical
import io.restassured.response.Response

import java.security.InvalidParameterException

@Canonical
class Flow {

    String containerId

    String signingTime
    Boolean forceSigningTime
    Response midStatus
    Response sidStatus
    Response sidCertificateStatus

    String serviceUuid
    String serviceSecret
    String hmacAlgorithm

    static Flow buildForTestClientService(Client client, Service service) {
        if (service !in client.services) {
            throw new InvalidParameterException("Service ${service.name} doesn't belong to client ${client.name}.")
        }
        return new Flow([serviceUuid     : service.uuid,
                         serviceSecret   : service.secret,
                         forceSigningTime: false,
                         hmacAlgorithm   : "HmacSHA256"])
    }

    static Flow buildForDefaultTestClientService() {
        buildForTestClientService(Client.CLIENT1, Service.SERVICE1)
    }
}
