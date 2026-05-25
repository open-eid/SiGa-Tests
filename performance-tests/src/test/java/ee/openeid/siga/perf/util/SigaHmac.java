package ee.openeid.siga.perf.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;

public final class SigaHmac {

    private SigaHmac() {}

    public static String hmac(String key, String data, String algo) {
        try {
            Mac mac = Mac.getInstance(algo);
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algo));
            return Hex.encodeHexString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HMAC computation failed", e);
        }
    }

    public static String signableString(String serviceUuid, String timestamp, String method, String path, String body) {
        int lastSlash = path.lastIndexOf('/');
        String prefix = path.substring(0, lastSlash + 1);
        String lastSegment = path.substring(lastSlash + 1);
        String encodedLast = URLEncoder.encode(lastSegment, StandardCharsets.UTF_8);
        return serviceUuid + ":" + timestamp + ":" + method + ":" + prefix + encodedLast + ":" + body;
    }

    public static Map<String, String> headers(String serviceUuid,
                                              String serviceSecret,
                                              String hmacAlgorithm,
                                              String method,
                                              String path,
                                              String body) {
        String timestamp = Long.toString(Instant.now().getEpochSecond());
        String signature = hmac(serviceSecret, signableString(serviceUuid, timestamp, method, path, body), hmacAlgorithm);
        return Map.of(
                "X-Authorization-Timestamp", timestamp,
                "X-Authorization-ServiceUUID", serviceUuid,
                "X-Authorization-Hmac-Algorithm", hmacAlgorithm,
                "X-Authorization-Signature", signature
        );
    }
}
