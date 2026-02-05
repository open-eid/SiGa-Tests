package ee.openeid.siga.test.accounts

class SmartIdAccount {

    String key
    String documentNumber
    String country
    String personalCode
    String commonName
    String serialNumber

    static SmartIdAccount fromMap(String accountKey, Map accountData) {
        SmartIdAccount account = new SmartIdAccount()
        account.with {
            key = accountKey
            documentNumber = accountData.documentNumber
            country = accountData.country
            personalCode = accountData.personalCode
            commonName = accountData.commonName
            serialNumber = accountData.documentNumber.toString().split('-').take(2).join('-')
        }
        return account
    }

    static SmartIdAccount defaultSigner() {
        SmartIdAccounts.byRole("defaultSigner")
    }
}
