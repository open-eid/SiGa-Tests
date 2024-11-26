package ee.openeid.siga.test.request

import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.util.Utils
import org.apache.commons.codec.binary.Base64

class RequestData {

    static Map createHashcodeRequestBody(List dataFiles) {
        [dataFiles: dataFiles]
    }

    static Map uploadHashcodeRequestBody(String containerBase64) {
        [container: containerBase64]
    }

    static Map uploadHashcodeRequestBodyFromFile(String fileName) {
        uploadHashcodeRequestBody(Base64.encodeBase64String(Utils.readFileFromResources(fileName)))
    }

    static Map uploadDatafileRequestBody(String containerBase64, String containerName) {
        [container    : containerBase64,
         containerName: containerName]
    }

    static Map uploadDatafileRequestBodyFromFile(String containerName) {
        uploadDatafileRequestBody(Base64.encodeBase64String(Utils.readFileFromResources(containerName)), containerName)
    }

    static Map createDatafileRequestDefaultBody() {
        [containerName: "datafileContainerSingleSignature.asice",
         dataFiles    : [["fileName"   : "testing.txt",
                          "fileContent": "cmFuZG9tdGV4dA=="]]
        ]
    }

    static Map signatureProductionPlace() {
        [countryName    : "Riik !\"#\$%&'()*+,-/",
         city           : "Linn :;<=>?@ ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{}~",
         stateOrProvince: "Maakond £€§½ŠšŽžÕõÄäÖöÜü",
         postalCode     : "Suunakood 0123456789"]
    }

    static Map midSigningRequestBody(String personIdentifier,
                                     String phoneNo,
                                     String language = "EST",
                                     String signatureProfile = "LT",
                                     List<String> roles = null,
                                     String messageToDisplay = null,
                                     Map signatureProductionPlace = null) {
        Map body = ["personIdentifier": personIdentifier,
                    "phoneNo"         : phoneNo,
                    "language"        : language,
                    "signatureProfile": signatureProfile]

        if (roles) {
            body.roles = roles
        }
        if (messageToDisplay) {
            body.messageToDisplay = messageToDisplay
        }
        if (signatureProductionPlace) {
            body.signatureProductionPlace = signatureProductionPlace
        }
        return body
    }

    static Map midSigningRequestBodyMinimal(String personIdentifier, String phoneNo) {
        return midSigningRequestBody(personIdentifier, phoneNo)
    }

    static Map midSigningRequestDefaultBody() {
        return midSigningRequestBody("60001019906", "+37200000766")
    }

    static Map remoteSigningStartDefaultRequest() {
        ["signingCertificate": TestData.SIGNER_CERT_ESTEID2018_PEM,
         "signatureProfile"  : "LT"]
    }

    static Map remoteSigningFinalizeRequest(String signatureValue) {
        ["signatureValue": signatureValue]
    }

    static Map sidCertificateChoiceRequestDefaultBody() {
        ["personIdentifier": "30303039914",
         "country"         : "EE"]
    }

    static Map sidSigningRequestDefaultBody(String documentNumber) {
        ["documentNumber"  : documentNumber,
         "signatureProfile": "LT"]
    }
}
