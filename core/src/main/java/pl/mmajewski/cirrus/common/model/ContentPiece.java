package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Single content piece model.
 *
 * Created by Maciej Majewski on 29/10/14.
 */
public class ContentPiece implements Serializable {
    private static final long serialVersionUID = 1681266000002L;

    private String contentId;//indexable, ContentMetadata ID
    private Integer sequence;//indexable
    private String expectedChecksum;
    private ByteBuffer content;
    private transient ContentStatus status; //indexable, determined locally by core

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
        return content;
    }

    public ContentStatus getStatus() {
        return status==null ? ContentStatus.UNCHECKED : status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public void setContent(ByteBuffer content) {
        this.content = content;
    }

     /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////// CQEngine Attributes \\\\\\\\\\\\\\\
     public static final Attribute<ContentPiece, String> IDX_CONTENT_ID = new SimpleAttribute<ContentPiece, String>("CONTENT_ID") {
         public String getValue(ContentPiece obj) {
             return obj.contentId;
         }
     };
    public static final Attribute<ContentPiece, Integer> IDX_SEQUENCE = new SimpleAttribute<ContentPiece, Integer>("SEQUENCE") {
        public Integer getValue(ContentPiece obj) {
            return obj.sequence;
        }
    };
    public static final Attribute<ContentPiece, ContentStatus> IDX_CONTENT_STATUS = new SimpleAttribute<ContentPiece, ContentStatus>("CONTENT_STATUS") {
        public ContentStatus getValue(ContentPiece obj) {
            return obj.status;
        }
    };
}
