package pl.mmajewski.cirrus.tests;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.content.ContentAdapter;
import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.appevents.CommitContentCirrusAppEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CirrusBasicAppTest {

    private void testContentStorageNotEmpty(ContentStorage storage, String which){
        Assert.assertNotNull(storage, which);
        Assert.assertNotNull(storage.getAllContentMetadata(), which);
        Assert.assertFalse(storage.getAllContentMetadata().isEmpty(),which);
        for(ContentMetadata meta : storage.getAllContentMetadata()) {
            Assert.assertNotEquals(meta.getPiecesAmount(),0);
            Assert.assertNotNull(storage.getAvailablePieces(meta),which);
            Assert.assertFalse(storage.getAvailablePieces(meta).isEmpty(),which);
        }
    }

    @Parameters("testFile")
    @Test
    public void testCommit(String file) throws ContentAdapterCirrusException, InterruptedException, EventHandlerClosingCirrusException, UnknownHostException {
        CirrusBasicApp app = new CirrusBasicApp(InetAddress.getLoopbackAddress());
        Assert.assertNotNull(app.getAppEventHandler());
        Assert.assertNotNull(app.getAppEventHandler().getContentStorage());
        Assert.assertNotNull(app.getAppEventHandler().getCoreEventHandler());
        Assert.assertNotNull(app.getAppEventHandler().getCoreEventHandler().getContentStorage());
        Assert.assertNotNull(app.getAppEventHandler().getCoreEventHandler().getAppEventHandler());
        Assert.assertEquals(app.getAppEventHandler(), app.getAppEventHandler().getCoreEventHandler().getAppEventHandler());
        Assert.assertNotEquals(app.getAppEventHandler().getContentStorage(),app.getAppEventHandler().getCoreEventHandler().getContentStorage());

        ContentAdapter adapter = new ContentAdapterImplPlainFile(app.getAppEventHandler());
        adapter.adapt(file);

        app.getAppEventHandler().standby();
        app.getAppEventHandler().getCoreEventHandler().standby();

        ContentStorage coreStorage = app.getAppEventHandler().getCoreEventHandler().getContentStorage();
        ContentStorage appStorage = app.getAppEventHandler().getContentStorage();
        testContentStorageNotEmpty(appStorage, "app");
        Assert.assertTrue(coreStorage.getAllContentMetadata().isEmpty());

        Assert.assertNotEquals(appStorage, coreStorage);

        CommitContentCirrusAppEvent evt = new CommitContentCirrusAppEvent();
        evt.init();
        app.getAppEventHandler().accept(evt);

        app.getAppEventHandler().standby();
        app.getAppEventHandler().getCoreEventHandler().standby();

        testContentStorageNotEmpty(coreStorage, "core");
    }

}