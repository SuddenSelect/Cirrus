package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.MultiValueAttribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Single program instance descriptor.
 * <p>
 * Created by Maciej Majewski on 29/10/14.
 */
public class Host implements Serializable, Comparable<Host> {
    private static final long serialVersionUID = 1681266000003L;

    private String cirrusId;//indexable
    private InetAddress physicalAddress;//indexable
    private LocalDateTime firstSeen;//indexable
    private LocalDateTime lastSeen;//indexable
    private LocalDateTime lastUpdated;//indexable
    private List<String> tags;//indexable
    private List<String/*contentID*/> availableContent;//indexable
    private Map<String/*contentID*/, Set<Integer>> sharedPieces;
    private transient Integer latency = -1;


    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public InetAddress getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(InetAddress physicalAddress) {
        this.physicalAddress = physicalAddress;
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

    public List<String> getAvailableContent() {
        return availableContent;
    }

    public void setAvailableContent(List<String> availableContent) {
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

    /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////// CQEngine Attributes \\\\\\\\\\\\\\\
    public static final SimpleAttribute<Host, String> IDX_CIRRUS_ID = new SimpleAttribute<Host, String>() {
        @Override
        public String getValue(Host obj, QueryOptions queryOptions) {
            return obj.cirrusId;
        }
    };
    public static final SimpleAttribute<Host, InetAddress> IDX_INET_ADDRESS = new SimpleAttribute<Host, InetAddress>() {
        @Override
        public InetAddress getValue(Host obj, QueryOptions queryOptions) {
            return obj.physicalAddress;
        }
    };
    public static final SimpleAttribute<Host, LocalDateTime> IDX_FIRST_SEEN = new SimpleAttribute<Host, LocalDateTime>() {
        @Override
        public LocalDateTime getValue(Host obj, QueryOptions queryOptions) {
            return obj.firstSeen;
        }
    };
    public static final SimpleAttribute<Host, LocalDateTime> IDX_LAST_SEEN = new SimpleAttribute<Host, LocalDateTime>() {
        @Override
        public LocalDateTime getValue(Host obj, QueryOptions queryOptions) {
            return obj.lastSeen;
        }
    };
    public static final SimpleAttribute<Host, Integer> IDX_LATENCY = new SimpleAttribute<Host, Integer>() {
        @Override
        public Integer getValue(Host obj, QueryOptions queryOptions) {
            return obj.latency;
        }
    };
    public static final SimpleAttribute<Host, LocalDateTime> IDX_LAST_UPDATED = new SimpleAttribute<Host, LocalDateTime>() {
        @Override
        public LocalDateTime getValue(Host obj, QueryOptions queryOptions) {
            return obj.lastUpdated;
        }
    };
    public static final MultiValueAttribute<Host, String> IDX_TAGS = new MultiValueAttribute<Host, String>() {
        @Override
        public List<String> getValues(Host obj, QueryOptions queryOptions) {
            return obj.tags;
        }
    };
    public static final MultiValueAttribute<Host, String> IDX_AVAILABLE_CONTENT = new MultiValueAttribute<Host, String>() {
        @Override
        public List<String> getValues(Host obj, QueryOptions queryOptions) {
            return obj.availableContent;
        }
    };

}
