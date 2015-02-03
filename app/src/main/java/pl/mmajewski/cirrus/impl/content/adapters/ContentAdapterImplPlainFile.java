package pl.mmajewski.cirrus.impl.content.adapters;

import pl.mmajewski.cirrus.appevents.NewContentPreparedCirrusAppEvent;
import pl.mmajewski.cirrus.binding.common.EventHandler;
import pl.mmajewski.cirrus.common.Constants;
import pl.mmajewski.cirrus.content.ContentAdapter;
import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;

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
                ByteBuffer chunk = ByteBuffer.allocateDirect(Constants.CHUNK_SIZE);
                fileChannel.read(chunk);
                chunks[i] = chunk;
            }
            //Reading last chunk
            if(lastChunkSize>0){
                ByteBuffer chunk = ByteBuffer.allocateDirect(lastChunkSize);
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
                EventHandler.getInstance().accept(evt);
            }
        }catch (Exception e){
            throw new ContentAdapterCirrusException(e,this);
        }
    }

    public ByteBuffer[] getChunks(){
        return chunks;
    }

    public void suppressEventGeneration(boolean suppress){
        eventGenereationSuppressed = suppress;
    }
}
