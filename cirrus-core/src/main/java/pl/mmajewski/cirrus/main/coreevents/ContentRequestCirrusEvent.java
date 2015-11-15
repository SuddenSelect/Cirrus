package pl.mmajewski.cirrus.main.coreevents;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.GenericCirrusEventThread;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.util.CirrusBlockingSequence;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;

import java.util.ArrayList;

/**
 * Created by Maciej Majewski on 2015-02-06.
 */
public class ContentRequestCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {
    private CirrusBlockingSequence<ContentPiece> sink;
    private ContentMetadata metadata;
    private ArrayList<ContentPiece> pieces;

    public ContentRequestCirrusEvent(ContentMetadata metadata, CirrusBlockingSequence<ContentPiece> sink){
        this.metadata = metadata;
        this.sink = sink;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        ContentStorage storage = handler.getContentStorage();
        pieces = storage.getAvailablePieces(metadata);

        //TODO extend upon the network
        //TODO optimize
        //Simplified local-only access
        new Thread(this.new ContentAppender()).start();
    }

    /**
     * Responsible for pushing ContentPieces into Queue
     */
    private class ContentAppender extends GenericCirrusEventThread {
        private int currentSeq = 0;
        private int piecesAmount = ContentRequestCirrusEvent.this.metadata.getPiecesAmount();

        @Override
        public Integer getProgress() {
            setProgress(100 * currentSeq / piecesAmount);
            return super.getProgress();
        }

        @Override
        public void run() {
            setMessage("Running");
            while(running() && currentSeq < piecesAmount){
                ContentPiece piece = pieces.get(currentSeq);
                ContentRequestCirrusEvent.this.sink.push(currentSeq, piece);

                currentSeq+=1;
            }
            setMessage("Finished");
        }
    }
}
