package pl.mmajewski.cirrus.impl.persistance;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.compound.CompoundIndex;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.persistence.offheap.OffHeapPersistence;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.googlecode.cqengine.query.QueryFactory.*;


/**
 * Created by Maciej Majewski on 15/09/15.
 */
public class MemoryHostStorage implements HostStorage {
    private final IndexedCollection<Host> hosts =
            new ConcurrentIndexedCollection<Host>(OffHeapPersistence.onPrimaryKey(Host.IDX_CIRRUS_ID)){{
                addIndex(CompoundIndex.onAttributes(Host.IDX_TAGS, Host.IDX_LAST_UPDATED));
                addIndex(CompoundIndex.onAttributes(Host.IDX_TAGS, Host.IDX_LAST_SEEN));
                addIndex(HashIndex.onAttribute(Host.IDX_CIRRUS_ID));
                addIndex(HashIndex.onAttribute(Host.IDX_INET_ADDRESS));
                addIndex(HashIndex.onAttribute(Host.IDX_AVAILABLE_CONTENT));
    }};
    private Host localhost;

    public MemoryHostStorage(Host localhost){
        this.localhost = localhost;
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
        return new HashSet<>(hosts);
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
