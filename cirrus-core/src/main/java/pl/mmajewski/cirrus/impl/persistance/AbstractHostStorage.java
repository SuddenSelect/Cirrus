package pl.mmajewski.cirrus.impl.persistance;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;

import java.util.HashSet;
import java.util.Set;

import static com.googlecode.cqengine.query.QueryFactory.*;

/**
 * Created by Maciej Majewski on 29/12/15.
 */
/*package*/ class AbstractHostStorage implements HostStorage {
    private IndexedCollection<Host> hosts;
    private Host localhost;

    public AbstractHostStorage(Host localhost, IndexedCollection<Host> hosts){
        this.localhost = localhost;
        this.hosts = hosts;
        if(localhost!=null) {
            hosts.add(localhost);
        }
    }

    @Override
    public void updateHosts(Set<Host> hosts) {
        if(hosts!=null) {
            this.hosts.removeAll(hosts);
            this.hosts.addAll(hosts);
            if (hosts.contains(localhost)) {
                for (Host host : hosts) {
                    if (host.equals(localhost)) {
                        localhost = host;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void deleteHosts(Set<Host> hosts) {
        this.hosts.removeAll(hosts);
    }

    @Override
    public Host fetchLocalHost() {
        return localhost;
    }

    @Override
    public Host fetchHost(String cirrusId) {
        Query<Host> query = equal(Host.IDX_CIRRUS_ID, cirrusId);
        return hosts.retrieve(query).uniqueResult();
    }

    @Override
    public Iterable<Host> fetchSharers(ContentMetadata contentMetadata) {
        Query<Host> query = equal(Host.IDX_AVAILABLE_CONTENT, contentMetadata.getContentId());
        QueryOptions options = queryOptions(orderBy(ascending(Host.IDX_LATENCY)));
        return hosts.retrieve(query, options);
    }

    @Override
    public Set<Host> fetchAllHosts() {
        Set<Host> all = new HashSet<>();
        Query<Host> query = all(Host.class);
        for(Host host : hosts.retrieve(query)){
            all.add(host);
        }
        return all;
    }

    @Override
    public Iterable<Host> fetchAllHostsAscendingLatency() {
        Query<Host> query = all(Host.class);
        QueryOptions options = queryOptions(orderBy(ascending(Host.IDX_LATENCY)));
        return hosts.retrieve(query, options);
    }

    @Override
    public Integer size(){
        return hosts.size();
    }
}
