package ee.openeid.siga.test.model

enum Service {
    SERVICE1("test1.service.ee", "a7fd7728-a3ea-4975-bfab-f240a67e894f", "746573745365637265744b6579303031"),
    SERVICE2("test2.service.ee", "824dcfe9-5c26-4d76-829a-e6630f434746", "746573745365637265744b6579303032"),
    SERVICE3("test3.service.ee", "400ff9a2-b5fb-4fde-b764-9b519963f82e", "746573745365637265744b6579303033"),
    SERVICE4("test4.service.ee", "1975daf1-4e8a-46b4-8cd9-442291da2108", ""),
    SERVICE5("test5.service.ee", "156ead6d-b884-46b1-aba5-d8cd1a573b1b", "02m5uss0zjDTjIwkME4zYTprtbT5fGnA"),
    SERVICE6("test6.service.ee", "cfbdce49-0ec9-4b83-8b41-d22655ea4741", "746573745365637265744b6579303036"),
    SERVICE7("test7.service.ee", "7dc75cb8-7076-4bed-9f06-b304f85cdccd", "746573745365637265744b6579303037"),
    SERVICE8("test8.service.ee", "3df75ca1-4272-4fcd-af01-b304f85bcccd", ""),
    SERVICE9("test9.service.ee", "b2246ed8-1f6a-4db6-8e4f-e7e664259a7d", "326573745365637265714b6579303029")

    final String name
    final String uuid
    final String secret

    Service(String name, String uuid, String secret) {
        this.name = name
        this.uuid = uuid
        this.secret = secret
    }
}
