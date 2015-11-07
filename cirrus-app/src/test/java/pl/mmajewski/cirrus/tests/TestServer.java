package pl.mmajewski.cirrus.tests;

import pl.mmajewski.cirrus.common.model.Host;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Maciej Majewski on 07/11/15.
 */
class TestServer implements Runnable {
    static Integer serverPort = 64444;
    public static Integer getServerPort(){
        int port;
        synchronized (TestServer.serverPort) {
            port = TestServer.serverPort++;
        }
        return port;
    }

    static Host getLocalHost(int serverPort) throws UnknownHostException {
        Host localhost = new Host();
        localhost.setPhysicalAddress(InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        localhost.setPort(serverPort);
        return localhost;
    }

    private Object received = null;
    private int port;

    public Object getReceived() {
        return received;
    }

    public TestServer setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            Socket client = server.accept();
            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
            received = inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (server != null && !server.isClosed()) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
