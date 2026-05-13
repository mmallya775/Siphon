package cloud.mallya.scan;

import cloud.mallya.model.FileMeta;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
    private final ClientSession clientSession;

    private static final Logger log = Logger.getLogger(RemoteScanner.class.getName());

    public RemoteScanner(Path remoteFolderPath, ClientSession clientSession) {
        this.remoteFolderPath = remoteFolderPath;
        this.clientSession = clientSession;
    }

    public Map<String, FileMeta> scan() {
        Map<String, FileMeta> scannedList = new HashMap<>();

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

//        String command = "find '" + remoteFolderPath.toString() + "' -type f -printf '%P : %s bytes : %T@ \\n'";
        String command = "find '" + remoteFolderPath + "' -type f -printf '%P\\t%s\\t%T@\\n'";
        Integer exitStatus;

        try (ClientChannel clientChannel = clientSession.createExecChannel(command)) {
            clientChannel.setOut(stdout);
            clientChannel.setErr(stderr);
            clientChannel.open().verify(10, TimeUnit.SECONDS);

            clientChannel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(60));

            exitStatus = clientChannel.getExitStatus();

        } catch (Exception e) {
            log.log(Level.WARNING, e, () -> "Error with creating exec channel!");
            throw new RuntimeException(e);
        }

        if (exitStatus != 0) {
            System.err.println("Exited with exit status: " + exitStatus);
            System.err.println(stderr.toString(StandardCharsets.UTF_8));
        }

        System.out.println("Exit status: " + exitStatus);

        String raw = stdout.toString(StandardCharsets.UTF_8);

        Stream.of(raw.split("\\n"))
                .filter(s -> !s.isEmpty())
                .forEach(s -> {
                    String[] splits = s.split("\\t", 3);

                    String relPath = splits[0];
                    long size = Long.parseLong(splits[1]);

                    String[] timeParts = splits[2].split("\\.", 2);
                    long secs = Long.parseLong(timeParts[0]);
                    long nanos = timeParts.length > 1
                            ? Long.parseLong((timeParts[1] + "000000000").substring(0, 9))
                            : 0;
                    Instant mtime = Instant.ofEpochSecond(secs, nanos);

                    scannedList.put(relPath, new FileMeta(size, mtime));
                });


        return scannedList;
    }

}
