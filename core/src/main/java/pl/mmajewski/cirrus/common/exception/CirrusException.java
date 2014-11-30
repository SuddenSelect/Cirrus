package pl.mmajewski.cirrus.common.exception;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.Host;

/**
 * Created by Maciej Majewski on 29/10/14.
 */
public class CirrusException extends Exception {
    private Host involvedHost;
    private ContentMetadata involvedContentMetadata;
    private ContentPiece involvedContentPiece;

    protected CirrusException() {
    }

    public CirrusException(Throwable cause, Host involvedHost, ContentMetadata involvedContentMetadata, ContentPiece involvedContentPiece) {
        super(cause);
        this.involvedHost = involvedHost;
        this.involvedContentMetadata = involvedContentMetadata;
        this.involvedContentPiece = involvedContentPiece;
    }

    public CirrusException(Host involvedHost, ContentMetadata involvedContentMetadata, ContentPiece involvedContentPiece) {
        this.involvedHost = involvedHost;
        this.involvedContentMetadata = involvedContentMetadata;
        this.involvedContentPiece = involvedContentPiece;
    }

    public ContentPiece getInvolvedContentPiece() {
        return involvedContentPiece;
    }

    public void setInvolvedContentPiece(ContentPiece involvedContentPiece) {
        this.involvedContentPiece = involvedContentPiece;
    }

    public ContentMetadata getInvolvedContentMetadata() {
        return involvedContentMetadata;
    }

    public void setInvolvedContentMetadata(ContentMetadata involvedContentMetadata) {
        this.involvedContentMetadata = involvedContentMetadata;
    }

    public Host getInvolvedHost() {
        return involvedHost;
    }

    public void setInvolvedHost(Host involvedHost) {
        this.involvedHost = involvedHost;
    }
}
