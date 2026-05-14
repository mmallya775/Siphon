package cloud.mallya;

import cloud.mallya.connection.SSHConnection;
import cloud.mallya.diff.FilesDiffing;
import cloud.mallya.model.FileMeta;
import cloud.mallya.scan.LocalScanner;
import cloud.mallya.scan.RemoteScanner;
import org.apache.sshd.client.session.ClientSession;

import java.nio.file.Path;
import java.util.Map;

public class Siphon {

    static void main() {

        Path localFolder = Path.of(System.getenv("LOCAL_PATH"));
        Path remoteFolder = Path.of(System.getenv("REMOTE_PATH"));

        LocalScanner localDirectoryUtilities = new LocalScanner(localFolder);
        Map<String, FileMeta> listOfFiles = localDirectoryUtilities.scan();

        try (SSHConnection sshConnection = SSHConnection.builder(System.getenv("HOST"), System.getenv("USER"))
                .keyPath(Path.of(System.getProperty("user.home"), ".ssh", "id_ed25519"))
                .passPhrase(System.getenv("PASS_PHRASE"))
                .build()) {
            ClientSession clientSession = sshConnection.getClientSession();

            RemoteScanner remoteScanner = new RemoteScanner(remoteFolder, clientSession);
            Map<String, FileMeta> remoteList = remoteScanner.scan();


            FilesDiffing diffedFiles = FilesDiffing.between(listOfFiles, remoteList);

            System.out.println(diffedFiles);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
