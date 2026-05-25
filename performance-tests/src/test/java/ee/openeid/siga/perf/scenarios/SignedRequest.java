package ee.openeid.siga.perf.scenarios;

import ee.openeid.siga.perf.config.PerfConfig;
import ee.openeid.siga.perf.util.SigaHmac;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import java.util.Map;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Builds HMAC-signed SiGa requests in two stages:
 *   1. {@link #prepare(String, Function, Function)} stashes the body, path, and X-Authorization-* headers in session.
 *   2. {@link #signedPost(String)} / {@link #signedPut(String)} / {@link #signedGet(String)} return an
 *      HttpRequestActionBuilder that reads those session keys via EL — scenarios chain {@code .check(...)} freely.
 *
 * The signable string excludes any context path, matching RequestBuilder.signRequest().
 */
public final class SignedRequest {

    public static final String PATH_KEY = "__path";
    public static final String BODY_KEY = "__body";
    public static final String TS_KEY   = "__hmacTs";
    public static final String UUID_KEY = "__hmacUuid";
    public static final String ALGO_KEY = "__hmacAlgo";
    public static final String SIG_KEY  = "__hmacSig";

    private SignedRequest() {}

    public static ChainBuilder prepare(String method,
                                       Function<Session, String> pathFn,
                                       Function<Session, String> bodyFn) {
        return exec(session -> {
            String relativePath = pathFn.apply(session);
            String body = bodyFn.apply(session);
            String fullPath = PerfConfig.basePath() + relativePath;
            Map<String, String> hdrs = SigaHmac.headers(
                    PerfConfig.SERVICE_UUID,
                    PerfConfig.SERVICE_SECRET,
                    PerfConfig.HMAC_ALGORITHM,
                    method,
                    relativePath,
                    body);
            return session
                    .set(PATH_KEY, fullPath)
                    .set(BODY_KEY, body)
                    .set(TS_KEY,   hdrs.get("X-Authorization-Timestamp"))
                    .set(UUID_KEY, hdrs.get("X-Authorization-ServiceUUID"))
                    .set(ALGO_KEY, hdrs.get("X-Authorization-Hmac-Algorithm"))
                    .set(SIG_KEY,  hdrs.get("X-Authorization-Signature"));
        });
    }

    public static HttpRequestActionBuilder signedPost(String name) {
        return withAuth(http(name).post("#{" + PATH_KEY + "}"))
                .header("Content-Type", "application/json")
                .body(StringBody("#{" + BODY_KEY + "}"));
    }

    public static HttpRequestActionBuilder signedPut(String name) {
        return withAuth(http(name).put("#{" + PATH_KEY + "}"))
                .header("Content-Type", "application/json")
                .body(StringBody("#{" + BODY_KEY + "}"));
    }

    public static HttpRequestActionBuilder signedGet(String name) {
        return withAuth(http(name).get("#{" + PATH_KEY + "}"));
    }

    private static HttpRequestActionBuilder withAuth(HttpRequestActionBuilder b) {
        return b
                .header("X-Authorization-Timestamp",      "#{" + TS_KEY   + "}")
                .header("X-Authorization-ServiceUUID",    "#{" + UUID_KEY + "}")
                .header("X-Authorization-Hmac-Algorithm", "#{" + ALGO_KEY + "}")
                .header("X-Authorization-Signature",      "#{" + SIG_KEY  + "}");
    }
}
