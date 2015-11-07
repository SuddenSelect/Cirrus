package pl.mmajewski.cirrus.tests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

    private Object received = null;
    private int port;

    public Object getReceived() {
        return received;
    }

    public void

    setPort(int port) {
        this.port = port;
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
