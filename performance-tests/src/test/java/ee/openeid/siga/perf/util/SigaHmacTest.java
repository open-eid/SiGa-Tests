package ee.openeid.siga.perf.util;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SigaHmacTest {

    private static final String UUID   = "a7fd7728-a3ea-4975-bfab-f240a67e894f";
    private static final String SECRET = "746573745365637265744b6579303031";
    private static final String ALGO   = "HmacSHA256";

    @Test
    void hmac_matches_reference_jdk_implementation() throws Exception {
        String data = "hello-siga";
        String ours = SigaHmac.hmac(SECRET, data, ALGO);
        assertEquals(referenceHmacHex(SECRET, data, ALGO), ours);
    }

    @Test
    void signableString_matches_RequestBuilder_signRequest_format() {
        String got = SigaHmac.signableString(UUID, "1700000000", "POST", "/containers", "{}");
        assertEquals(UUID + ":1700000000:POST:/containers:{}", got);
    }

    @Test
    void signableString_url_encodes_only_last_segment() {
        String got = SigaHmac.signableString(UUID, "1700000000", "GET", "/containers/abc def/mobileidsigning", "");
        assertEquals(UUID + ":1700000000:GET:/containers/abc def/mobileidsigning:", got);
        String got2 = SigaHmac.signableString(UUID, "1700000000", "GET", "/path/with space", "");
        assertEquals(UUID + ":1700000000:GET:/path/with+space:", got2);
    }

    @Test
    void headers_returns_all_four_authorization_keys() {
        Map<String, String> h = SigaHmac.headers(UUID, SECRET, ALGO, "POST", "/containers", "{}");
        assertEquals(4, h.size());
        assertNotNull(h.get("X-Authorization-Timestamp"));
        assertEquals(UUID, h.get("X-Authorization-ServiceUUID"));
        assertEquals(ALGO, h.get("X-Authorization-Hmac-Algorithm"));
        assertEquals(64, h.get("X-Authorization-Signature").length()); // SHA-256 hex = 64 chars
    }

    private static String referenceHmacHex(String key, String data, String algo) throws Exception {
        Mac mac = Mac.getInstance(algo);
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algo));
        byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(raw.length * 2);
        for (byte b : raw) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
