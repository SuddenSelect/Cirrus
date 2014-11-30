package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Model class representing whole File/Content.
 * Propagated through CirrusEvent's.
 * It is intended for every Host to posses info about all
 * available content and retrieve content pieces when necessary.
 * Created by Maciej Majewski on 29/10/14.
 */
public class ContentMetadata implements Serializable{
    private static final long serialVersionUID = 1681266000001L;

    private String contentId;//indexable
    private String commiterCirrusId;//indexable
    private LocalDateTime availableSince;//indexable
    private LocalDateTime lastUpdated;//indexable
    private Integer piecesAmount;
    private String contentChecksum;
    private ContentStatus status;//indexable
    private Map<Integer, String> piecesChecksums;

    /**
     * Pieces are counted from 0.
     * @return Map of checksums expected from each piece, indexed by piece sequence number
     */
    public Map<Integer, String> getPiecesChecksums() {
        return piecesChecksums;
    }

    public void setPiecesChecksums(Map<Integer, String> piecesChecksums) {
        this.piecesChecksums = piecesChecksums;
    }

    public String getContentChecksum() {
        return contentChecksum;
    }

    public void setContentChecksum(String contentChecksum) {
        this.contentChecksum = contentChecksum;
    }

    public Integer getPiecesAmount() {
        return piecesAmount;
    }

    public void setPiecesAmount(Integer piecesAmount) {
        this.piecesAmount = piecesAmount;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getAvailableSince() {
        return availableSince;
    }

    public void setAvailableSince(LocalDateTime availableSince) {
        this.availableSince = availableSince;
    }

    public String getCommiterCirrusId() {
        return commiterCirrusId;
    }

    public void setCommiterCirrusId(String commiterCirrusId) {
        this.commiterCirrusId = commiterCirrusId;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }


     /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////// CQEngine Attributes \\\\\\\\\\\\\\\
    public static final Attribute<ContentMetadata, String> IDX_CONTENT_ID = new SimpleAttribute<ContentMetadata, String>("CONTENT_ID") {
        public String getValue(ContentMetadata obj) {
            return obj.contentId;
        }
    };
    public static final Attribute<ContentMetadata, String> IDX_CONTENT_CHECKSUM = new SimpleAttribute<ContentMetadata, String>("CONTENT_CHECKSUM") {
        public String getValue(ContentMetadata obj) {
            return obj.contentChecksum;
        }
    };
    public static final Attribute<ContentMetadata, LocalDateTime> IDX_AVAILABLE_SINCE = new SimpleAttribute<ContentMetadata, LocalDateTime>("AVAILABLE_SINCE") {
        public LocalDateTime getValue(ContentMetadata obj) {
            return obj.availableSince;
        }
    };
    public static final Attribute<ContentMetadata, LocalDateTime> IDX_LAST_UPDATED = new SimpleAttribute<ContentMetadata, LocalDateTime>("LAST_UPDATED") {
        public LocalDateTime getValue(ContentMetadata obj) {
            return obj.lastUpdated;
        }
    };
    public static final Attribute<ContentMetadata, String> IDX_COMMITER_CIRRUS_ID = new SimpleAttribute<ContentMetadata, String>("COMMITER_CIRRUS_ID") {
        public String getValue(ContentMetadata obj) {
            return obj.commiterCirrusId;
        }
    };
    public static final Attribute<ContentMetadata, ContentStatus> IDX_CONTENT_STATUS = new SimpleAttribute<ContentMetadata, ContentStatus>("CONTENT_STATUS") {
        public ContentStatus getValue(ContentMetadata obj) {
            return obj.status;
        }
    };
}
