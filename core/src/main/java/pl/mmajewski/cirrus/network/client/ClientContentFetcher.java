package pl.mmajewski.cirrus.network.client;

import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;

/**
 * Created by Maciej Majewski on 30/10/14.
 */
public interface ClientContentFetcher {
    public ContentPiece requestContentPiece(Host source, String contentId, int pieceNumber) throws NetworkCirrusException;
}
