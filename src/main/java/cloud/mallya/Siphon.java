package cloud.mallya;

import cloud.mallya.model.FileMeta;
import cloud.mallya.scan.LocalScanner;

import java.nio.file.Path;
import java.util.Map;

public class Siphon {
    static void main() {

        Path localFolder = Path.of("/home/mmallya/Documents/RustProjects/hello_world");
        LocalScanner localDirectoryUtilities = new LocalScanner(localFolder);

        Map<String, FileMeta> listOfFiles = localDirectoryUtilities.scan();
        System.out.println(listOfFiles);

//        listOfFiles.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .forEach(e -> System.out.printf("%-50s %100d bytes  %s%n",
//                        e.getKey(),
//                        e.getValue().size(),
//                        Instant.ofEpochMilli(e.getValue().timestamp())));
    }
}
