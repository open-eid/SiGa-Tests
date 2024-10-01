package ee.openeid.siga.test.model

enum RequestError {
    INVALID_CALLING_CODE(CommonErrorCode.INVALID_REQUEST, "Invalid international calling code"),
    INVALID_PERSON_ID(CommonErrorCode.INVALID_REQUEST, "Invalid person identifier"),
    INVALID_PHONE(CommonErrorCode.INVALID_REQUEST, "Invalid phone No."),
    INVALID_LANGUAGE(CommonErrorCode.INVALID_REQUEST, "Invalid Mobile-Id language"),
    INVALID_PROFILE(CommonErrorCode.INVALID_REQUEST, "Invalid signature profile"),
    INVALID_EMPTY_DATAFILE(CommonErrorCode.INVALID_SESSION_DATA, "Unable to sign container with empty datafiles"),
    INVALID_TYPE_MID(CommonErrorCode.INVALID_SESSION_DATA, "Unable to finalize signature for signing type: MOBILE_ID"),
    INVALID_CHANGED_DATAFILE(CommonErrorCode.INVALID_SESSION_DATA, "Unable to finalize signature. Container data files have been changed after signing was initiated. Repeat signing process"),
    INVALID_RESOURCE("RESOURCE_NOT_FOUND_EXCEPTION", "Session not found")

    final String errorCode
    final String errorMessage

    RequestError(String errorCode, String errorMessage) {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
    }
}

final class CommonErrorCode {
    static final INVALID_REQUEST = "REQUEST_VALIDATION_EXCEPTION"
    static final INVALID_SESSION_DATA = "INVALID_SESSION_DATA_EXCEPTION"
}
