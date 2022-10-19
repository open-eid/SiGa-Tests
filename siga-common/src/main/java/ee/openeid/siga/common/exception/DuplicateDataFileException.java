package ee.openeid.siga.common.exception;

import static ee.openeid.siga.common.exception.ErrorResponseCode.DUPLICATE_DATA_FILE_EXCEPTION;

public class DuplicateDataFileException extends SigaApiException {

    public DuplicateDataFileException(String message) {
        super(DUPLICATE_DATA_FILE_EXCEPTION, message);
    }
}
