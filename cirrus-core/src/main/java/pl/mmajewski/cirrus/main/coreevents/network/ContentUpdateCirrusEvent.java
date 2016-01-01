package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;

import java.util.Set;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class ContentUpdateCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {

    private Set<ContentMetadata> contentMetadataSet;
    private Set<ContentPiece> contentPiecesSet;

    public void setContentMetadataSet(Set<ContentMetadata> contentMetadataSet) {
        this.contentMetadataSet = contentMetadataSet;
    }

    public void setContentPiecesSet(Set<ContentPiece> contentPieces) {
        this.contentPiecesSet = contentPieces;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        for(ContentPiece contentPiece : contentPiecesSet){
            contentPiece.simulateFieldTransiency();
        }
        ContentStorage contentStorage = handler.getContentStorage();
        contentStorage.updateContentMetadata(contentMetadataSet);
        contentStorage.updateContentPieces(contentPiecesSet);
    }
}
