package ee.openeid.siga.test.util

import io.qameta.allure.Allure
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.filter.FilterContext
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification

class AllureRestAssuredWithStep extends AllureRestAssured {
    @Override
    Response filter(final FilterableRequestSpecification requestSpec,
                    final FilterableResponseSpecification responseSpec,
                    final FilterContext filterContext) {
        if (requestSpec.hasProperty("allureStepName") && requestSpec?.allureStepName) {
            Response response = stepWithValue(requestSpec.allureStepName) {
                return super.filter(requestSpec, responseSpec, filterContext)
            }
            return response
        } else {
            return super.filter(requestSpec, responseSpec, filterContext)
        }
    }

    static stepWithValue(String name, Runnable closure) {
        return Allure.step(name, closure as Allure.ThrowableRunnable)
    }
}
