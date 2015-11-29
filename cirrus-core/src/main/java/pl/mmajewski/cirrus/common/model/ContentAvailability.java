package pl.mmajewski.cirrus.common.model;

import com.googlecode.cqengine.attribute.MultiValueAttribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

import java.io.Serializable;
import java.util.Set;

/**
 * Class representing placement of ContentPieces in the system.
 * Created by Maciej Majewski on 09/11/14.
 */
public class ContentAvailability implements Serializable, Comparable<ContentAvailability> {

    private String holderCirrusId;//indexable
    private String contentId;//indexable
    private Set<Integer> piecesSequenceNumbers;//indexable

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

    @Override
    public int compareTo(ContentAvailability o) {
        return contentId.compareTo(o.contentId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ContentAvailability{");
        sb.append(holderCirrusId);
        sb.append(" -> ");
        sb.append(contentId);
        sb.append(" ( ");
        for(Integer i : piecesSequenceNumbers){
            sb.append(i);
            sb.append(" ");
        }
        sb.append(") }");
        return sb.toString();

    }

    /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////// CQEngine Attributes \\\\\\\\\\\\\\\
    public static final SimpleAttribute<ContentAvailability, String> IDX_HOLDER_CIRRUS_ID = new SimpleAttribute<ContentAvailability, String>("IDX_HOLDER_CIRRUS_ID") {
        @Override
        public String getValue(ContentAvailability obj, QueryOptions queryOptions) {
            return obj.holderCirrusId;
        }
    };
    public static final SimpleAttribute<ContentAvailability, String> IDX_CONTENT_ID = new SimpleAttribute<ContentAvailability, String>("IDX_CONTENT_ID") {
        @Override
        public String getValue(ContentAvailability obj, QueryOptions queryOptions) {
            return obj.contentId;
        }
    };
    public static final SimpleAttribute<ContentAvailability, String> IDX_UNIQUE_ID = new SimpleAttribute<ContentAvailability, String>("IDX_UNIQUE_ID") {
        @Override
        public String getValue(ContentAvailability obj, QueryOptions queryOptions) {
            return obj.contentId+"-"+obj.holderCirrusId;
        }
    };
    public static final MultiValueAttribute<ContentAvailability, Integer> IDX_AVAILABLE_PIECES = new MultiValueAttribute<ContentAvailability, Integer>("IDX_AVAILABLE_PIECES") {
        @Override
        public Set<Integer> getValues(ContentAvailability obj, QueryOptions queryOptions) {
            return obj.piecesSequenceNumbers;
        }
    };
}
