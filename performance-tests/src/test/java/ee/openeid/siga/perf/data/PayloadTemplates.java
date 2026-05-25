package ee.openeid.siga.perf.data;

/**
 * JSON request bodies for the SiGa happy flows. Hand-rolled strings — small, dependency-free,
 * and identical in shape to RequestData.groovy / RequestBuilder.java in the parent project.
 */
public final class PayloadTemplates {

    public static final String DEFAULT_FILENAME = "perftest.txt";
    public static final String DEFAULT_DATAFILE_CONTENT_BASE64 = "cmFuZG9tdGV4dA==";
    public static final String DEFAULT_CONTAINER_NAME = "perftest.asice";

    private PayloadTemplates() {}

    public static String createDatafileContainer() {
        return "{"
                + "\"containerName\":\"" + DEFAULT_CONTAINER_NAME + "\","
                + "\"dataFiles\":[{"
                + "\"fileName\":\"" + DEFAULT_FILENAME + "\","
                + "\"fileContent\":\"" + DEFAULT_DATAFILE_CONTENT_BASE64 + "\""
                + "}]"
                + "}";
    }

    public static String midStart(String personIdentifier, String phoneNo) {
        return "{"
                + "\"personIdentifier\":\"" + personIdentifier + "\","
                + "\"phoneNo\":\"" + phoneNo + "\","
                + "\"language\":\"EST\","
                + "\"signatureProfile\":\"LT\""
                + "}";
    }

    public static String sidCertChoice(String personIdentifier, String country) {
        return "{"
                + "\"personIdentifier\":\"" + personIdentifier + "\","
                + "\"country\":\"" + country + "\""
                + "}";
    }

    public static String sidStart(String documentNumber) {
        return "{"
                + "\"documentNumber\":\"" + documentNumber + "\","
                + "\"signatureProfile\":\"LT\""
                + "}";
    }

    public static String remoteSigningStart(String signingCertificateBase64) {
        return "{"
                + "\"signingCertificate\":\"" + signingCertificateBase64 + "\","
                + "\"signatureProfile\":\"LT\""
                + "}";
    }

    public static String remoteSigningFinalize(String signatureValueBase64) {
        return "{"
                + "\"signatureValue\":\"" + signatureValueBase64 + "\""
                + "}";
    }
}
