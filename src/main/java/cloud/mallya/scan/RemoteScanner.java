package cloud.mallya.scan;

import cloud.mallya.model.FileMeta;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RemoteScanner {

    private final Path remoteFolderPath;
    private static final Logger log = Logger.getLogger(RemoteScanner.class.getName());

    public RemoteScanner(Path remoteFolderPath) {
        this.remoteFolderPath = remoteFolderPath;
    }

    public Map<String, FileMeta> scan() {
        Map<String, FileMeta> scannedList = new HashMap<>();

        return scannedList;
    }

}
