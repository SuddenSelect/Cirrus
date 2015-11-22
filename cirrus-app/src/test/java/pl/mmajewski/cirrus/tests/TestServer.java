package pl.mmajewski.cirrus.tests;

import pl.mmajewski.cirrus.common.model.Host;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maciej Majewski on 07/11/15.
 */
class TestServer implements Runnable {
    static Integer serverPort = 64444;
    public static synchronized Integer getServerPort(){
        return TestServer.serverPort++;
    }

    static Host getLocalHost(int serverPort) {
        Host localhost = Host.newHost(InetAddress.getLoopbackAddress());
        localhost.setPort(serverPort);
        localhost.setCirrusId("test-cirrus-id");
        return localhost;
    }

    private CountDownLatch startSignal = new CountDownLatch(1);
    private List<Object> received = new LinkedList<>();
    private int port;
    private int receiveAmount;

    public TestServer(int receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public List<Object> getReceived() {
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
            startSignal.countDown();
            Socket client = server.accept();
            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
            for (int i = 0; i < receiveAmount; i++) {
                Object object=null;
                while(object==null){//Don't know why, but sometimes first object from inputStream is null
                    object = inputStream.readUnshared();
                }
                received.add(object);
            }
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

    public void waitForServerStart() throws InterruptedException {
        startSignal.await(3, TimeUnit.SECONDS);
    }
}
