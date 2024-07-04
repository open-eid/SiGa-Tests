package ee.openeid.siga.test

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.DefaultValue
import org.aeonbits.owner.Config.Key
import org.aeonbits.owner.Config.Sources

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
    List<SigaProfile> sigaProfilesActive()

    @Key("test-files-directory")
    String testFilesDirectory()

    @Key("rest-assured-console-logging")
    Boolean restAssuredConsoleLogging()

    @Key("logging.character-split-limit")
    @DefaultValue("10000")
    Integer loggingCharacterSplitLimit()

}
