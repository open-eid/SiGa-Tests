package ee.openeid.siga.service.signature.hashcode;

import ee.openeid.siga.common.HashCodeDataFile;
import org.digidoc4j.DigestAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.OutputStream;
import java.util.List;

public class HashCodesDataFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashCodesDataFile.class);
    public static final String HASHCODES_SHA256 = "META-INF/hashcodes-sha256.xml";
    public static final String HASHCODES_SHA512 = "META-INF/hashcodes-sha512.xml";
    public static final String HASHCODES_PREFIX = "META-INF/hashcodes-";
    private Document dom;
    private Element rootElement;
    private DigestAlgorithm digestAlgorithm;

    public HashCodesDataFile(DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public void generateHashCodeFile(List<HashCodeDataFile> dataFiles) {
        LOGGER.debug("Writing hashCode files");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            dom = documentBuilder.newDocument();
            rootElement = dom.createElement("hashcodes");

            dataFiles.forEach(this::addFileEntry);
            dom.appendChild(rootElement);

        } catch (
                ParserConfigurationException e) {
            throw new RuntimeException("Error creating hashCodes file", e);
        }
    }

    private void addFileEntry(HashCodeDataFile dataFile) {
        Element child = dom.createElement("file-entry");
        child.setAttribute("full-path", dataFile.getFileName());
        child.setAttribute("hash", digestAlgorithm.name());
        child.setAttribute("size", dataFile.getFileSize().toString());
        rootElement.appendChild(child);
    }

    public void writeTo(OutputStream outputStream) {
        DOMImplementationLS implementation = (DOMImplementationLS) dom.getImplementation();
        LSOutput lsOutput = implementation.createLSOutput();
        lsOutput.setByteStream(outputStream);
        LSSerializer writer = implementation.createLSSerializer();
        writer.write(dom, lsOutput);
    }
}