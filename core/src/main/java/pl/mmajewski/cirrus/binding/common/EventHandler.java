package pl.mmajewski.cirrus.binding.common;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;

/**
 * Singleton wrapper. Only one EventHandler working in it's own thread is expected to exist.
 * The same goes for AppEventHandler.
 * Created by Maciej Majewski on 30/11/14.
 */
public class EventHandler {
    private static CirrusEventHandler handler = null;
    public static CirrusEventHandler getInstance() {
        if(handler==null){
            handler = null;//binding stub
        }
        return handler;
    }
}
