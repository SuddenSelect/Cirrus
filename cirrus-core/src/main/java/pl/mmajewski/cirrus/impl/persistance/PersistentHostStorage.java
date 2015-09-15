package pl.mmajewski.cirrus.impl.persistance;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.persistence.disk.DiskPersistence;
import pl.mmajewski.cirrus.common.model.Host;

import java.io.File;
import java.util.Set;

/**
 * Created by Maciej Majewski on 15/09/15.
 */
public class PersistentHostStorage extends MemoryHostStorage {
    private final IndexedCollection<Host> hosts;
    private final File hostsFile;

    public PersistentHostStorage(Host localhost, File persistancePath) {
        super(localhost);
        hostsFile = new File(persistancePath,"hosts");
        hosts = new ConcurrentIndexedCollection<Host>(
                DiskPersistence.onPrimaryKeyInFile(
                        Host.IDX_CIRRUS_ID,
                        hostsFile));
        load();
    }

    private void load(){
        super.updateHosts(hosts);
    }

    @Override
    public void updateHosts(Set<Host> hosts) {
        super.updateHosts(hosts);
        this.hosts.removeAll(hosts);
        this.hosts.addAll(hosts);
    }

    @Override
    public void deleteHosts(Set<Host> hosts) {
        super.deleteHosts(hosts);
        this.hosts.removeAll(hosts);
    }
}
