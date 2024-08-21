package ee.openeid.siga.test.request

class RequestData {

    static Map uploadRequestBodyBase(String containerBase64, String containerName) {
        Map body = ["container": containerBase64]
        if (containerName) {
            body.containerName = containerName
        }
        return body
    }

    static Map uploadHashcodeRequestBody(String containerBase64) {
        return uploadRequestBodyBase(containerBase64, null)
    }

    static Map uploadDatafileRequestBody(String containerBase64, String containerName) {
        uploadRequestBodyBase(containerBase64, containerName)
    }

    static Map midSigningRequestBody(
            String personIdentifier,
            String phoneNo,
            String language,
            String signatureProfile,
            String messageToDisplay,
            String city,
            String stateOrProvince,
            String postalCode,
            String country,
            Map roles) {
        Map body = [
                "personIdentifier": personIdentifier,
                "phoneNo"         : phoneNo,
                "language"        : language,
                "signatureProfile": signatureProfile
        ]

        if (roles) {
            body.roles = roles
        }
        if (messageToDisplay) {
            body.messageToDisplay = messageToDisplay
        }
        if (city || stateOrProvince || postalCode || country) {
            body.signatureProductionPlace = buildSignatureProductionPlace(city, stateOrProvince, postalCode, country)
        }

        return body
    }

    static Object buildSignatureProductionPlace(String city, String stateOrProvince, String postalCode, String country) {
        Object signatureProductionPlace = new Object()
        if (city) {
            signatureProductionPlace.put("city", city)
        }
        if (stateOrProvince) {
            signatureProductionPlace.put("stateOrProvince", stateOrProvince)
        }
        if (postalCode) {
            signatureProductionPlace.put("postalCode", postalCode)
        }
        if (city) {
            signatureProductionPlace.put("country", country)
        }
        return signatureProductionPlace
    }

    static Map midSigningRequestBodyDefault(String personIdentifier, String phoneNo, String signatureProfile) {
        return midSigningRequestBody(
                personIdentifier,
                phoneNo,
                "EST",
                signatureProfile,
                "SiGa Test",
                null,
                null,
                null,
                null,
                null)
    }
}
