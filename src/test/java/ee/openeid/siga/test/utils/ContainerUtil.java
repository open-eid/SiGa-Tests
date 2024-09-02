package ee.openeid.siga.test.utils;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.config.XmlPathConfig;
import lombok.experimental.UtilityClass;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static ee.openeid.siga.test.helper.TestData.HASHCODE_SHA512;

@UtilityClass
public final class ContainerUtil {

    public static final String MANIFEST_NAMESPACE_PREFIX = "manifest";
    public static final String MANIFEST_NAMESPACE_URL = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";

    public static byte[] extractEntryFromContainer(String entryPath, String containerBase64String) {
        return extractEntryFromContainer(entryPath, Base64.getDecoder().decode(containerBase64String));
    }

    public static byte[] extractEntryFromContainer(String entryPath, byte[] containerBytes) {
        try (SeekableInMemoryByteChannel byteChannel = new SeekableInMemoryByteChannel(containerBytes);
             ZipFile zipFile = new ZipFile(byteChannel)) {
            return extractEntryFromZipFile(entryPath, zipFile);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read container", e);
        }
    }

    private static byte[] extractEntryFromZipFile(String entryPath, ZipFile zipFile) throws IOException {
        ZipArchiveEntry zipEntry = Optional.ofNullable(zipFile.getEntry(entryPath))
                .orElseThrow(() -> new IllegalStateException("No entry " + entryPath + " found"));
        return extractEntryFromZipFile(zipEntry, zipFile);
    }

    private static byte[] extractEntryFromZipFile(ZipArchiveEntry zipEntry, ZipFile zipFile) throws IOException {
        if (zipEntry.isDirectory()) {
            throw new IllegalStateException("Entry " + zipEntry.getName() + " is a directory");
        }

        try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
            return inputStream.readAllBytes();
        }
    }

    public static XmlPath manifestAsXmlPath(String manifestXmlString) {
        return configureXmlPathForManifest(XmlPath.from(manifestXmlString));
    }

    public static XmlPath manifestAsXmlPath(byte[] manifestBytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(manifestBytes)) {
            return configureXmlPathForManifest(XmlPath.from(byteArrayInputStream));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read manifest", e);
        }
    }

    public static XmlPath manifestAsXmlPath(String entryPath, String containerBase64String) {
        return manifestAsXmlPath(extractEntryFromContainer(entryPath, containerBase64String));
    }

    public static XmlPath configureXmlPathForManifest(XmlPath xmlPath) {
        return xmlPath.using(XmlPathConfig.xmlPathConfig().declaredNamespace(MANIFEST_NAMESPACE_PREFIX, MANIFEST_NAMESPACE_URL));
    }

    public static XmlPath hashcodeDataFileAsXmlPath(String entryPath, String containerBase64String) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(extractEntryFromContainer(entryPath, containerBase64String))) {
            return XmlPath.from(byteArrayInputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read hashcode file", e);
        }
    }

    public static XmlPath signaturesFileAsXmlPath(String entryPath, String containerBase64String) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(extractEntryFromContainer(entryPath, containerBase64String))) {
            return XmlPath.from(byteArrayInputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read signatures file", e);
        }
    }

    public static boolean getHashcodeSha512FilePresent(String container) {
        try {
            hashcodeDataFileAsXmlPath(HASHCODE_SHA512, container);
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("No entry META-INF/hashcodes-sha512.xml found")) {
                return false;
            }
        }
        return true;
    }

    public static List<Pair<ZipEntry, byte[]>> getAllZipEntries(ZipInputStream zipInputStream) {
        List<Pair<ZipEntry, byte[]>> extractedZipEntries = new ArrayList<>();
        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                extractedZipEntries.add(Pair.of(zipEntry, IOUtils.toByteArray(zipInputStream)));
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to extract ZIP entries", e);
        }
        return extractedZipEntries;
    }

    public static void assertZipFilesEqual_entriesInExactOrder(byte[] expectedZipFileBytes, byte[] actualZipFileBytes) {
        try (
                ZipInputStream expectedZipInputStream = new ZipInputStream(new ByteArrayInputStream(expectedZipFileBytes));
                ZipInputStream actualZipInputStream = new ZipInputStream(new ByteArrayInputStream(actualZipFileBytes));
        ) {
            assertZipFilesEqual_entriesInExactOrder(expectedZipInputStream, actualZipInputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to map byte arrays to ZIP input streams");
        }
    }

    public static void assertZipFilesEqual_entriesInExactOrder(ZipInputStream expectedZipInputStream, ZipInputStream actualZipInputStream) {
        List<Pair<ZipEntry, byte[]>> expectedZipEntries = getAllZipEntries(expectedZipInputStream);
        List<Pair<ZipEntry, byte[]>> actualZipEntries = getAllZipEntries(actualZipInputStream);

        int expectedZipEntryCount = expectedZipEntries.size();
        Assertions.assertEquals(expectedZipEntryCount, actualZipEntries.size(), String.format(
                "ZIP file ZIP entry count mismatch: expected %d, actual %d", expectedZipEntryCount, actualZipEntries.size()));

        for (int i = 0; i < expectedZipEntryCount; ++i) {
            Pair<ZipEntry, byte[]> expectedZipEntry = expectedZipEntries.get(i);
            Pair<ZipEntry, byte[]> actualZipEntry = actualZipEntries.get(i);

            assertZipEntriesEqual(expectedZipEntry.getKey(), actualZipEntry.getKey());
            Assertions.assertArrayEquals(expectedZipEntry.getValue(), actualZipEntry.getValue(),
                    "ZIP entry bytes not equal: " + expectedZipEntry.getKey().getName());
        }
    }

    private static void assertZipEntriesEqual(ZipEntry expected, ZipEntry actual) {
        Assertions.assertEquals(expected.getName(), actual.getName(), String.format("Container ZIP entry name mismatch: expected %s, actual %s", expected.getName(), actual.getName()));
        Assertions.assertEquals(expected.getMethod(), actual.getMethod(), String.format("Container ZIP entry compression method mismatch: expected %d, actual %d",
                expected.getMethod(), actual.getMethod()));
    }

}
