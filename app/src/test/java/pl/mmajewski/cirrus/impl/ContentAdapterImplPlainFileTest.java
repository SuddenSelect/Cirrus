package pl.mmajewski.cirrus.impl;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pl.mmajewski.cirrus.common.Constants;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;


public class ContentAdapterImplPlainFileTest {

    @Parameters("test-file")
    @Test(priority = 1)
    public void testIsSupported(String file) throws Exception {
        ContentAdapterImplPlainFile test = new ContentAdapterImplPlainFile();
        Assert.assertTrue(test.isSupported(file));
    }

    @Parameters("test-file")
    @Test(priority = 2)
    public void testAdapt(String file) throws Exception {
        ContentAdapterImplPlainFile test = new ContentAdapterImplPlainFile();
        test.suppressEventGeneration(true);
        test.adapt(file);
        File source = new File(file);
        int expectedChunksNum = (int) (1 + (source.length() / Constants.CHUNK_SIZE));
        Assert.assertEquals(test.getChunks().length, expectedChunksNum);
    }

    @Parameters("test-file")
    @Test(priority = 3)
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
                Assert.assertEquals(chunk.capacity(), expectedChunkSize, "Got: "+chunk.capacity()+", Expected: "+expectedChunkSize);
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
}