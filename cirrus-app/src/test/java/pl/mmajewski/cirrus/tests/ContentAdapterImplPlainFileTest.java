package pl.mmajewski.cirrus.tests;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pl.mmajewski.cirrus.main.Constants;
import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.util.CirrusBlockingSequence;
import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;
import pl.mmajewski.cirrus.main.appevents.NewContentPreparedCirrusAppEvent;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;


public class ContentAdapterImplPlainFileTest {

    @Parameters("testFile")
    @Test
    public void testIsSupported(String file) throws Exception {
        ContentAdapterImplPlainFile test = new ContentAdapterImplPlainFile();
        Assert.assertTrue(test.isSupported(file));
    }

    @Parameters("testFile")
    @Test
    public void testAdapt(String file) throws Exception {
        ContentAdapterImplPlainFile test = new ContentAdapterImplPlainFile();
        test.suppressEventGeneration(true);
        test.adapt(file);
        File source = new File(file);
        int expectedChunksNum = (int) (1 + (source.length() / Constants.CHUNK_SIZE));
        Assert.assertEquals(test.getChunks().length, expectedChunksNum);
    }

    @Test
    @Parameters({"testFile"})
    public void testGetChunks(String file) throws Exception {
        ContentAdapterImplPlainFile test = new ContentAdapterImplPlainFile();
        Assert.assertTrue(test.isSupported(file));
        test.suppressEventGeneration(true);
        test.adapt(file);

        File source = new File(file);
        int expectedChunksNum = (int) (source.length() / Constants.CHUNK_SIZE);
        if(source.length() % Constants.CHUNK_SIZE > 0){
            expectedChunksNum += 1;
        }

        int expectedChunkSize = Constants.CHUNK_SIZE;
        int expectedLastChunkSize = (int) (Constants.CHUNK_SIZE - ((expectedChunksNum * Constants.CHUNK_SIZE) - source.length()));

        ByteBuffer[] chunks = test.getChunks();
        Assert.assertNotNull(chunks);
        Assert.assertNotEquals(chunks.length, 0);

        ByteBuffer lastChunk = chunks[chunks.length-1];
        for(ByteBuffer chunk : chunks){
            if(chunk!=lastChunk){
                Assert.assertEquals(chunk.capacity(), expectedChunkSize, "Got: " + chunk.capacity() + ", Expected: " + expectedChunkSize);
            }else{
                Assert.assertEquals(chunk.capacity(), expectedLastChunkSize, "Got: "+chunk.capacity()+", Expected: "+expectedLastChunkSize);
            }
        }

        //Content comparison
        FileInputStream input = new FileInputStream(source);
        int current = input.read();
        for(ByteBuffer chunk : chunks){
            for (int i = 0; i < chunk.capacity(); i++) {
                Assert.assertEquals(current, chunk.get(i));

                current = input.read();
            }
        }
    }

    private static class DummyHandler implements CirrusEventHandler{

        private boolean passed = false;

        @Override
        public void accept(CirrusEvent event) {
            NewContentPreparedCirrusAppEvent evt = (NewContentPreparedCirrusAppEvent) event;
            Assert.assertNotNull(evt.getMetadata());
            Assert.assertNotEquals(0, evt.getMetadata().getPiecesAmount());
            Assert.assertNotNull(evt.getPieces());
            Assert.assertFalse(evt.getPieces().isEmpty());
            passed = true;
        }

        @Override
        public boolean hasAwaitingEvents() {
            return false;
        }

        @Override
        public void standby() throws InterruptedException {

        }

        @Override
        public void setAppEventHandler(CirrusEventHandler handler) {

        }

        @Override
        public CirrusEventHandler getAppEventHandler() {
            return null;
        }

        @Override
        public void setContentStorage(ContentStorage contentStorage) {

        }

        @Override
        public ContentStorage getContentStorage() {
            return null;
        }

        public void test() {
            Assert.assertTrue(passed);
        }

        @Override
        public void pushFailure(String failure) {
            throw new RuntimeException(failure);
        }

        @Override
        public String popFailure() {
            return null;
        }

        @Override
        public String getLocalCirrusId() {
            return "dummy-cirrus-id";
        }

        @Override
        public CirrusBlockingSequence<ContentPiece> getContentPieceSink(ContentMetadata metadata) {
            return null;
        }

        @Override
        public void freeContentPieceSink(ContentMetadata metadata) {

        }
    }

    @Parameters("testFile")
    @Test
    public void testEventGeneration(String file) throws ContentAdapterCirrusException {
        DummyHandler handler = new DummyHandler();
        ContentAdapterImplPlainFile adapter = new ContentAdapterImplPlainFile(handler);
        adapter.adapt(file);
        handler.test();
    }
}