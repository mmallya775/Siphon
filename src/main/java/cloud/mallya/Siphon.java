package cloud.mallya;

import cloud.mallya.model.FileMeta;
import cloud.mallya.scan.LocalScanner;

import java.util.Map;

public class Siphon {
    static void main() {
        LocalScanner localDirectoryUtilities = new LocalScanner();

        Map<String, FileMeta> listOfFiles = localDirectoryUtilities.listLocal();

        System.out.println(listOfFiles);
    }
}
