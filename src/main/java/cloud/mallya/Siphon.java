package cloud.mallya;

import cloud.mallya.connection.SSHConnection;
import cloud.mallya.model.FileMeta;
import cloud.mallya.scan.LocalScanner;
import org.apache.sshd.client.session.ClientSession;

import java.nio.file.Path;
import java.util.Map;

public class Siphon {

    static void main() {

        Path localFolder = Path.of(System.getenv("LOCAL_PATH"));
        Path remoteFolder = Path.of(System.getenv("REMOTE_PATH"));


        long start = System.currentTimeMillis();
        LocalScanner localDirectoryUtilities = new LocalScanner(localFolder);
        Map<String, FileMeta> listOfFiles = localDirectoryUtilities.scan();
        System.out.println("It took: " + (System.currentTimeMillis() - start) + " ms.");

        try (SSHConnection sshConnection = SSHConnection.builder(System.getenv("HOST"), System.getenv("USER"))
                .keyPath(Path.of(System.getProperty("user.home"), ".ssh", "id_ed25519"))
                .passPhrase(System.getenv("PASS_PHRASE"))
                .build()) {
            ClientSession clientSession = sshConnection.getClientSession();


            for (int i = 0; i < 2; i++) {
                System.out.println("Iteration: " + i + " user: " + clientSession.getUsername());
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        listOfFiles.forEach((p, m) -> System.out.println(p + "::" + m));

//        listOfFiles.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .forEach(e -> System.out.printf("%-50s %100d bytes  %s%n",
//                        e.getKey(),
//                        e.getValue().size(),
//                        Instant.ofEpochMilli(e.getValue().timestamp())));
    }
}
