package ee.openeid.siga.test

import ee.openeid.siga.test.util.AllureRestAssuredWithStep
import ee.openeid.siga.test.util.Utils
import io.restassured.RestAssured
import io.restassured.filter.Filter

@Singleton(strict = false)
class BeforeAll {

    TestConfig conf = ConfigHolder.getConf()

    BeforeAll() {

        // Rest Assured settings
        // Log all requests and responses in allure report
//        RestAssured.filters(new AllureRestAssuredWithStep())
//         Temporary solution to prevent log duplication in Allure report. TODO: remove once JUnit tests are removed.
        addRestAssuredFilterSafely(new AllureRestAssuredWithStep())
        // Relax validation
        RestAssured.useRelaxedHTTPSValidation()
        // Log requests and responses to console for debugging
        // Enabled when not running in docker (i.e. running locally) or when toggled in configuration
        if (Utils.isLocal() || conf.restAssuredConsoleLogging()) {
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
