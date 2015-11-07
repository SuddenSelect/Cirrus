package pl.mmajewski.cirrus.impl.network;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.ClientDataConnection;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.exception.SendContentMetadataFailCirrusException;
import pl.mmajewski.cirrus.network.exception.SendContentPieceFailCirrusException;
import pl.mmajewski.cirrus.network.server.ServerContentSender;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 03/11/15.
 */
public class DoubleThreadContentSender implements ServerContentSender {
    private final Logger logger = Logger.getLogger(DoubleThreadContentSender.class.getName());

    private class SendCommand {
        private Host targetHost;
        private Collection<? extends ContentPiece> contentPieces = null;
        private ContentMetadata contentMetadata = null;

        public SendCommand(Host targetHost, ContentMetadata contentMetadata) {
            this.targetHost = targetHost;
            this.contentMetadata = contentMetadata;
        }

        public SendCommand(Host targetHost, Collection<? extends ContentPiece> contentPieces) {
            this.targetHost = targetHost;
            this.contentPieces = contentPieces;
        }

        public Host getTargetHost() {
            return targetHost;
        }

        public Collection<? extends ContentPiece> getContentPieces() {
            return contentPieces;
        }

        public ContentMetadata getContentMetadata() {
            return contentMetadata;
        }
    }
    private class Sender implements Runnable{
        private BlockingQueue<SendCommand> sendCommands = new LinkedBlockingQueue<>();
        private ConnectionPool connectionPool;
        private CountDownLatch endSignal = new CountDownLatch(1);

        public Sender(ConnectionPool dataConnectionPool) {
            this.connectionPool = dataConnectionPool;
        }

        public void accept(SendCommand sendCommand) throws InterruptedException {
            sendCommands.put(sendCommand);
        }

        @Override
        public void run() {
            try{
                //Main sending loop
                while(true){
                   SendCommand sendCommand = sendCommands.take();
                   try {
                       ClientDataConnection connection = connectionPool.fetchConnection(sendCommand.getTargetHost());

                       if(sendCommand.getTargetHost() != null){
                           connection.sendContentMetadata(sendCommand.getContentMetadata());
                       }

                       if(sendCommand.getContentPieces() != null){
                           for(ContentPiece toSend : sendCommand.getContentPieces()){
                               connection.sendContentPiece(toSend);
                           }
                       }
                       
                       if(endSignal !=null){//for testing only
                           endSignal.countDown();
                       }
                   } catch (NetworkCirrusException e) {
                       int commandsAmount = sendCommands.size();
                       sendCommands.removeIf(command -> command.getTargetHost().equals(sendCommand.getTargetHost()));
                       logger.warning(MessageFormat.format(
                               "Sending content failed for {0}, cancelled {1} tasks. Reason: {2}",
                               sendCommand.getTargetHost().toString(),
                               sendCommands.size() - commandsAmount,
                               e.getMessage()
                       ));

                       while(endSignal.getCount()>0){
                           endSignal.countDown();
                       }
                   }
                }
            }catch (InterruptedException e){
                logger.info("Content sending thread finished");
            }

        }
    }

    private Sender metadataSender;
    private Thread metadataThread;

    private Sender piecesSender;
    private Thread piecesThread;

    public DoubleThreadContentSender(ConnectionPool connectionPool) {
        metadataSender = new Sender(connectionPool);
        piecesSender = new Sender(connectionPool);

        metadataThread = new Thread(metadataSender);
        piecesThread = new Thread(piecesSender);

        metadataThread.start();
        piecesThread.start();
    }

    @Override
    public void sendContentMetadata(Host target, ContentMetadata contentMetadata) throws NetworkCirrusException {
        try {
            metadataSender.accept(new SendCommand(target, contentMetadata));
        } catch (InterruptedException e) {
            throw new SendContentMetadataFailCirrusException(e);
        }
    }

    @Override
    public void sendContentPieces(Host target, Collection<? extends ContentPiece> contentPieces) throws NetworkCirrusException {
        try {
            piecesSender.accept(new SendCommand(target, contentPieces));
        } catch (InterruptedException e) {
            throw new SendContentPieceFailCirrusException(e);
        }
    }

    @Override
    public void kill() {
        metadataThread.interrupt();
        piecesThread.interrupt();
    }

    /**
     * Holds calling thread until Sender Thread have finished
     * For testing only
     */
    public void waitForMetadataSending() throws InterruptedException {
        metadataSender.endSignal.await(3, TimeUnit.SECONDS);
    }
    public void waitForPiecesSending() throws InterruptedException {
        piecesSender.endSignal.await(3, TimeUnit.SECONDS);
    }

}
