package ee.openeid.siga.test.util

import io.restassured.path.xml.XmlPath
import io.restassured.path.xml.config.XmlPathConfig
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel

class ContainerUtil {

    static ZipFile bytesToZipFile(byte[] zipBytes) {
        return ZipFile.builder().setSeekableByteChannel(new SeekableInMemoryByteChannel(zipBytes)).get()
    }

    static ZipFile base64ToZipFile(String base64Zip) {
        return bytesToZipFile(Base64.decoder.decode(base64Zip))
    }

    static Set getZipStructure(ZipFile zipFile) {
        Set structure = []
        zipFile.entries.each { entry -> structure << entry.name }
        return structure
    }

    static byte[] extractEntryBytesFromZipFile(ZipFile zipFile, String entryPath) {
        ZipArchiveEntry entry = zipFile.getEntry(entryPath)
        if (entry == null) {
            throw new IllegalStateException("Entry '${entryPath}' not found.")
        }
        return zipFile.getInputStream(entry).readAllBytes()
    }

    static byte[] extractEntryBytesFromBase64Container(String containerBase64String, String entryPath) {
        return extractEntryBytesFromZipFile(base64ToZipFile(containerBase64String), entryPath)
    }

    static String extractEntryFromZipFile(ZipFile zipFile, String entryPath) {
        return new String(extractEntryBytesFromZipFile(zipFile, entryPath))
    }

    static XmlPath configureXmlPathForManifest(XmlPath xmlPath) {
        return xmlPath.using(XmlPathConfig.xmlPathConfig().declaredNamespace("manifest",
                "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0"))
    }

    static XmlPath manifestAsXmlPath(byte[] manifestBytes) {
        return new ByteArrayInputStream(manifestBytes).withCloseable { inputStream ->
            configureXmlPathForManifest(XmlPath.from(inputStream))
        }
    }

    static XmlPath manifestAsXmlPath(String containerBase64String, String entryPath) {
        return manifestAsXmlPath(extractEntryBytesFromBase64Container(containerBase64String, entryPath))
    }

    static void assertZipFilesEqualInExactOrder(byte[] expectedZipBytes, byte[] actualZipBytes) {
        bytesToZipFile(expectedZipBytes).withCloseable { ZipFile expectedZip ->
            bytesToZipFile(actualZipBytes).withCloseable { ZipFile actualZip ->
                List<ZipArchiveEntry> expectedEntries = expectedZip.entriesInPhysicalOrder.toList()
                List<ZipArchiveEntry> actualEntries = actualZip.entriesInPhysicalOrder.toList()
                assert expectedEntries*.name == actualEntries*.name:
                        "ZIP entry names or order not equal"
                expectedEntries.eachWithIndex { ZipArchiveEntry expectedEntry, int i ->
                    ZipArchiveEntry actualEntry = actualEntries[i]
                    assert expectedEntry.method == actualEntry.method:
                            "ZIP entry compression method not equal: ${expectedEntry.name}"
                    assert Arrays.equals(
                            expectedZip.getInputStream(expectedEntry).readAllBytes(),
                            actualZip.getInputStream(actualEntry).readAllBytes()):
                            "ZIP entry bytes not equal: ${expectedEntry.name}"
                }
            }
        }
    }

}
