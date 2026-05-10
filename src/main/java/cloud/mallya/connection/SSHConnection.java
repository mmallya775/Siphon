package cloud.mallya.connection;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.keyverifier.KnownHostsServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.config.keys.loader.KeyPairResourceLoader;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.common.util.security.SecurityUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SSHConnection implements AutoCloseable {

    private final SshClient sshClient;
    private final ClientSession clientSession;
    private static final Logger log = Logger.getLogger(SSHConnection.class.getName());

    private SSHConnection(Builder b) {
        this.sshClient = SshClient.setUpDefaultClient();

        // To provide passphrase for the ssh key
        FilePasswordProvider filePasswordProvider = (_, _, retryIndex) -> {
            if (retryIndex > 0) {
                log.log(Level.WARNING, () -> "Incorrect Passphrase. Aborting !!!");
            }

            return b.passPhrase;
        };

        // Prove client's identity for the server using a collection of KeyPair(s)
        Collection<KeyPair> keys;
        try {
            KeyPairResourceLoader keyPairResourceLoader = SecurityUtils.getKeyPairResourceParser();
            keys = keyPairResourceLoader.loadKeyPairs(null, b.keyPath, filePasswordProvider);
        } catch (GeneralSecurityException | IOException e) {
            log.log(Level.SEVERE, e, () -> "Error Loading the key pair resource!");
            throw new RuntimeException(e);
        }

        // Provide the key to the client session
        sshClient.setKeyIdentityProvider(KeyIdentityProvider.wrapKeyPairs(keys));
        sshClient.setServerKeyVerifier(new KnownHostsServerKeyVerifier(
                AcceptAllServerKeyVerifier.INSTANCE,
                Paths.get(System.getProperty("user.home"), ".ssh", "known_hosts")));

//        CoreModuleProperties.HEARTBEAT_INTERVAL.set(sshClient, Duration.ofSeconds(20));
//        CoreModuleProperties.HEARTBEAT_NO_REPLY_MAX.set(sshClient, 5);
//
//        PropertyResolverUtils.updateProperty(sshClient, CoreModuleProperties.HEARTBEAT_INTERVAL.getName(),10000);
//        PropertyResolverUtils.updateProperty(sshClient, CoreModuleProperties.HEARTBEAT_NO_REPLY_MAX.getName(),5);

        sshClient.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE,
                Duration.ofSeconds(20));

        sshClient.start();

        System.out.println("SSH Heartbeat: " + sshClient.getSessionHeartbeatInterval());

        try {
            this.clientSession = sshClient.connect(b.userName, b.host, b.port)
                    .verify(10, TimeUnit.SECONDS)
                    .getSession();

            this.clientSession.auth();

            System.out.println("Connected as: " + clientSession.getUsername());
            System.out.println(clientSession.getClientVersion());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public ClientSession getClientSession() {
        return clientSession;
    }

    @Override
    public void close() {
        try {
            clientSession.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sshClient.stop();
    }

    public static Builder builder(String host, String userName) {
        return new Builder(host, userName);
    }

    public static final class Builder {

        private final String host;
        private final String userName;

        private int port = 22;
        private Path keyPath;
        private String passPhrase;

        private Builder(String host, String userName) {
            this.host = host;
            this.userName = userName;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder keyPath(Path keypath) {
            this.keyPath = keypath;
            return this;
        }

        public Builder passPhrase(String passPhrase) {
            this.passPhrase = passPhrase;
            return this;
        }


        public SSHConnection build() {
            return new SSHConnection(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "host='" + host + '\'' +
                    ", userName='" + userName + '\'' +
                    ", port=" + port +
                    ", keyPath=" + keyPath +
                    ", passPhrase='" + passPhrase + '\'' +
                    '}';
        }
    }
}
