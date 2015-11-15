package pl.mmajewski.cirrus.main.coreevents.storage;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;

import java.io.IOException;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class BalanceAndDiffuseStorageCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {

    private ContentStorage toCommit;

    public void setStorageToCommit(ContentStorage toCommit) {
        this.toCommit = toCommit;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        ContentStorage storage = handler.getContentStorage();
        storage.updateContentMetadata(toCommit.getAllContentMetadata());
            for (ContentMetadata metadata : toCommit.getAllContentMetadata()) {
                for (ContentPiece piece : toCommit.getAvailablePieces(metadata)) {
                    try {
                        storage.storeContentPiece(piece);
                    } catch (IOException e) {
                        e.printStackTrace();//todo handle
                    }
                }
            }

        //TODO generate events for content updates
        //TODO initiate content diffusion
    }
}
