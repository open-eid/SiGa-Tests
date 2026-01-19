package ee.openeid.siga.test

import ee.openeid.siga.test.model.SigaProfile
import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.ConverterClass
import org.aeonbits.owner.Config.DefaultValue
import org.aeonbits.owner.Config.Key
import org.aeonbits.owner.Config.Sources
import org.aeonbits.owner.Converter

import java.lang.reflect.Method

@Sources(["classpath:application.properties"])
interface TestConfig extends Config {

    @Key("siga.application-context-path")
    String sigaContextPath()

    @Key("siga.hostname")
    String sigaHostname()

    @Key("siga.port")
    String sigaPort()

    @Key("siga.protocol")
    String sigaProtocol()

    @Key("siga.profiles.active")
    @ConverterClass(SigaProfileConverter.class)
    List<SigaProfile> sigaProfilesActive()

    @Key("test-files-directory")
    String testFilesDirectory()

    @Key("rest-assured-console-logging")
    Boolean restAssuredConsoleLogging()

    @Key("test-sid-accounts-file")
    String testSidAccountsFilePath()
}

class SigaProfileConverter implements Converter<SigaProfile> {
    @Override
    SigaProfile convert(Method method, String input) {
        SigaProfile.fromName(input)
    }
}
