package ee.openeid.siga.test.accounts

import ee.openeid.siga.test.ConfigHolder
import ee.openeid.siga.test.TestConfig
import groovy.json.JsonSlurper

class SmartIdAccounts {

    private static Map cachedAccounts // make sure that accounts are loaded once per test run
    static TestConfig conf = ConfigHolder.getConf()

    static SmartIdAccount byRole(String role) {
        Map sidAccounts = load()
        String accountKey = sidAccounts.roles[role]
        assert accountKey: "Unknown Smart-ID role '${role}'. Available roles: ${sidAccounts.roles.keySet()}"
        return byAccount(accountKey)
    }

    static SmartIdAccount byAccount(String accountKey) {
        Map sidAccounts = load()
        Map account = sidAccounts.accounts[accountKey] as Map
        assert account: "Unknown Smart-ID account key '${accountKey}'. Available keys: ${sidAccounts.accounts.keySet()}"
        SmartIdAccount.fromMap(accountKey, account)
    }

    private static Map load() {
        if (cachedAccounts != null) return cachedAccounts

        String location = conf.testSidAccountsFilePath()
        assert location: "Missing property test-sid-accounts-file"

        InputStream is = openLocation(location)
        Map sidAccounts = new JsonSlurper().parse(is) as Map

        // minimal validation (fail fast)
        assert sidAccounts?.accounts instanceof Map
        assert sidAccounts?.roles instanceof Map

        cachedAccounts = sidAccounts
        return cachedAccounts
    }

    private static InputStream openLocation(String location) {
        if (location.startsWith("classpath:")) {
            def path = location - "classpath:"
            def is = SmartIdAccounts.classLoader.getResourceAsStream(path)
            assert is: "Accounts file not found on classpath: ${path}"
            return is
        }
        if (location.startsWith("file:")) {
            return new File(new URI(location)).newInputStream()
        }
        // fallback: treat as plain filesystem path
        return new File(location).newInputStream()
    }
}
