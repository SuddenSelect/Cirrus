package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.util.CirrusBlockingSequence;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;

import java.util.ArrayList;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public class DeliverContentCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {

    private ContentMetadata contentMetadata = null;
    private ArrayList<ContentPiece> contentPieces = null;

    public void setContentMetadata(ContentMetadata contentMetadata) {
        this.contentMetadata = contentMetadata;
    }

    public void setContentPieces(ArrayList<ContentPiece> contentPieces) {
        this.contentPieces = contentPieces;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        CirrusBlockingSequence<ContentPiece> contentPieceSink = handler.getContentPieceSink(contentMetadata);
        for(ContentPiece contentPiece : contentPieces){
            contentPieceSink.push(contentPiece.getSequence(), contentPiece);
        }
    }
}
