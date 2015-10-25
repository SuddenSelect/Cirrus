package pl.mmajewski.cirrus.network.client;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.exception.SendEventFailCirrusException;

/**
 * Represents connection for event sharing.
 * Created by Maciej Majewski on 30/10/14.
 */
public interface ClientEventConnection extends Connection {
    public void sendEvent(CirrusEvent event) throws SendEventFailCirrusException;
}
