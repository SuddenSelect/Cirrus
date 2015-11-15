package pl.mmajewski.cirrus.tests;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.ContentStatus;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;
import pl.mmajewski.cirrus.impl.network.ClientDirectConnection;
import pl.mmajewski.cirrus.impl.network.ClientDirectConnectionPool;
import pl.mmajewski.cirrus.main.appevents.AdaptFileCirrusAppEvent;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;

import java.net.*;

/**
 * Created by Maciej Majewski on 31/10/15.
 */
public class ClientDirectConnectionTest {

    @Test
    public void clientDirectConnectionEventTest() throws UnknownHostException, NetworkCirrusException, ContentAdapterCirrusException, InterruptedException {
        TestServer testServer = new TestServer(1);
        int serverPort = TestServer.getServerPort();
        testServer.setPort(serverPort);
        Thread server = new Thread(testServer);
        server.start();
        testServer.waitForServerStart();

        Host localhost = TestServer.getLocalHost(serverPort);


        ClientDirectConnectionPool connectionPool = new ClientDirectConnectionPool();
        connectionPool.addHost(localhost);
        ClientDirectConnection connection = connectionPool.fetchConnection(localhost);
        connection.connect();

        AdaptFileCirrusAppEvent event = new AdaptFileCirrusAppEvent();
        event.init();

        connection.sendEvent(event);

        server.join();
        Object object = testServer.getReceived().get(0);
        connection.kill();


        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof CirrusAppEvent);
        Assert.assertTrue(object instanceof AdaptFileCirrusAppEvent);
        AdaptFileCirrusAppEvent received = (AdaptFileCirrusAppEvent) object;
        Assert.assertEquals(received.getCreationTime(), event.getCreationTime());
        Assert.assertEquals(received.getEventId(), event.getEventId());
        Assert.assertEquals(received.getCreatedThread(), event.getCreatedThread());
        Assert.assertEquals(received.getTrace(), event.getTrace());
    }

}
