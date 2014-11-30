package pl.mmajewski.cirrus.event;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerMismatchedCirrusException;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public interface CirrusAppEventHandler extends CirrusEventHandler {

    @Override
    default public void appEvent(CirrusEvent event){
        //no more sneaky comebacks ]:->
        try {
            CirrusAppEvent appEvent = (CirrusAppEvent) event;
            appEvent.appEvent(this);
        }catch (ClassCastException e){
            throw new EventHandlerMismatchedCirrusException(e,event,this);
        }
    }
}
