package ee.openeid.siga.test.request

@Singleton
class HashcodeRequests extends SigaRequests {

    String getBasePath() {
        return "/hashcodecontainers"
    }
}
