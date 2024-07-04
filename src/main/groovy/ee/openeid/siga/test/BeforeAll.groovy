package ee.openeid.siga.test

import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.filter.Filter

class BeforeAll {

    TestConfig conf = ConfigHolder.getConf()

    BeforeAll() {

        // Rest Assured settings
        // Log all requests and responses in allure report
//        RestAssured.filters(new AllureRestAssured())
//         Temporary solution to prevent log duplication in Allure report. TODO: remove once JUnit tests are removed.
        addRestAssuredFilterSafely(new AllureRestAssured())
        // Relax validation
        RestAssured.useRelaxedHTTPSValidation()
        // Log requests and responses to console for debugging
        if (conf.restAssuredConsoleLogging()) {
//            RestAssured.filters(new LoggingFilter(conf.loggingCharacterSplitLimit()))
//             Temporary solution to prevent log duplication in Allure report. TODO: remove once JUnit tests are removed.
            addRestAssuredFilterSafely(new LoggingFilter(conf.loggingCharacterSplitLimit()))
        }
    }

    static void addRestAssuredFilterSafely(Filter filter) {
        if (RestAssured.filters().stream().noneMatch { f -> (f.metaClass.theClass.name == filter.metaClass.theClass.name) }) {
            RestAssured.filters(filter)
        }
    }
}
