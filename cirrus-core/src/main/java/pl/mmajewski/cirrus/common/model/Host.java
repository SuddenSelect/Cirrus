package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.MultiValueAttribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Single program instance descriptor.
 *
 * Created by Maciej Majewski on 29/10/14.
 */
public class Host implements Serializable{
    private static final long serialVersionUID = 1681266000003L;

    private String cirrusId;//indexable
    private InetAddress physicalAddress;//indexable
    private LocalDateTime firstSeen;//indexable
    private LocalDateTime lastSeen;//indexable
    private LocalDateTime lastUpdated;//indexable
    private List<String> tags;//indexable
    private List<String/*contentID*/> availableContent;//indexable
    private Map<String/*contentID*/, Set<Integer>> sharedPieces;



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

    public Set<Integer> getSharedPieces(ContentMetadata contentMetadata) {
        return sharedPieces.containsKey(contentMetadata) ? sharedPieces.get(contentMetadata) : Collections.EMPTY_SET;
    }

    public void setSharedPieces(ContentMetadata contentMetadata, Set<Integer> sharedPieces) {
        this.sharedPieces.put(contentMetadata.getContentId(),sharedPieces);
    }
    public void setSharedPieces(String contentId, Set<Integer> sharedPieces) {
        if(this.sharedPieces == null){
            this.sharedPieces = new HashMap<>();
        }
        this.sharedPieces.put(contentId,sharedPieces);
    }

     /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////// CQEngine Attributes \\\\\\\\\\\\\\\
     public static final Attribute<Host, String> IDX_CIRRUS_ID = new SimpleAttribute<Host, String>("CIRRUS_ID") {
         public String getValue(Host obj) {
             return obj.cirrusId;
         }
     };
    public static final Attribute<Host, InetAddress> IDX_INET_ADDRESS = new SimpleAttribute<Host, InetAddress>("INET_ADDRESS") {
        public InetAddress getValue(Host obj) {
            return obj.physicalAddress;
        }
    };
    public static final Attribute<Host, LocalDateTime> IDX_FIRST_SEEN = new SimpleAttribute<Host, LocalDateTime>("FIRST_SEEN") {
        public LocalDateTime getValue(Host obj) {
            return obj.firstSeen;
        }
    };
    public static final Attribute<Host, LocalDateTime> IDX_LAST_SEEN = new SimpleAttribute<Host, LocalDateTime>("LAST_SEEN") {
        public LocalDateTime getValue(Host obj) {
            return obj.lastSeen;
        }
    };
    public static final Attribute<Host, LocalDateTime> IDX_LAST_UPDATED = new SimpleAttribute<Host, LocalDateTime>("LAST_UPDATED") {
        public LocalDateTime getValue(Host obj) {
            return obj.lastUpdated;
        }
    };
    public static final Attribute<Host, String> IDX_TAGS = new MultiValueAttribute<Host, String>("TAGS") {
        public List<String> getValues(Host obj) {
            return obj.tags;
        }
    };
    public static final Attribute<Host, String> IDX_AVAILABLE_CONTENT = new MultiValueAttribute<Host, String>("AVAILABLE_CONTENT") {
        public List<String> getValues(Host obj) {
            return obj.availableContent;
        }
    };

}
