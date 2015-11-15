package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;

import java.util.List;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class ContentFetchCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {

    private List<ContentPiece> contentPiecesList = null;

    public void setContentPiecesList(List<ContentPiece> contentPiecesList) {
        this.contentPiecesList = contentPiecesList;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        ContentStorage contentStorage = handler.getContentStorage();
        for(ContentPiece contentPiece : contentPiecesList) {
            contentStorage.storeContentPieceTemporarily(contentPiece);
        }
    }
}
