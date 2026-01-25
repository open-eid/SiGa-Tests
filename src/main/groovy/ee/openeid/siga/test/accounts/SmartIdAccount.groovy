package ee.openeid.siga.test.accounts

class SmartIdAccount {

    String key
    String documentNumber
    String country
    String personalCode
    String commonName
    String serialNumber

    static SmartIdAccount fromMap(String key, Map accountData) {
        SmartIdAccount account = new SmartIdAccount()
        account.key = key
        account.documentNumber = accountData.documentNumber
        account.country = accountData.country
        account.personalCode = accountData.personalCode
        account.commonName = accountData.commonName
        account.serialNumber = accountData.documentNumber.toString().split('-').take(2).join('-')
        return account
    }

    static SmartIdAccount defaultSigner() {
        SmartIdAccounts.byRole("defaultSigner")
    }
}
