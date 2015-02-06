package pl.mmajewski.cirrus.event;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.UnimplementedEventCirrusException;

/**
 * AppEvents are expected to extend this class and implement their behavior
 * by overriding 'appEvent' method.
 * Created by Maciej Majewski on 09/11/14.
 */
public abstract class CirrusAppEvent <CEH extends CirrusAppEventHandler> extends CirrusEvent<CEH> {
    private static final long serialVersionUID = 1681266000008L;

    @Override
    public final void event(CirrusAppEventHandler handler){
        handler.appEvent(this);
    }

    public void appEvent(CEH handler) {
        throw new UnimplementedEventCirrusException(this);
    }
}
