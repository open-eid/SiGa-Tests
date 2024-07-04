package ee.openeid.siga.test

class SigaRequests {

    static TestConfig conf = ConfigHolder.getConf()
    static String sigaServiceUrl = "${conf.sigaProtocol()}://${conf.sigaHostname()}:${conf.sigaPort()}${conf.sigaContextPath()}"

}
