package ee.openeid.siga.test.util

import io.restassured.path.xml.XmlPath
import io.restassured.path.xml.config.XmlPathConfig
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel

class ContainerUtil {

    static ZipFile base64ToZipFile(String base64Zip) {
        byte[] zipBytes = Base64.decoder.decode(base64Zip)
        ZipFile zipFile = ZipFile.builder().setSeekableByteChannel(new SeekableInMemoryByteChannel(zipBytes)).get()
        return zipFile
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
}
