package ee.openeid.siga.perf.config;

public final class PerfConfig {

    public static final String PROTOCOL       = sys("siga.protocol",        "https");
    public static final String HOST           = sys("siga.host",            "localhost");
    public static final String PORT           = sys("siga.port",            "8443");
    public static final String CONTEXT_PATH   = sys("siga.contextPath",     "");

    public static final String SERVICE_UUID   = sys("siga.serviceUuid",     "a7fd7728-a3ea-4975-bfab-f240a67e894f");
    public static final String SERVICE_SECRET = sys("siga.serviceSecret",   "746573745365637265744b6579303031");
    public static final String HMAC_ALGORITHM = sys("siga.hmacAlgorithm",   "HmacSHA256");

    public static final String MID_PERSON_ID  = sys("siga.midPersonId",     "60001019906");
    public static final String MID_PHONE      = sys("siga.midPhone",        "+37200000766");

    public static final String SID_PERSON_ID  = sys("siga.sidPersonId",     "50001029996");
    public static final String SID_COUNTRY    = sys("siga.sidCountry",      "EE");

    public static final String MODE             = sys("mode",                "ramp");
    public static final int    USERS            = sysInt("users",            10);
    public static final int    RAMP_SECONDS     = sysInt("ramp",             10);
    public static final double FLOWS_PER_MINUTE = sysDouble("flowsPerMinute", 60.0);
    public static final int    DURATION_SECONDS = sysInt("durationSeconds",  60);
    public static final int    WARMUP_SECONDS   = sysInt("warmupSeconds",    0);
    public static final int    POLL_INTERVAL_MS = sysInt("pollIntervalMs",   3500);
    public static final int    POLL_TIMEOUT_MS  = sysInt("pollTimeoutMs",    300000);

    public static final double WEIGHT_MID     = sysDouble("weights.mid",    33.0);
    public static final double WEIGHT_SID     = sysDouble("weights.sid",    33.0);
    public static final double WEIGHT_REMOTE  = sysDouble("weights.remote", 34.0);

    private PerfConfig() {}

    public static String baseUrl() {
        return PROTOCOL + "://" + HOST + ":" + PORT + CONTEXT_PATH;
    }

    public static String basePath() {
        return CONTEXT_PATH;
    }

    public static int maxPollIterations() {
        return Math.max(1, POLL_TIMEOUT_MS / Math.max(1, POLL_INTERVAL_MS));
    }

    private static String sys(String key, String def) {
        String v = System.getProperty(key);
        return (v == null || v.isBlank()) ? def : v;
    }

    private static int sysInt(String key, int def) {
        String v = System.getProperty(key);
        return (v == null || v.isBlank()) ? def : Integer.parseInt(v);
    }

    private static double sysDouble(String key, double def) {
        String v = System.getProperty(key);
        return (v == null || v.isBlank()) ? def : Double.parseDouble(v);
    }
}
