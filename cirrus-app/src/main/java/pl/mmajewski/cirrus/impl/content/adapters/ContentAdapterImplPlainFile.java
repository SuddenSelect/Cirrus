package pl.mmajewski.cirrus.impl.content.adapters;

import pl.mmajewski.cirrus.common.Constants;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.content.ContentAdapter;
import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;
import pl.mmajewski.cirrus.main.appevents.NewContentPreparedCirrusAppEvent;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-03.
 *
 * Simple implementation loading whole file into memory
 */
public class ContentAdapterImplPlainFile implements ContentAdapter {
    private static Logger logger = Logger.getLogger(ContentAdapterImplPlainFile.class.getName());
    private String contentSource;
    private ByteBuffer[] chunks;
    private boolean eventGenereationSuppressed;
    private CirrusEventHandler eventHandler;

    /**
     * Constructor for quiet (non-eventful) processing
     */
    public ContentAdapterImplPlainFile(){
        eventHandler = null;
        eventGenereationSuppressed = true;
    }

    /**
     * Constructor for eventful processing (still can be turned off with suppressEventGeneration method)
     * @param eventHandler handler for generated events
     */
    public ContentAdapterImplPlainFile(CirrusEventHandler eventHandler){
        this.eventHandler = eventHandler;
        this.eventGenereationSuppressed = false;
    }



    @Override
    public String getContentSource() {
        return contentSource;
    }

    @Override
    public String getContentTypeDescription() {
        return "File";
    }

    @Override
    public  boolean isSupported(String contentSource) {
        boolean supported = false;
        try {
            File source = new File(contentSource);
            supported = source.isFile() && source.canRead();
        }catch (Exception e){
            logger.finest(e.getMessage());
        }
        return supported;
    }

    @Override
    public void adapt(String contentSource) throws ContentAdapterCirrusException {
        try{
            this.contentSource = contentSource;
            File source = new File(contentSource);

            FileChannel fileChannel = new FileInputStream(source).getChannel();

            int numberOfChunks = (int) (source.length() / Constants.CHUNK_SIZE);
            int lastChunkSize = 0;
            if(source.length() % Constants.CHUNK_SIZE > 0){
                numberOfChunks += 1;
                lastChunkSize = (int) (Constants.CHUNK_SIZE - ((numberOfChunks * Constants.CHUNK_SIZE) - source.length()));
            }

            chunks = new ByteBuffer[numberOfChunks];

            //Reading all chunks except wierd-sized last
            for (int i = 0; i < numberOfChunks-1; i++) {
//                ByteBuffer chunk = ByteBuffer.allocateDirect(Constants.CHUNK_SIZE);
                ByteBuffer chunk = ByteBuffer.allocate(Constants.CHUNK_SIZE);
                fileChannel.read(chunk);
                chunks[i] = chunk;
            }
            //Reading last chunk
            if(lastChunkSize>0){
//                ByteBuffer chunk = ByteBuffer.allocateDirect(lastChunkSize);
                ByteBuffer chunk = ByteBuffer.allocate(lastChunkSize);
                fileChannel.read(chunk);
                chunk.flip();//make buffer ready for read
                chunks[numberOfChunks-1] = chunk;
            }

            if(!eventGenereationSuppressed) {
                ContentFactory contentFactory = new ContentFactory(numberOfChunks);
                contentFactory.feed(chunks);

                NewContentPreparedCirrusAppEvent evt = new NewContentPreparedCirrusAppEvent();
                evt.init();
                evt.setMetadata(contentFactory.getMetadata());
                evt.setPieces(contentFactory.getPieces());
                eventHandler.accept(evt);
            }
        }catch (Exception e){
            throw new ContentAdapterCirrusException(e,this);
        }
    }

    public ByteBuffer[] getChunks(){
        return chunks;
    }

    public void suppressEventGeneration(boolean suppress){
        if(eventHandler == null && !suppress){
            throw new UnsupportedOperationException("Instance was initialized as non-eventful");
        }
        eventGenereationSuppressed = suppress;
    }

    public CirrusEventHandler getEventHandler(){
        return eventHandler;
    }
}
