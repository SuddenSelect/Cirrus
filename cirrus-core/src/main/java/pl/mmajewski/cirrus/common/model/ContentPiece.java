package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Single content piece model.
 *
 * Created by Maciej Majewski on 29/10/14.
 */
public class ContentPiece implements Serializable, Comparable<ContentPiece> {
    private static final long serialVersionUID = 1681266000002L;

    private String contentId;//indexable, ContentMetadata ID
    private Integer sequence;//indexable
    private String expectedChecksum;
    private byte[] content;
    private /*transient*/ ContentStatus status; //indexable, determined locally by core
    // IMPORTANT: transient fields becomes NULL when inserted into CQEngine collection
    // Therefore, field needs to be reset by server upon receiving

    public void simulateFieldTransiency(){
        status = ContentStatus.UNCHECKED;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getExpectedChecksum() {
        return expectedChecksum;
    }

    public void setExpectedChecksum(String expectedChecksum) {
        this.expectedChecksum = expectedChecksum;
    }

    public ByteBuffer getContent() {
        return ByteBuffer.wrap(content);
    }

    public ContentStatus getStatus() {
        return status==null ? ContentStatus.UNCHECKED : status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public void setContent(ByteBuffer content) {
        this.content = content.hasArray() ? content.array() : null;
    }

    @Override
    public int compareTo(ContentPiece o) {
        return sequence.compareTo(o.sequence);
    }

    /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////// CQEngine Attributes \\\\\\\\\\\\\\\
     public static final SimpleAttribute<ContentPiece, String> IDX_CONTENT_ID = new SimpleAttribute<ContentPiece, String>("IDX_CONTENT_ID") {
        @Override
        public String getValue(ContentPiece obj, QueryOptions queryOptions) {
             return obj.contentId;
         }
     };
    public static final SimpleAttribute<ContentPiece, Integer> IDX_SEQUENCE = new SimpleAttribute<ContentPiece, Integer>("IDX_SEQUENCE") {
        @Override
        public Integer getValue(ContentPiece obj, QueryOptions queryOptions) {
            return obj.sequence;
        }
    };
    public static final SimpleAttribute<ContentPiece, ContentStatus> IDX_CONTENT_STATUS = new SimpleAttribute<ContentPiece, ContentStatus>("IDX_CONTENT_STATUS") {
        @Override
        public ContentStatus getValue(ContentPiece obj, QueryOptions queryOptions) {
            return obj.status;
        }
    };
    public static final SimpleAttribute<ContentPiece, String> IDX_UNIQUE_ID = new SimpleAttribute<ContentPiece, String>("IDX_UNIQUE_ID") {
        @Override
        public String getValue(ContentPiece obj, QueryOptions queryOptions) {
            return obj.getContentId()+"-"+obj.getSequence();
        }
    };
}
