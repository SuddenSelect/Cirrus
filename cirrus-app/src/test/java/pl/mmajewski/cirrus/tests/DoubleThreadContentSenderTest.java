package pl.mmajewski.cirrus.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.ContentStatus;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.impl.network.ClientDirectConnectionPool;
import pl.mmajewski.cirrus.impl.network.DoubleThreadContentSender;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Maciej Majewski on 07/11/15.
 */
public class DoubleThreadContentSenderTest {

    @Test
    public void testMetadataSending() throws UnknownHostException, NetworkCirrusException, InterruptedException {
        int serverPort = TestServer.getServerPort();
        TestServer testServer = new TestServer(1).setPort(serverPort);
        Thread serverThread = new Thread(testServer);
        serverThread.start();
        testServer.waitForServerStart();

        Host localhost = TestServer.getLocalHost(serverPort);

        ContentMetadata metadata = new ContentMetadata();
        metadata.setContentId("test-content");
        metadata.setPiecesAmount(3);
        metadata.setStatus(ContentStatus.CALCULATING);
        metadata.setAvailableSince(LocalDateTime.now());
        metadata.setCommiterCirrusId("test-id");
        metadata.setContentChecksum("test-checksum");
        metadata.setLastUpdated(LocalDateTime.now());
        Map<Integer, String> checksums = new HashMap<>();
        checksums.put(0,"checksum-0");
        checksums.put(1,"checksum-1");
        checksums.put(2,"checksum-2");
        metadata.setPiecesChecksums(checksums);

        ConnectionPool connectionPool = new ClientDirectConnectionPool();
        connectionPool.addHost(localhost);
        DoubleThreadContentSender sender = new DoubleThreadContentSender(connectionPool);

        sender.sendContentMetadata(localhost, metadata);
        sender.waitForMetadataSending();
        serverThread.join();
        sender.kill();

        Object received = testServer.getReceived().get(0);

        Assert.assertEquals(received.getClass(), ContentMetadata.class);

        ContentMetadata receivedMetadata = (ContentMetadata) received;

        Assert.assertEquals(receivedMetadata.getContentId(),metadata.getContentId());
        Assert.assertEquals(receivedMetadata.getPiecesAmount(),metadata.getPiecesAmount());
        Assert.assertEquals(receivedMetadata.getStatus(),metadata.getStatus());
        Assert.assertEquals(receivedMetadata.getAvailableSince(),metadata.getAvailableSince());
        Assert.assertEquals(receivedMetadata.getCommiterCirrusId(),metadata.getCommiterCirrusId());
        Assert.assertEquals(receivedMetadata.getContentChecksum(),metadata.getContentChecksum());
        Assert.assertEquals(receivedMetadata.getLastUpdated(),metadata.getLastUpdated());
        Assert.assertEquals(receivedMetadata.getPiecesChecksums(),metadata.getPiecesChecksums());

    }

    @Test
    public void testPiecesSending() throws UnknownHostException, NetworkCirrusException, InterruptedException {
        int serverPort = TestServer.getServerPort();
        TestServer testServer = new TestServer(2).setPort(serverPort);
        Thread serverThread = new Thread(testServer);
        serverThread.start();
        testServer.waitForServerStart();

        Host localhost = TestServer.getLocalHost(serverPort);

        ContentPiece contentPiece0 = new ContentPiece();
        contentPiece0.setContentId("test-id");
        contentPiece0.setContent(ByteBuffer.wrap(new byte[]{1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89}));
        contentPiece0.setExpectedChecksum("test-checksum-0");
        contentPiece0.setStatus(ContentStatus.CALCULATING);
        contentPiece0.setSequence(0);

        ContentPiece contentPiece1 = new ContentPiece();
        contentPiece1.setContentId("test-id");
        contentPiece1.setContent(ByteBuffer.wrap(new byte[]{2, 2, 4, 6, 10, 16, 26, 42, 68, 110}));
        contentPiece1.setExpectedChecksum("test-checksum-1");
        contentPiece1.setStatus(ContentStatus.CORRUPTED);
        contentPiece1.setSequence(1);

        List<ContentPiece> contentPieces = new LinkedList<>();
        contentPieces.add(contentPiece0);
        contentPieces.add(contentPiece1);

        ConnectionPool connectionPool = new ClientDirectConnectionPool();
        connectionPool.addHost(localhost);
        DoubleThreadContentSender sender = new DoubleThreadContentSender(connectionPool);

        sender.sendContentPieces(localhost, contentPieces);
        sender.waitForPiecesSending();
        sender.kill();
        serverThread.join();

        {
            Object received0 = testServer.getReceived().get(0);

            Assert.assertEquals(received0.getClass(), ContentPiece.class);

            ContentPiece receivedPiece0 = (ContentPiece) received0;
            receivedPiece0.simulateFieldTransiency();

            Assert.assertEquals(receivedPiece0.getContentId(), contentPiece0.getContentId());
            Assert.assertEquals(receivedPiece0.getContent(), contentPiece0.getContent());
            Assert.assertEquals(receivedPiece0.getExpectedChecksum(), contentPiece0.getExpectedChecksum());
            Assert.assertEquals(receivedPiece0.getStatus(), ContentStatus.UNCHECKED);
            Assert.assertNotEquals(receivedPiece0.getStatus(), contentPiece0.getStatus());
            Assert.assertEquals(receivedPiece0.getSequence(), contentPiece0.getSequence());
        }
        {
            Object received1 = testServer.getReceived().get(1);

            Assert.assertEquals(received1.getClass(), ContentPiece.class);

            ContentPiece receivedPiece1 = (ContentPiece) received1;
            receivedPiece1.simulateFieldTransiency();

            Assert.assertEquals(receivedPiece1.getContentId(), contentPiece1.getContentId());
            Assert.assertEquals(receivedPiece1.getContent(), contentPiece1.getContent());
            Assert.assertEquals(receivedPiece1.getExpectedChecksum(), contentPiece1.getExpectedChecksum());
            Assert.assertEquals(receivedPiece1.getStatus(), ContentStatus.UNCHECKED);
            Assert.assertNotEquals(receivedPiece1.getStatus(), contentPiece0.getStatus());
            Assert.assertEquals(receivedPiece1.getSequence(), contentPiece1.getSequence());
        }
    }
}
