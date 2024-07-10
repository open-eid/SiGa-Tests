package ee.openeid.siga.test.model

enum Client {
    CLIENT1("client1", "5f923dee-4e6f-4987-bce0-36ad9647ba58", [Service.SERVICE1, Service.SERVICE2, Service.SERVICE4]),
    CLIENT2("client2", "5f923dee-4e6f-4987-bce0-36ad9647ba58", [Service.SERVICE3, Service.SERVICE5, Service.SERVICE6, Service.SERVICE7, Service.SERVICE8]),
    CLIENT3("client3", "5f923dee-4e6f-4987-bce0-36ad9647ba58", [Service.SERVICE9]),

    final String name
    final String uuid
    final List<Service> services

    Client(String name, String uuid, List<Service> services) {
        this.name = name
        this.uuid = uuid
        this.services = services
    }
}
