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
import java.util.logging.Logger;

/**
 * Single usage ContentAccessor
 * Created by Maciej Majewski on 2015-02-06.
 */
public class ContentAccessorImplPlainBQueue implements ContentAccessor {
    private CirrusBlockingSequence<ContentPiece> piecesSequence;
    private ContentMetadata metadata;
    private final CirrusEventHandler coreEventHandler;
    private FileDumperCirrusThread fileDumpingThread = null;
    private File dumpFile = null;
    private FileChannel dumpFileChannel = null;
    private Thread runningThread = null;

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
        if(fileDumpingThread == null) {
            dumpFile = new File(filename);
            dumpFileChannel = new FileOutputStream(dumpFile).getChannel();

            fileDumpingThread = this.new FileDumperCirrusThread();
            runningThread = new Thread(fileDumpingThread);
            runningThread.start();

            AssembleContentCirrusEvent evt = new AssembleContentCirrusEvent();
            evt.setMetadata(metadata);
            evt.init();
            coreEventHandler.accept(evt);
        }else{
            throw new UnsupportedOperationException("Unsupported operation - object was already used for dwnloading "+metadata);
        }
    }

    /**
     * Waits for the FileDumpingThread to finish
     */
    public void waitForSaving(){
        try {
            if(runningThread != null) {
                runningThread.join();
            }
        } catch (InterruptedException e) {}
    }

    @Override
    public ByteArrayOutputStream stream() {
        //TODO implement if possible
        return null;
    }

    @Override
    public int getProgress() {
        if(fileDumpingThread != null){
            return fileDumpingThread.getProgress();
        }
        return 100;
    }

    @Override
    public int getMaxProgress() {
        return metadata.getPiecesAmount();
    }

    @Override
    public void cancel() {
        try {
            runningThread.interrupt();
            runningThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(dumpFile.exists()){
                try {
                    dumpFileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dumpFile.delete();
            }
        }
    }

    private class FileDumperCirrusThread extends GenericCirrusEventThread{
        private Logger logger = Logger.getLogger(FileDumperCirrusThread.class.getName());

        @Override
        public void run() {
            try {
                setMessage("Running");
                dumpFileChannel.force(true);
                int progress = 0;
                for(ContentPiece piece : piecesSequence){
                    if(!running()){
                        break;
                    }
                    if(piece==null){
                        setMessage("Interrupted");
                        return;
                    }
                    piece.getContent().rewind();
                    dumpFileChannel.write(piece.getContent());
                    setProgress(++progress);
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
