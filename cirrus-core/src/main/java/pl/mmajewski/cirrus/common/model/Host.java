package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.MultiValueAttribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import pl.mmajewski.cirrus.binding.CirrusCoreFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Single program instance descriptor.
 * <p>
 * Created by Maciej Majewski on 29/10/14.
 */
public class Host implements Serializable, Comparable<Host> {
    public static Host getLocalhost() {
        return CirrusCoreFactory.getLocalhost();
    }

    private String cirrusId;//indexable
    private String physicalAddress;//indexable
    // IMPORTANT: InetAddress fields become "0.0.0.0" when inserted into CQEngine collection
    // Therefore, field needs to be stored differently. String class was chosen instead.

    private Integer port;
    private LocalDateTime firstSeen;//indexable
    private LocalDateTime lastSeen;//indexable
    private LocalDateTime lastUpdated;//indexable
    private List<String> tags = new ArrayList<>(0);//indexable
    private Set<String/*contentID*/> availableContent;//indexable
    private Map<String/*contentID*/, Set<Integer>> sharedPieces;
    private /*transient*/ Integer latency = -1;
    // IMPORTANT: transient fields become NULL when inserted into CQEngine collection
    // Therefore, field needs to be reset by server upon receiving

    public void simulateFieldTransiency(){
        latency = -1;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public InetAddress getPhysicalAddress() {
        try {
            return InetAddress.getByName(physicalAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setPhysicalAddress(InetAddress physicalAddress) {
        this.physicalAddress = physicalAddress.getHostAddress();
    }

    public String getCirrusId() {
        return cirrusId;
    }

    public void setCirrusId(String cirrusId) {
        this.cirrusId = cirrusId;
    }

    public LocalDateTime getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(LocalDateTime firstSeen) {
        this.firstSeen = firstSeen;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Set<String> getAvailableContent() {
        return availableContent;
    }

    public void setAvailableContent(Set<String> availableContent) {
        this.availableContent = availableContent;
    }

    public Map<String, Set<Integer>> getSharedPiecesMap() {
        return sharedPieces;
    }

    public void setSharedPiecesMap(Map<String, Set<Integer>> sharedPieces) {
        this.sharedPieces = sharedPieces;
    }

    public Set<Integer> getSharedPieces(String contentId) {
        return sharedPieces.containsKey(contentId) ? sharedPieces.get(contentId) : Collections.EMPTY_SET;
    }

    public void setSharedPieces(ContentMetadata contentMetadata, Set<Integer> sharedPieces) {
        this.setSharedPieces(contentMetadata.getContentId(), sharedPieces);
    }

    public void setSharedPieces(String contentId, Set<Integer> sharedPieces) {
        if (this.sharedPieces == null) {
            this.sharedPieces = new HashMap<>();
        }
        this.sharedPieces.put(contentId, sharedPieces);
    }

    public Integer getLatency() {
        return latency;
    }

    public void setLatency(Integer latency) {
        this.latency = latency;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Host){
            return cirrusId.equals(((Host) obj).getCirrusId());
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(Host o) {
        return this.cirrusId.compareTo(o.cirrusId);
    }

    @Override
    public String toString() {
        return "Host{ "+getCirrusId()+" @ "+getPhysicalAddress().getHostAddress()+" : "+getPort()+" }";
    }

    /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////// CQEngine Attributes \\\\\\\\\\\\\\\
    public static final SimpleAttribute<Host, String> IDX_CIRRUS_ID = new SimpleAttribute<Host, String>("IDX_CIRRUS_ID") {
        @Override
        public String getValue(Host obj, QueryOptions queryOptions) {
            return obj.cirrusId;
        }
    };
    public static final SimpleAttribute<Host, String> IDX_INET_ADDRESS = new SimpleAttribute<Host, String>("IDX_INET_ADDRESS") {
        @Override
        public String getValue(Host obj, QueryOptions queryOptions) {
            return obj.physicalAddress;
        }
    };
    public static final SimpleAttribute<Host, LocalDateTime> IDX_FIRST_SEEN = new SimpleAttribute<Host, LocalDateTime>("IDX_FIRST_SEEN") {
        @Override
        public LocalDateTime getValue(Host obj, QueryOptions queryOptions) {
            return obj.firstSeen;
        }
    };
    public static final SimpleAttribute<Host, LocalDateTime> IDX_LAST_SEEN = new SimpleAttribute<Host, LocalDateTime>("IDX_LAST_SEEN") {
        @Override
        public LocalDateTime getValue(Host obj, QueryOptions queryOptions) {
            return obj.lastSeen;
        }
    };
    public static final SimpleAttribute<Host, Integer> IDX_LATENCY = new SimpleAttribute<Host, Integer>("IDX_LATENCY") {
        @Override
        public Integer getValue(Host obj, QueryOptions queryOptions) {
            return obj.latency;
        }
    };
    public static final SimpleAttribute<Host, LocalDateTime> IDX_LAST_UPDATED = new SimpleAttribute<Host, LocalDateTime>("IDX_LAST_UPDATED") {
        @Override
        public LocalDateTime getValue(Host obj, QueryOptions queryOptions) {
            return obj.lastUpdated;
        }
    };
    public static final MultiValueAttribute<Host, String> IDX_TAGS = new MultiValueAttribute<Host, String>("IDX_TAGS") {
        @Override
        public List<String> getValues(Host obj, QueryOptions queryOptions) {
            return obj.tags;
        }
    };
    public static final MultiValueAttribute<Host, String> IDX_AVAILABLE_CONTENT = new MultiValueAttribute<Host, String>("IDX_AVAILABLE_CONTENT") {
        @Override
        public Set<String> getValues(Host obj, QueryOptions queryOptions) {
            return obj.availableContent;
        }
    };

}
