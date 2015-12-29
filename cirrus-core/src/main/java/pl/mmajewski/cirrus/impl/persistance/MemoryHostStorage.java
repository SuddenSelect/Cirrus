package pl.mmajewski.cirrus.impl.persistance;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.index.compound.CompoundIndex;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.persistence.offheap.OffHeapPersistence;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;


/**
 * Created by Maciej Majewski on 15/09/15.
 */
public class MemoryHostStorage extends AbstractHostStorage implements HostStorage {
    public MemoryHostStorage(Host localhost) {
        super(localhost, new ConcurrentIndexedCollection<Host>(OffHeapPersistence.onPrimaryKey(Host.IDX_CIRRUS_ID)){{
            addIndex(CompoundIndex.onAttributes(Host.IDX_TAGS, Host.IDX_LAST_UPDATED));
            addIndex(CompoundIndex.onAttributes(Host.IDX_TAGS, Host.IDX_LAST_SEEN));
            addIndex(HashIndex.onAttribute(Host.IDX_CIRRUS_ID));
            addIndex(HashIndex.onAttribute(Host.IDX_INET_ADDRESS));
            addIndex(HashIndex.onAttribute(Host.IDX_AVAILABLE_CONTENT));
        }});
    }
}
