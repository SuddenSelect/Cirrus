package pl.mmajewski.cirrus.main.coreevents;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.model.ContentPiece;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class ActionFailureCirrusEvent extends CirrusEvent<CirrusEventHandler> {
    private static final Logger logger = Logger.getLogger(ActionFailureCirrusEvent.class.getName());

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
    public void event(CirrusEventHandler handler) {
        //TODO present problem to the user
        StringWriter stackTrace = new StringWriter();
        if(exception!=null) {
            exception.printStackTrace(new PrintWriter(stackTrace));
        } else {
            stackTrace.append("< no stack trace >");
        }
        String fail = "# "+getCreationTime()+" # "+message+" :\n"+stackTrace.toString()+"\n";
        logger.warning(fail);
        handler.pushFailure(fail);
    }
}
