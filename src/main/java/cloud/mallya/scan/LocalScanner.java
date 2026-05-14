package cloud.mallya.scan;

import cloud.mallya.model.FileMeta;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class LocalScanner implements Scanner {

    private final Path localFolderPath;
    private static final Logger log = Logger.getLogger(LocalScanner.class.getName());

    public LocalScanner(Path localFolderPath) {
        this.localFolderPath = localFolderPath;
    }

    @Override
    public Map<String, FileMeta> scan() {

        Map<String, FileMeta> scannedList = new HashMap<>();

        try (Stream<Path> localFileStream = Files.walk(localFolderPath)) {
            localFileStream
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            String key = localFolderPath.relativize(path).toString();
                            BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
                            scannedList.put(key, new FileMeta(basicFileAttributes.size(), basicFileAttributes.lastModifiedTime().toInstant()));
                        } catch (IOException e) {
                            log.log(Level.WARNING, e, () -> "Failed to fetch file attributes for: " + path);
                        }
                    });

        } catch (IOException e) {
            log.log(Level.SEVERE, e, () -> "Failed to scan folder: " + localFolderPath);
            throw new UncheckedIOException("Failed to scan " + localFolderPath, e);
        }
        return scannedList;
    }
}
