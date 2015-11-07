package pl.mmajewski.cirrus.network.server;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;

import java.util.Collection;

/**
 * Class handling process of sending content to remote hosts. Called by EventHandler when necessary.
 * Active part of the server.
 * Expected to maintain a separate thread sending buffered data where instructed.
 * Created by Maciej Majewski on 30/10/14.
 */
public interface ServerContentSender {

    /**
     * Sends ContentMetadata to the remote host.
     * @param target remote host
     * @param contentMetadata
     * @throws NetworkCirrusException
     */
    public void sendContentMetadata(Host target, ContentMetadata contentMetadata) throws NetworkCirrusException;

    /**
     * Sequentially sends requested pieces to the remote host.
     * @param target remote host
     * @param contentPieces collection of pieces to send
     * @throws NetworkCirrusException thrown when something went wrong
     */
    public void sendContentPieces(Host target, Collection<? extends ContentPiece> contentPieces) throws NetworkCirrusException;

    /**
     * Ends sending threads
     */
    public void kill();
}
