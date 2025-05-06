package ee.openeid.siga.test.util

import ee.openeid.siga.test.ConfigHolder
import ee.openeid.siga.test.TestConfig
import io.restassured.response.Response
import org.apache.commons.lang3.StringUtils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.stream.Stream

class Utils {
    static TestConfig conf = ConfigHolder.getConf()


    static boolean isRunningInDocker() {
        if (StringUtils.containsIgnoreCase(System.getProperty('os.name'), 'linux')) {
            def cgroupFile = new File('/proc/1/cgroup')
            if (cgroupFile.exists() && cgroupFile.text.contains('docker')) {
                return true
            }
            def cgroupV2File = new File('/proc/1/mountinfo')
            if (cgroupV2File.exists() && cgroupV2File.text.contains('docker')) {
                return true
            }
        }
        return false
    }

    static boolean isLocal() {
        return !isRunningInDocker()
    }

    private static Path findFileRecursively(Path basePath, String filename) {
        if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {
            throw new FileNotFoundException("Base directory '$basePath' not found or is not a directory")
        }
        try (Stream<Path> paths = Files.walk(basePath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString() == filename)
                    .findFirst()
                    .orElse(null)
        } catch (IOException e) {
            throw new RuntimeException("Error while searching for file '$filename' in path '$basePath'", e)
        }
    }

    static byte[] readFileFromResources(String filename) {
        String basePath = conf.testFilesDirectory() ?: "src/test/resources/"
        Path foundFile = findFileRecursively(Paths.get(basePath), filename)
        if (!foundFile) {
            throw new FileNotFoundException("File $filename not found in path $basePath")
        }
        try {
            return Files.readAllBytes(foundFile)
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file '$filename' from path '$basePath'", e)
        }
    }

    // Helper for testing, if there is a need to examine container
    static void saveContainerFromResponse(Response response) {
        String outputPath = "testsSavedFiles"
        String base64Content = response.path("container").toString()
        // If hashcode container, use default container name (hashcode response don't have container name)
        String containerName = response.path("containerName") ? response.path("containerName").toString() : "hashcodeContainer.asice"

        try {
            // Ensure the output folder exists
            File folder = new File(outputPath)
            folder.mkdirs()

            // Generate a timestamped file name
            Date time = new Date()
            String timestamp = new SimpleDateFormat('ddMMYYYY_HHmmssSSS').format(time)
            String newContainerName = containerName.replaceFirst(/\.(?=[^\.]+$)/, "_${timestamp}.")
            // Find the last dot and replace it with timestamp

            // Write the decoded content to the file
            File file = new File(folder, newContainerName)
            file.bytes = base64Content.decodeBase64() // Write decoded bytes in Groovy style
            println "File saved successfully: ${file.absolutePath}"

        } catch (e) {
            println "Error saving file: ${e.message}"
            e.printStackTrace()
        }
    }
}
