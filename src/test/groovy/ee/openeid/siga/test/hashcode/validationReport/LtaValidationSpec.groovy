package ee.openeid.siga.test.hashcode.validationReport

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.SignatureFormat
import io.qameta.allure.*
import io.restassured.response.Response

import static ee.openeid.siga.test.util.EnumNameMatcher.matchesEnumName
import static org.hamcrest.Matchers.*

@Epic("Validation report (hashcode)")
@Feature("LTA signature validation")
class LtaValidationSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("LTA hashcode signature is valid at LongTermData validation level")
    def "Validating LTA hashcode container with #description succeeds at LongTermData validation level although archive timestamps fail"() {
        when: "validate container without session"
        Response response = hashcode.validateContainerFromFile(flow, containerName)

        then: "LTA signatures pass without errors, archive timestamps are reported as failed with LTA warning"
        response.then().rootPath("validationConclusion.")
                .body("validationLevel", is("LONG_TERM_DATA"))
                .body("validSignaturesCount", is(signatureCount))
                .body("signatures", hasSize(signatureCount))
                .body("signatures.signatureFormat", everyItem(matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA)))
                .body("signatures.indication", everyItem(is("TOTAL-PASSED")))
                .body("signatures.errors.flatten()", empty())
                .body("signatures.warnings.content", everyItem(hasItem(TestData.LTA_ATS_NO_EFFECT_AT_LONG_TERM_DATA)))

                .body("signatures.info.archiveTimeStamps.flatten()", hasSize(archiveTimeStampCount))
                .body("signatures.info.archiveTimeStamps.flatten().indication", everyItem(is("FAILED")))
                .body("signatures.info.archiveTimeStamps.flatten().subIndication", everyItem(is("HASH_FAILURE")))

        where:
        description                                                          | containerName                                              | signatureCount | archiveTimeStampCount
        "one LTA signature + one archive timestamp"                          | "hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LTA_DigiDoc4.asice" | 1              | 1
        "two LTA signatures + two archive timestamps"                        | "hashcode_TEST_ESTEID2018_ASiC-E_XAdES_LTA+LTA.asice"      | 2              | 2
        "one LTA signature + two archive timestamps"                         | "hashcode_TEST_ESTEID2018_ASiC-E_XAdES_LTAx2.asice"        | 1              | 2
        "one LTA signature + two archive timestamps, one from untrusted TSA" | "hashcode_2xLTA-SK+Entrust.asice"                          | 1              | 2
    }

    @Story("LTA warning not produced for not-LTA hashcode signature")
    def "When validating hashcode container that has both LTA and LT, then LTA warning is added only to LTA signature"() {
        when: "validate container without session"
        Response response = hashcode.validateContainerFromFile(flow, "hashcode_TEST_ESTEID2018_ASiC-E_XAdES_LT+LTA.asice")

        then: "only LTA signature has LTA warning"
        response.then().rootPath("validationConclusion.")
                .body("validationLevel", is("LONG_TERM_DATA"))
                .body("validSignaturesCount", is(2))
                .body("signatures", hasSize(2))
                .body("signatures.signatureFormat", containsInAnyOrder(
                        matchesEnumName(SignatureFormat.XAdES_BASELINE_LT), matchesEnumName(SignatureFormat.XAdES_BASELINE_LTA)))
                .body("signatures.find { it.signatureFormat == 'XAdES_BASELINE_LT' }.warnings.content",
                        not(hasItem(TestData.LTA_ATS_NO_EFFECT_AT_LONG_TERM_DATA)))
                .body("signatures.find { it.signatureFormat == 'XAdES_BASELINE_LTA' }.warnings.content",
                        hasItem(TestData.LTA_ATS_NO_EFFECT_AT_LONG_TERM_DATA))
    }

    @Story("LongTermData validation level does not hide signature errors")
    def "Validating LTA hashcode container fails at LongTermData validation level if #description"() {
        when: "validate container without session"
        Response response = hashcode.validateContainerFromFile(flow, containerName)

        then: "signature errors and warnings are both shown"
        response.then().rootPath("validationConclusion.")
                .body("validationLevel", is("LONG_TERM_DATA"))
                .body("validSignaturesCount", is(0))
                .body("signatures", hasSize(1))
                .body("signatures[0].indication", is(indication))
                .body("signatures[0].subIndication", is(subIndication))
                .body("signatures[0].errors.content", hasItem(error))
                .body("signatures[0].warnings.content", hasItem(TestData.LTA_ATS_NO_EFFECT_AT_LONG_TERM_DATA))

                .body("signatures[0].info.archiveTimeStamps", hasSize(1))
                .body("signatures[0].info.archiveTimeStamps[0].indication", is("FAILED"))

        where:
        description                            | containerName                                                         | indication      | subIndication                       | error
        "datafile hash is wrong"               | "hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LTA_InvalidDatafileHash.asice" | "TOTAL-FAILED"  | "HASH_FAILURE"                      | "The reference data object is not intact!"
        "signature timestamp is not qualified" | "hashcode_LTA_TSA_QTSA.asice"                                         | "TOTAL-FAILED"  | null                                | "The certificate is not related to a TSA/QTST!"
        "AIA OCSP certificate is expired"      | "hashcode_esteid2018signerAiaOcspLTA.asice"                           | "INDETERMINATE" | "CERTIFICATE_CHAIN_GENERAL_FAILURE" | "No acceptable revocation data for the certificate!"
    }

    @Story("Validation report combines SiGa and SiVa warnings")
    def "Validating LTA hashcode container with empty datafile reports LTA warning together with empty datafile warning"() {
        when: "validate container without session"
        Response response = hashcode.validateContainerFromFile(flow, "hashcode_TEST_ESTEID2025_ASiC-E_XAdES_LTA_EmptyDatafileSize.asice")

        then: "SiVa LTA warning and SiGa empty datafile warning are both reported"
        response.then().rootPath("validationConclusion.")
                .body("validationLevel", is("LONG_TERM_DATA"))
                .body("validSignaturesCount", is(1))
                .body("signatures", hasSize(1))
                .body("signatures[0].indication", is("TOTAL-PASSED"))
                .body("signatures[0].warnings.content", hasItems(
                        TestData.LTA_ATS_NO_EFFECT_AT_LONG_TERM_DATA,
                        String.format(TestData.EMPTY_DATAFILE_WARNING, "test.txt")))
    }

}
