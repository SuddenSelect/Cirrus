package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Model class representing whole File/Content.
 * Propagated through CirrusEvent's.
 * It is intended for every Host to posses info about all
 * available content and retrieve content pieces when necessary.
 * Created by Maciej Majewski on 29/10/14.
 */
public class ContentMetadata implements Serializable, Comparable<ContentMetadata> {
    private static final long serialVersionUID = 1681266000001L;

    private String contentId;//indexable, determined by app upon creation
    private String commiterCirrusId;//indexable, determined by core upon publishing
    private LocalDateTime availableSince;//indexable, determined by core upon publishing
    private LocalDateTime lastUpdated;//indexable, determined by app upon any change
    private Integer piecesAmount;//determined by app upon creation or update
    private String contentChecksum;//determined by app upon creation or update, checked by core
    private ContentStatus status;//indexable, determined by app
    private Map<Integer, String> piecesChecksums;

    /**
     * Pieces are counted from 0.
     *
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
    public static final SimpleAttribute<ContentMetadata, String> IDX_CONTENT_ID = new SimpleAttribute<ContentMetadata, String>() {
        @Override
        public String getValue(ContentMetadata obj, QueryOptions queryOptions) {
            return obj.contentId;
        }
    };
    public static final SimpleAttribute<ContentMetadata, String> IDX_CONTENT_CHECKSUM = new SimpleAttribute<ContentMetadata, String>() {
        @Override
        public String getValue(ContentMetadata obj, QueryOptions queryOptions) {
            return obj.contentChecksum;
        }
    };
    public static final SimpleAttribute<ContentMetadata, LocalDateTime> IDX_AVAILABLE_SINCE = new SimpleAttribute<ContentMetadata, LocalDateTime>() {
        @Override
        public LocalDateTime getValue(ContentMetadata obj, QueryOptions queryOptions) {
            return obj.availableSince;
        }
    };
    public static final SimpleAttribute<ContentMetadata, LocalDateTime> IDX_LAST_UPDATED = new SimpleAttribute<ContentMetadata, LocalDateTime>() {
        @Override
        public LocalDateTime getValue(ContentMetadata obj, QueryOptions queryOptions) {
            return obj.lastUpdated;
        }
    };
    public static final SimpleAttribute<ContentMetadata, String> IDX_COMMITER_CIRRUS_ID = new SimpleAttribute<ContentMetadata, String>() {
        @Override
        public String getValue(ContentMetadata obj, QueryOptions queryOptions) {
            return obj.commiterCirrusId;
        }
    };
    public static final SimpleAttribute<ContentMetadata, ContentStatus> IDX_CONTENT_STATUS = new SimpleAttribute<ContentMetadata, ContentStatus>() {
        @Override
        public ContentStatus getValue(ContentMetadata obj, QueryOptions queryOptions) {
            return obj.status;
        }
    };

    /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////// Comparable \\\\\\\\\\\\\\\\\\\\


    @Override
    public int compareTo(ContentMetadata o) {
        return contentId.compareTo(o.contentId);
    }
}
