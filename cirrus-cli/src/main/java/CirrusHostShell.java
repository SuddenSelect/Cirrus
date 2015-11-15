import asg.cliche.Command;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.util.CirrusIdGenerator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class CirrusHostShell extends CirrusCommonShell {
    private Set<Host> hosts;
    private Host host = null;

    public CirrusHostShell(Set<Host> hosts) {
        this.hosts = hosts;
    }

    @Command
    public void createLocalhost(){
        host = Host.getLocalHost();
    }

    @Command
    public void create(String address, int port) throws UnknownHostException {
        Host host = new Host();
        host.setPhysicalAddress(InetAddress.getByName(address));
        host.setPort(port);
        host.setCirrusId(CirrusIdGenerator.generateHostId());
        host.setFirstSeen(LocalDateTime.now());
        host.setLastSeen(LocalDateTime.now());
        host.setTags(Collections.EMPTY_LIST);
        this.host = host;
    }

    @Command
    public void test(Host host){
        this.host = host;
    }

    @Command
    public String list(){
        if(host==null){
            return "No host";
        }
        return listClassSetters(Host.class);
    }

    @Command
    public String listHosts(){
        return listCollection(hosts);
    }

    @Command
    public String host(){
        return host.toString();
    }

    @Command
    public void add(){
        hosts.add(host);
    }

    @Command
    public void reset(){
        hosts.clear();
    }

    @Command
    public void set(String property, String className, String value) throws Exception {
        super.set(host,property, Class.forName(className), value);
    }

}
