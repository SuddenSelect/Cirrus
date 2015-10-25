package pl.mmajewski.cirrus.network.client;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.exception.SendContentMetadataFailCirrusException;
import pl.mmajewski.cirrus.network.exception.SendContentPieceFailCirrusException;

/**
 * Represents connection for data transfer.
 * Created by Maciej Majewski on 30/10/14.
 */
public interface ClientDataConnection extends Connection {
    public void sendContentPiece(ContentPiece contentPiece) throws SendContentPieceFailCirrusException;
    public void sendContentMetadata(ContentMetadata contentMetadata) throws SendContentMetadataFailCirrusException;
}
