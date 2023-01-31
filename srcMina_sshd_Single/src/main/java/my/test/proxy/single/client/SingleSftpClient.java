package my.test.proxy.single.client;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpErrorDataHandler;
import org.apache.sshd.sftp.client.SftpVersionSelector;
import org.apache.sshd.sftp.client.impl.DefaultSftpClientFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class SingleSftpClient extends Thread {

    public static void main(String[] args) throws IOException, InterruptedException {
        new SingleSftpClient().createSftpClient();
    }

    public void createSftpClient() throws IOException {
        // session create
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        ClientSession clientSession = null;
        try {
            clientSession = client.connect("root", "192.168.5.102", 22).verify().getSession();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clientSession.addPasswordIdentity("1234");
        AuthFuture tt = null;
        try {
            tt = clientSession.auth().verify(60, TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // sftp client - default
        DefaultSftpClientFactory defaultSftpClientFactory = new DefaultSftpClientFactory();
        var defaultSftpClientExtend = defaultSftpClientFactory.createDefaultSftpClient(clientSession,
                SftpVersionSelector.preferredVersionSelector(3),
                SftpErrorDataHandler.EMPTY);

        var sftpFileSystem = defaultSftpClientFactory.createSftpFileSystem(clientSession, 3);
        var sftpClient = sftpFileSystem.getClient();
        var inputStream = sftpClient.read("/opt/plainText.txt");

        System.out.println(new String(inputStream.readNBytes(10)));
        System.out.println("■■■■■■■■■■■Client■■ Command! ■■■■■■■■■■■■■■■");


        start();
    }

    @Override
    public void run() {
        for (;;) {

        }
    }


}
