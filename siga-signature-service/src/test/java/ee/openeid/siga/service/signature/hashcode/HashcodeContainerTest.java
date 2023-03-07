package ee.openeid.siga.service.signature.hashcode;

import ee.openeid.siga.common.exception.InvalidContainerException;
import ee.openeid.siga.common.exception.SignatureExistsException;
import ee.openeid.siga.common.model.HashcodeDataFile;
import ee.openeid.siga.common.model.HashcodeSignatureWrapper;
import ee.openeid.siga.common.model.ServiceType;
import ee.openeid.siga.common.model.SignatureHashcodeDataFile;
import ee.openeid.siga.service.signature.test.HashcodeContainerFilesHolder;
import ee.openeid.siga.service.signature.test.RequestUtil;
import ee.openeid.siga.service.signature.test.TestUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static ee.openeid.siga.service.signature.test.RequestUtil.SIGNED_HASHCODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashcodeContainerTest {

    @Test
    public void validHashcodeContainerCreation() throws IOException {
        List<HashcodeDataFile> hashcodeDataFiles = RequestUtil.createHashcodeDataFiles();
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        hashcodeDataFiles.forEach(hashcodeContainer::addDataFile);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            hashcodeContainer.save(outputStream);
            HashcodeContainerFilesHolder hashcodeContainerFilesHolder = TestUtil.getContainerFiles(outputStream.toByteArray());
            assertEquals(TestUtil.MIMETYPE, hashcodeContainerFilesHolder.getMimeTypeContent());
            assertEquals(TestUtil.MANIFEST_CONTENT, hashcodeContainerFilesHolder.getManifestContent());
            assertEquals(TestUtil.HASHCODES_SHA256_CONTENT, hashcodeContainerFilesHolder.getHashcodesSha256Content());
            assertEquals(TestUtil.HASHCODES_SHA512_CONTENT, hashcodeContainerFilesHolder.getHashcodesSha512Content());
        }
    }

    @Test
    public void validHashcodeContainerOpening() throws IOException, URISyntaxException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        byte[] container = TestUtil.getFile(SIGNED_HASHCODE);
        hashcodeContainer.open(container);
        assertEquals(1, hashcodeContainer.getSignatures().size());
        assertFalse(StringUtils.isBlank(hashcodeContainer.getSignatures().get(0).getGeneratedSignatureId()));
        assertEquals(2, hashcodeContainer.getDataFiles().size());

        List<SignatureHashcodeDataFile> signatureDataFiles = hashcodeContainer.getSignatures().get(0).getDataFiles();
        assertEquals(2, signatureDataFiles.size());
        assertEquals("test.txt", signatureDataFiles.get(0).getFileName());
        assertEquals("SHA256", signatureDataFiles.get(0).getHashAlgo());
        assertEquals("test1.txt", signatureDataFiles.get(1).getFileName());
        assertEquals("SHA256", signatureDataFiles.get(1).getHashAlgo());
    }


    @Test
    public void couldNotAddDataFileWhenSignatureExists() throws URISyntaxException, IOException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        byte[] container = TestUtil.getFile(SIGNED_HASHCODE);
        hashcodeContainer.open(container);
        HashcodeDataFile hashcodeDataFile = new HashcodeDataFile();
        hashcodeDataFile.setFileName("randomFile.txt");
        hashcodeDataFile.setFileHashSha256("asdasd=");

        SignatureExistsException caughtException = assertThrows(
            SignatureExistsException.class, () -> hashcodeContainer.addDataFile(hashcodeDataFile)
        );
        assertEquals("Unable to add data file when signature exists", caughtException.getMessage());
    }

    @Test
    public void hashcodeContainerMustHaveAtLeastOneDataFile() throws IOException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        byte[] outputBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            hashcodeContainer.save(outputStream);
            outputBytes = outputStream.toByteArray();
        }
        HashcodeContainer newHashcodeContainer = new HashcodeContainer();

        InvalidContainerException caughtException = assertThrows(
            InvalidContainerException.class, () -> newHashcodeContainer.open(outputBytes)
        );
        assertEquals("Container must have data file hashes", caughtException.getMessage());
    }

    @Test
    public void hashcodeContainerMissingSha512() {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();

        InvalidContainerException caughtException = assertThrows(
            InvalidContainerException.class, () -> hashcodeContainer.open(TestUtil.getFile("hashcodeMissingSha512File.asice"))
        );
        assertEquals("Hashcode container is missing SHA512 hash", caughtException.getMessage());
    }

    @Test
    public void directoryNotAllowedForFileName() {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();

        InvalidContainerException caughtException = assertThrows(
            InvalidContainerException.class, () -> hashcodeContainer.open(TestUtil.getFile("hashcodeShaFileIsDirectory.asice"))
        );
        assertEquals("Hashcode container contains invalid file name", caughtException.getMessage());
    }

    @Test
    public void containerWithTooLargeFiles() {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();

        InvalidContainerException caughtException = assertThrows(
                InvalidContainerException.class, () -> hashcodeContainer.open(TestUtil.getFile("hashcodeWithBigHashcodesFile.asice"))
        );
        assertEquals("Container contains file which is too large", caughtException.getMessage());
    }

    @Test
    public void validHashcodeContainerAddedNewData() throws IOException, URISyntaxException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        hashcodeContainer.open(TestUtil.getFile(SIGNED_HASHCODE));
        HashcodeSignatureWrapper signature = hashcodeContainer.getSignatures().get(0);

        hashcodeContainer.getSignatures().remove(0);
        HashcodeDataFile hashcodeDataFile = new HashcodeDataFile();
        hashcodeDataFile.setFileName("randomFile.txt");
        hashcodeDataFile.setFileHashSha256("n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=");
        hashcodeDataFile.setFileHashSha512("7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==");
        hashcodeDataFile.setFileSize(10);
        hashcodeContainer.addDataFile(hashcodeDataFile);

        hashcodeContainer.addSignature(signature);
        assertEquals(1, hashcodeContainer.getSignatures().size());
        assertEquals(3, hashcodeContainer.getDataFiles().size());
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            hashcodeContainer.save(outputStream);
            HashcodeContainer newContainer = new HashcodeContainer();
            newContainer.open(outputStream.toByteArray());
            assertEquals(1, newContainer.getSignatures().size());
            assertEquals(3, newContainer.getDataFiles().size());
        }
    }

    @Test
    public void validHashcodeContainerOpening_StoredAlgoWithDataDescriptor() throws IOException, URISyntaxException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        hashcodeContainer.open(TestUtil.getFile("hashcodeStoredAlgoWithDataDescriptor.asice"));
        assertEquals(1, hashcodeContainer.getSignatures().size());
        assertFalse(StringUtils.isBlank(hashcodeContainer.getSignatures().get(0).getGeneratedSignatureId()));
        assertEquals(1, hashcodeContainer.getDataFiles().size());

        List<SignatureHashcodeDataFile> signatureDataFiles = hashcodeContainer.getSignatures().get(0).getDataFiles();
        assertEquals(1, signatureDataFiles.size());
        assertEquals("client_test.go", signatureDataFiles.get(0).getFileName());
        assertEquals("SHA512", signatureDataFiles.get(0).getHashAlgo());
    }

    @Test
    public void validHashcodeContainerCreation_withOneDataFile() throws IOException {
        List<HashcodeDataFile> hashcodeDataFiles = RequestUtil.createHashcodeDataFiles();
        hashcodeDataFiles.get(0).setFileHashSha512(null);
        HashcodeContainer hashcodeContainer = new HashcodeContainer(ServiceType.PROXY);
        hashcodeDataFiles.forEach(hashcodeContainer::addDataFile);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            hashcodeContainer.save(outputStream);
            HashcodeContainerFilesHolder hashcodeContainerFilesHolder = TestUtil.getContainerFiles(outputStream.toByteArray());
            assertEquals(TestUtil.MIMETYPE, hashcodeContainerFilesHolder.getMimeTypeContent());
            assertEquals(TestUtil.MANIFEST_CONTENT, hashcodeContainerFilesHolder.getManifestContent());
            assertEquals(TestUtil.HASHCODES_SHA256_CONTENT, hashcodeContainerFilesHolder.getHashcodesSha256Content());
            assertNull(hashcodeContainerFilesHolder.getHashcodesSha512Content());
        }
    }

    @Test
    public void validateHashcodeContainerOpening_withInvalidBase64DataFileHash() throws URISyntaxException, IOException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        byte[] container = TestUtil.getFile("hashcode_with_invalid_base64_hash.asice");

        InvalidContainerException caughtException = assertThrows(
            InvalidContainerException.class, () -> hashcodeContainer.open(container)
        );
        assertEquals("Invalid data file hash", caughtException.getMessage());
    }

    @Test
    public void validateHashcodeContainerOpening_withInvalidLengthDataFileHash() throws URISyntaxException, IOException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        byte[] container = TestUtil.getFile("hashcode_with_invalid_length_hash.asice");

        InvalidContainerException caughtException = assertThrows(
            InvalidContainerException.class, () -> hashcodeContainer.open(container)
        );
        assertEquals("Invalid data file hash", caughtException.getMessage());
    }

    @Test
    public void invalidStructureHashcodeContainer() throws URISyntaxException, IOException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        byte[] container = TestUtil.getFile("hashcode_invalid_structure.asice");

        InvalidContainerException caughtException = assertThrows(
            InvalidContainerException.class, () -> hashcodeContainer.open(container)
        );
        assertEquals("Invalid hashcode container. Invalid file or directory in root level. Only mimetype file and META-INF directory allowed", caughtException.getMessage());
    }

    @Test
    public void openRegularAsicContainerWithDataFiles() throws URISyntaxException, IOException {
        HashcodeContainer hashcodeContainer = new HashcodeContainer();
        byte[] container = TestUtil.getFile("test.asice");

        InvalidContainerException caughtException = assertThrows(
            InvalidContainerException.class, () -> hashcodeContainer.open(container)
        );
        assertEquals("Invalid hashcode container. Invalid file or directory in root level. Only mimetype file and META-INF directory allowed", caughtException.getMessage());
    }

}
