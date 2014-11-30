package pl.mmajewski.cirrus.network.client;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.network.Connection;

/**
 * Represents connection for data transfer.
 * Created by Maciej Majewski on 30/10/14.
 */
public interface ClientDataConnection extends Connection {
    public void sendContentPiece(Connection connection, ContentPiece contentPiece);
    public void sendContentMetadata(Connection connection, ContentMetadata contentMetadata);
}
