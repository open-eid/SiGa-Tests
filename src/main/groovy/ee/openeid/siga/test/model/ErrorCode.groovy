package ee.openeid.siga.test.model

enum ErrorCode {
    // MID error codes
    SENDING_ERROR,
    USER_CANCEL,
    NOT_VALID,
    PHONE_ABSENT,
    EXPIRED_TRANSACTION,

    INVALID_REQUEST,
    RESOURCE_NOT_FOUND,
    CLIENT_EXCEPTION,
    MID_EXCEPTION,
    NOT_FOUND,
    SIM_ERROR,
    INVALID_CONTAINER,
    AUTHORIZATION_ERROR,
    INVALID_SIGNATURE,
    INVALID_LANGUAGE,
    INVALID_DATA,
    DUPLICATE_DATA_FILE,
    INVALID_CERTIFICATE_EXCEPTION,
    INVALID_SIGNATURE_EXCEPTION,
    SMARTID_EXCEPTION,
    USER_SELECTED_WRONG_VC,
    CONNECTION_LIMIT_EXCEPTION,
    REQUEST_SIZE_LIMIT_EXCEPTION,
    INVALID_SESSION_DATA_EXCEPTION
}
