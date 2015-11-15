import asg.cliche.Command;
import asg.cliche.Shell;
import asg.cliche.ShellDependent;
import asg.cliche.ShellFactory;
import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.CirrusCoreServer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class CirrusCLI extends CirrusCommonShell implements ShellDependent {
    private Shell shell;

    private CirrusBasicApp cirrusBasicApp = new CirrusBasicApp();
    private CirrusEvent event = null;
    private Set<Host> hosts = new HashSet<>();

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
    public String storedHost(String cirrusId){
        CirrusCoreServer srv = (CirrusCoreServer)cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
        HostStorage hostStorage = srv.getHostStorage();
        return hostStorage.fetchHost(cirrusId).toString();
    }

    @Command
    public String storedMetadataApp(){
        CirrusCoreServer srv = (CirrusCoreServer)cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
        ContentStorage contentStorage = srv.getContentStorage();
        return super.listCollection(contentStorage.getAllContentMetadata());
    }
    @Command
    public String storedMetadataCore(){
        CirrusBasicApp.AppEventHandler srv = cirrusBasicApp.getAppEventHandler();
        ContentStorage contentStorage = srv.getContentStorage();
        return super.listCollection(contentStorage.getAllContentMetadata());
    }

    @Command
    public void reset(){
        event = null;
    }

    @Command
    public void create(String className) throws Exception {
        Class eventClass = Class.forName(className);
        event = (CirrusEvent) eventClass.newInstance();
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
    public void applyCore() throws Exception {
        cirrusBasicApp.getAppEventHandler().getCoreEventHandler().accept(event);
    }

    @Command
    public void applyApp() throws Exception {
        cirrusBasicApp.getAppEventHandler().accept(event);
    }

    @Command
    public String listProperties() throws Exception {
        if(event==null){
            return "No event";
        }
        Class eventClass = event.getClass();
        return listClassSetters(eventClass);
    }

    @Command
    public String listHosts(){
        return listCollection(hosts);
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
    public void makeHosts() throws IOException {
        ShellFactory.createSubshell("hosts", shell, null, new CirrusHostShell(hosts)).commandLoop();
    }

    @Command
    public void set(String what, String property) throws Exception {
        if("host-collection".equals(what)){
            super.set(event,property,Set.class,hosts);
        }
    }

    @Command
    public void set(String property, String className, String value) throws Exception {
        super.set(event,property,Class.forName(className),value);
    }

    public static void main(String[] args) throws IOException {
        ShellFactory.createConsoleShell("cirrus-cli", "Welcome to simple Cirrus CLI!", new CirrusCLI()).commandLoop();
    }

    @Override
    public void cliSetShell(Shell shell) {
        this.shell = shell;
    }
}
