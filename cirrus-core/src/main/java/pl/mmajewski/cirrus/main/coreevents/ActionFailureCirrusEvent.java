package pl.mmajewski.cirrus.main.coreevents;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class ActionFailureCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {

    private String message;
    private Throwable exception;
    private ContentPiece invalidContentPiece;

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContentPiece getInvalidContentPiece() {
        return invalidContentPiece;
    }

    public void setInvalidContentPiece(ContentPiece invalidContentPiece) {
        this.invalidContentPiece = invalidContentPiece;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        //TODO present problem to the user
        StringWriter stackTrace = new StringWriter();
        if(exception!=null) {
            exception.printStackTrace(new PrintWriter(stackTrace));
        } else {
            stackTrace.append("< no stack trace >");
        }
        handler.pushFailure("# "+getCreationTime()+" # "+message+" :\n"+stackTrace.toString()+"\n");
    }
}
