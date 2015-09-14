package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

import java.util.Set;

/**
 * Class representing placement of ContentPieces in the system.
 * Created by Maciej Majewski on 09/11/14.
 */
public class ContentAvailability {
    private String holderCirrusId;//indexable
    private String contentId;//indexable
    private Set<Integer> piecesSequenceNumbers;

    public String getHolderCirrusId() {
        return holderCirrusId;
    }

    public void setHolderCirrusId(String holderCirrusId) {
        this.holderCirrusId = holderCirrusId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Set<Integer> getPiecesSequenceNumbers() {
        return piecesSequenceNumbers;
    }

    public void setPiecesSequenceNumbers(Set<Integer> piecesSequenceNumbers) {
        this.piecesSequenceNumbers = piecesSequenceNumbers;
    }


    /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////// CQEngine Attributes \\\\\\\\\\\\\\\
    public static final Attribute<ContentAvailability, String> IDX_HOLDER_CIRRUS_ID = new SimpleAttribute<ContentAvailability, String>() {
        @Override
        public String getValue(ContentAvailability obj, QueryOptions queryOptions) {
            return obj.holderCirrusId;
        }
    };
    public static final Attribute<ContentAvailability, String> IDX_CONTENT_ID = new SimpleAttribute<ContentAvailability, String>() {
        @Override
        public String getValue(ContentAvailability obj, QueryOptions queryOptions) {
            return obj.contentId;
        }
    };
}
