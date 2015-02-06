package pl.mmajewski.cirrus.main.appevents;

import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class ActionFailureCirrusAppEvent extends CirrusAppEvent<CirrusBasicApp.AppEventHandler> {

    private String message;
    private Throwable exception;

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

    @Override
    public void appEvent(CirrusBasicApp.AppEventHandler handler) {
        //TODO present problem to the user
    }
}
