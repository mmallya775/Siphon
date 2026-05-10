package cloud.mallya.scan;

import cloud.mallya.model.FileMeta;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Using the SftpFileSystem to list out the files and its arttributes is very slow
 * for large directories with multiple nested subdirectories. We could try to list out the
 * files recursively in the server itself and send its results over to the client via
 * the clientSesion.createExecChannel(command). The result is an extremely fast listing of
 * files in the remote directory. I am currently only aiming for the case where the remote
 * system is also UNIX-based. Not sure if other operating systems can utilize the `find` command.
 */

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
