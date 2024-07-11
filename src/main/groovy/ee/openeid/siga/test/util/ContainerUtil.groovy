package ee.openeid.siga.test.util

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

    static String extractEntryFromZipFile(ZipFile zipFile, String entryPath) {
        return new String(extractEntryBytesFromZipFile(zipFile, entryPath))
    }
}
