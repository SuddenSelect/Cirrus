package pl.mmajewski.cirrus.impl.content.accessors;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.event.GenericCirrusEventThread;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.util.CirrusBlockingSequence;
import pl.mmajewski.cirrus.content.ContentAccessor;
import pl.mmajewski.cirrus.main.coreevents.AssembleContentCirrusEvent;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-06.
 */
public class ContentAccessorImplPlainBQueue implements ContentAccessor {
    private CirrusBlockingSequence<ContentPiece> piecesSequence;
    private ContentMetadata metadata;
    private final CirrusEventHandler coreEventHandler;
    private FileChannel dumpFile;
    private Map<String/*filename*/,Thread> fileDumpingThreads = new HashMap<>();

    public ContentAccessorImplPlainBQueue(ContentMetadata metadata, CirrusEventHandler coreEventHandler){
        this.coreEventHandler = coreEventHandler;
        this.piecesSequence = coreEventHandler.getContentPieceSink(metadata);
        setContentMetadata(metadata);
    }

    @Override
    public void setContentMetadata(ContentMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public void saveAsFile(String filename) throws FileNotFoundException, EventHandlerClosingCirrusException {
        File dump = new File(filename);
        dumpFile = new FileOutputStream(dump).getChannel();

        fileDumpingThreads.put(filename, new Thread(this.new FileDumperCirrusThread()));
        fileDumpingThreads.get(filename).start();

        AssembleContentCirrusEvent evt = new AssembleContentCirrusEvent();
        evt.setMetadata(metadata);
        evt.init();
        coreEventHandler.accept(evt);
    }

    /**
     * Waits for the FileDumpingThread to finish
     */
    public void waitForSaving(String filename){
        try {
            fileDumpingThreads.get(filename).join();
            fileDumpingThreads.remove(filename);
        } catch (InterruptedException e) {}
    }

    @Override
    public ByteArrayOutputStream stream() {
        //TODO implement if possible
        return null;
    }

    private class FileDumperCirrusThread extends GenericCirrusEventThread{
        private Logger logger = Logger.getLogger(FileDumperCirrusThread.class.getName());

        @Override
        public void run() {
            try {
                setMessage("Running");
                dumpFile.force(true);
                for(ContentPiece piece : piecesSequence){
                    if(!running()){
                        break;
                    }
                    if(piece==null){
                        setMessage("Interrupted");
                        return;
                    }
                    piece.getContent().rewind();
                    dumpFile.write(piece.getContent());
                }
                coreEventHandler.freeContentPieceSink(metadata);
                setMessage("Finished");
            }catch (IOException e) {
                setMessage("Saving problem: "+e.getMessage());
                logger.warning(e.getMessage());
            }
        }
    }
}
