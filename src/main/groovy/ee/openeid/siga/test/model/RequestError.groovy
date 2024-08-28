package ee.openeid.siga.test.model

enum RequestError {
    INVALID_CALLING_CODE(CommonErrorCode.INVALID_REQUEST, "Invalid international calling code"),
    INVALID_PERSON_ID(CommonErrorCode.INVALID_REQUEST,"Invalid person identifier"),
    INVALID_PHONE(CommonErrorCode.INVALID_REQUEST,"Invalid phone No.")

    final String errorCode
    final String errorMessage

    RequestError(String errorCode, String errorMessage) {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
    }
}

final class CommonErrorCode {
    static final INVALID_REQUEST = "REQUEST_VALIDATION_EXCEPTION"
}
