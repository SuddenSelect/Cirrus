package pl.mmajewski.cirrus.appevents;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.event.SimpleCirrusAppEventHandler;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class NewContentPreparedCirrusAppEvent extends CirrusAppEvent<SimpleCirrusAppEventHandler> {
    private ContentMetadata metadata;
    private List<ContentPiece> pieces;

    public void setMetadata(ContentMetadata metadata) {
        this.metadata = metadata;
    }

    public ContentMetadata getMetadata() {
        return metadata;
    }

    public List<ContentPiece> getPieces() {
        return pieces;
    }

    public void setPieces(List<ContentPiece> pieces) {
        this.pieces = pieces;
    }


    @Override
    public void appEvent(SimpleCirrusAppEventHandler handler) {
        ContentStorage prepared = handler.getContentStorage();

        Set<ContentMetadata> metadatas = new TreeSet<>();
        metadatas.add(metadata);

        for(ContentPiece piece : pieces) {
            prepared.storeContentPiece(piece);
        }
        prepared.updateContentMetadata(metadatas);
    }
}
