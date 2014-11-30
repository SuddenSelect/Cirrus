package pl.mmajewski.cirrus.exception;

import pl.mmajewski.cirrus.common.exception.CirrusException;
import pl.mmajewski.cirrus.content.ContentAdapter;

/**
 * Created by Maciej Majewski on 30/11/14.
 */
public class ContentAdapterCirrusException extends CirrusException {
    private ContentAdapter failed;

    public ContentAdapterCirrusException(Throwable cause, ContentAdapter failed) {
        super(cause, null, null, null);
        this.failed = failed;
    }

    public ContentAdapter getFailedContentAdapter(){
        return failed;
    }
}
