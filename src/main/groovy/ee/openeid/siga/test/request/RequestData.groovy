package ee.openeid.siga.test.request

class RequestData {

    static Map uploadDatafileRequestBody(String containerBase64, String containerName) {
        [container    : containerBase64,
         containerName: containerName]
    }

    static Map uploadHashcodeRequestBody(String containerBase64) {
        [container: containerBase64]
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
}