package ee.openeid.siga.test.request

@Singleton
class DatafileRequests extends SigaRequests {

    String getBasePath() {
        return "/containers"
    }
}
