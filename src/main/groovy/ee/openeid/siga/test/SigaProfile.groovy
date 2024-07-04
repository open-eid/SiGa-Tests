package ee.openeid.siga.test

enum SigaProfile {
    DIGIDOC4J_PROD(name: "digidoc4jProd"),
    DIGIDOC4J_TEST(name: "digidoc4jTest"),
    DIGIDOC4J_PERF(name: "digidoc4jPerf"),
    MOBILE_ID(name: "mobileId"),
    SMART_ID(name: "smartId"),
    DATA_FILE_CONTAINER(name: "datafileContainer")

    final String name

    SigaProfile(Map<String, String> params) {
        this.name = params.name
    }
}
