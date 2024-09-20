package ee.openeid.siga.test.util

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class EnumNameMatcher<T extends Enum<T>> extends TypeSafeMatcher<String> {

    private final T expectedEnumConstant

    EnumNameMatcher(T expectedEnumConstant) {
        this.expectedEnumConstant = expectedEnumConstant
    }

    @Override
    protected boolean matchesSafely(String actualString) {
        return expectedEnumConstant.name() == actualString
    }

    @Override
    void describeTo(Description description) {
        description.appendText("a string matching the enum constant: ${expectedEnumConstant.name()}")
    }

    // Static factory method to create the matcher
    static <T extends Enum<T>> EnumNameMatcher<T> matchesEnumName(T enumConstant) {
        return new EnumNameMatcher<>(enumConstant)
    }
}
