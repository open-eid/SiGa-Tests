package ee.openeid.siga.test.util

class Utils {
    static boolean isRunningInDocker() {
        def cgroupFile = new File('/proc/1/cgroup')
        // Check if the OS is Linux and /proc/1/cgroup exists
        if (System.getProperty('os.name').toLowerCase().contains('linux') && cgroupFile.exists()) {
            // Check if /proc/1/cgroup contents contain the string "docker"
            return cgroupFile.text.contains('docker')
        }
        return false
    }

    static boolean isLocal() {
        return !isRunningInDocker()
    }
}
