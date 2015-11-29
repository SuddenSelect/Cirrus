import asg.cliche.Command;
import asg.cliche.ShellFactory;
import asg.cliche.ShellManageable;
import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.content.ContentAccessor;
import pl.mmajewski.cirrus.impl.content.accessors.ContentAccessorImplPlainBQueue;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.CirrusCoreServer;
import pl.mmajewski.cirrus.main.appevents.AdaptFileCirrusAppEvent;
import pl.mmajewski.cirrus.main.appevents.CleanupContentCirrusAppEvent;
import pl.mmajewski.cirrus.main.appevents.CommitContentCirrusAppEvent;
import pl.mmajewski.cirrus.main.coreevents.network.SendSignupCirrusEvent;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.ServiceLoader;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class CirrusCLI implements ShellManageable {
    private CirrusBasicApp cirrusBasicApp;

    private String listCollection(Collection collection){
        StringBuilder sb = new StringBuilder();
        for(Object object : collection){
            sb.append("+ ");
            sb.append(object.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Command
    public String start(String localhostAddress) throws UnknownHostException {
        if(cirrusBasicApp==null) {
            cirrusBasicApp = new CirrusBasicApp(InetAddress.getByName(localhostAddress));
            return "CirrusBasicApp started";
        }
        return "ERROR: CirrusBasicApp already started";
    }

    @Command
    public void stop(){
        cirrusBasicApp.stopProcessingEvents();
    }

    @Command
    public String awaitingEvents() {
        return new MessageFormat("Core: {0}, App: {1}").format(new Object[]{
                cirrusBasicApp.getAppEventHandler().getCoreEventHandler().hasAwaitingEvents(),
                cirrusBasicApp.getAppEventHandler().hasAwaitingEvents()});
    }

    @Command
    public String storedHosts(){
        CirrusCoreServer srv = (CirrusCoreServer)cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
        HostStorage hostStorage = srv.getHostStorage();
        return listCollection(hostStorage.fetchAllHosts());
    }

    @Command
    public String storedMetadataCore(){
        CirrusCoreServer srv = (CirrusCoreServer)cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
        ContentStorage contentStorage = srv.getContentStorage();
        return listCollection(contentStorage.getAllContentMetadata());
    }
    @Command
    public String storedMetadataApp(){
        CirrusBasicApp.AppEventHandler srv = cirrusBasicApp.getAppEventHandler();
        ContentStorage contentStorage = srv.getContentStorage();
        return listCollection(contentStorage.getAllContentMetadata());
    }

    @Command
    public void failures(){
        CirrusBasicApp.AppEventHandler srv = cirrusBasicApp.getAppEventHandler();
        String failure;
        while((failure=srv.popFailure())!=null){
            System.err.println("---- FAILURE ----\n"+failure+"------------------");
        }
    }

    @Command
    public String listEvents(){
        ServiceLoader<CirrusEvent> loader = ServiceLoader.load(CirrusEvent.class);
        StringBuilder sb = new StringBuilder();
        for(CirrusEvent impl : loader){
            sb.append("-> ");
            sb.append(impl.getClass().getName());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Command
    public void adaptFile(String file) throws EventHandlerClosingCirrusException {
        AdaptFileCirrusAppEvent event = new AdaptFileCirrusAppEvent();
        event.setFile(file);
        cirrusBasicApp.accept(event);
    }

    @Command
    public void commit() throws EventHandlerClosingCirrusException {
        CommitContentCirrusAppEvent event = new CommitContentCirrusAppEvent();
        cirrusBasicApp.accept(event);
    }

    @Command
    public void rollback(){
        cirrusBasicApp.getAppEventHandler().resetStorage();
    }

    @Command
    public void cleanupContent() throws EventHandlerClosingCirrusException {
        CleanupContentCirrusAppEvent event = new CleanupContentCirrusAppEvent();
        cirrusBasicApp.accept(event);
    }

    @Command
    public String localhost(){
        CirrusCoreServer coreServer = (CirrusCoreServer) cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
        return coreServer.getHostStorage().fetchLocalHost().toString();
    }

    @Command
    public void signup(String cirrusId, String ip, int port) throws UnknownHostException, EventHandlerClosingCirrusException {
        InetAddress bootstrap = InetAddress.getByName(ip);
        Host host = new Host();
        host.setPhysicalAddress(bootstrap);
        host.setPort(port);
        host.setCirrusId(cirrusId);

        SendSignupCirrusEvent event = new SendSignupCirrusEvent();
        event.setHost(host);
        cirrusBasicApp.accept(event);
    }

    @Command
    public String availableContent(){
        ContentStorage contentStorage = cirrusBasicApp.getAppEventHandler().getCoreEventHandler().getContentStorage();
        return this.listCollection(contentStorage.getAllContentMetadata());
    }

    @Command
    public void assemble(String contentId, String destination) throws Exception {
        File file = new File(destination);
        if(file.exists()){
            throw new Exception("File already exists");
        }else{
            file.createNewFile();
        }

        CirrusEventHandler coreEventHandler = cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
        ContentStorage contentStorage = coreEventHandler.getContentStorage();
        ContentMetadata metadata = contentStorage.getContentMetadata(contentId);
        ContentAccessor contentAccessor = new ContentAccessorImplPlainBQueue(metadata, coreEventHandler);
        contentAccessor.saveAsFile(destination);
    }


    private static String address = null;
    public static void main(String[] args) throws IOException {
        if(args.length>0){
            address = args[0];
        }
        ShellFactory.createConsoleShell("cirrus-cli", "Welcome to simple Cirrus CLI!", new CirrusCLI()).commandLoop();
    }

    @Override
    public void cliEnterLoop() {
        if(address!=null){
            try {
                start(address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cliLeaveLoop() {
        if(cirrusBasicApp!=null) {
            stop();
        }
    }
}
