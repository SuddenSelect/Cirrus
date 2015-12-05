package pl.mmajewski.cirrus.tests;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.content.ContentAdapter;
import pl.mmajewski.cirrus.impl.content.accessors.ContentAccessorImplPlainBQueue;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.appevents.CommitContentCirrusAppEvent;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

public class ContentAccessorImplPlainBQueueTest {

    private CirrusBasicApp app;

    public ContentAccessorImplPlainBQueueTest() throws UnknownHostException {
        app = new CirrusBasicApp(InetAddress.getLoopbackAddress());
    }


    @Parameters({"testFile","dumpFile"})
    @Test
    public void testSaveAsFile(String testFile, String dumpFile) throws Exception {
        File inFile = new File(testFile);
        File outFile = new File(dumpFile);
        Assert.assertTrue(outFile.canWrite()||!outFile.exists());


        ContentAdapter adapter = new ContentAdapterImplPlainFile(app.getAppEventHandler());
        adapter.adapt(testFile);

        app.getAppEventHandler().standby();

        ContentStorage storage = app.getAppEventHandler().getContentStorage();
        CirrusEventHandler coreEventHandler = app.getAppEventHandler().getCoreEventHandler();
        Assert.assertNotNull(storage);
        Assert.assertNotNull(storage.getAllContentMetadata());
        Assert.assertNotNull(coreEventHandler);
        Assert.assertFalse(storage.getAllContentMetadata().isEmpty());

        Set<ContentMetadata> allMetadatas = storage.getAllContentMetadata();
        Assert.assertEquals(allMetadatas.size(), 1);
        ContentMetadata firstMetadata = allMetadatas.iterator().next();
        Assert.assertNotNull(firstMetadata);


        CommitContentCirrusAppEvent evt = new CommitContentCirrusAppEvent();
        evt.init();
        evt.setBroadcastChange(false);
        app.getAppEventHandler().accept(evt);

        app.getAppEventHandler().standby();
        app.getAppEventHandler().getCoreEventHandler().standby();



        ContentAccessorImplPlainBQueue accessor = new ContentAccessorImplPlainBQueue(firstMetadata, coreEventHandler);
        accessor.saveAsFile(dumpFile);

        app.stopProcessingEvents();
        app.getAppEventHandler().standby();
        app.getAppEventHandler().getCoreEventHandler().standby();

        accessor.waitForSaving();
        Assert.assertTrue(FileUtils.contentEquals(inFile,outFile));

    }
}