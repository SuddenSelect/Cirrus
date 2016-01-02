package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.util.CirrusBlockingSequence;
import pl.mmajewski.cirrus.common.util.CirrusChecksum;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;

import java.text.MessageFormat;
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
            CirrusChecksum pieceChecksum = new CirrusChecksum();
            pieceChecksum.update(contentPiece.getContent());
            contentPiece.getContent().rewind();

            if(pieceChecksum.validate(contentPiece.getExpectedChecksum())) {
                contentPieceSink.push(contentPiece.getSequence(), contentPiece);
            }else{
                ActionFailureCirrusEvent failureEvent = new ActionFailureCirrusEvent();
                failureEvent.setInvalidContentPiece(contentPiece);
                failureEvent.setMessage(MessageFormat.format(
                    "Invalid checksum for piece {0} in content {1} - expected {2}, got {3}",
                    new Object[]{
                        contentPiece.getSequence(),
                        contentPiece.getContentId(),
                        contentPiece.getExpectedChecksum(),
                        pieceChecksum.getStringChecksum()
                }));
                try {
                    handler.accept(failureEvent);
                } catch (EventHandlerClosingCirrusException e1) {}


            }
        }
    }
}
