import asg.cliche.Command;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import asg.cliche.ShellManageable;
import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.CirrusCoreServer;
import pl.mmajewski.cirrus.main.appevents.AdaptFileCirrusAppEvent;
import pl.mmajewski.cirrus.main.appevents.CleanupContentCirrusAppEvent;
import pl.mmajewski.cirrus.main.coreevents.network.SendSignupCirrusEvent;

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
    private Shell shell;

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

    @Command(abbrev = "ae")
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
    public String storedMetadataApp(){
        CirrusCoreServer srv = (CirrusCoreServer)cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
        ContentStorage contentStorage = srv.getContentStorage();
        return listCollection(contentStorage.getAllContentMetadata());
    }
    @Command
    public String storedMetadataCore(){
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


    public static void main(String[] args) throws IOException {
        ShellFactory.createConsoleShell("cirrus-cli", "Welcome to simple Cirrus CLI!", new CirrusCLI()).commandLoop();
    }

    @Override
    public void cliEnterLoop() {

    }

    @Override
    public void cliLeaveLoop() {
        stop();
    }
}
