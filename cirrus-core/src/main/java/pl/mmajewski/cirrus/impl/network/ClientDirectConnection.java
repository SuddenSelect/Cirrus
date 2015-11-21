package pl.mmajewski.cirrus.impl.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.exception.ConnectionFailCirrusException;
import pl.mmajewski.cirrus.network.exception.SendEventFailCirrusException;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

/**
 * Created by Maciej Majewski on 25/10/15.
 */
public class ClientDirectConnection extends DirectConnection implements ClientEventConnection {
    private ObjectOutputStream objectOutputStream = null;

    /*package*/ ClientDirectConnection(ConnectionPool connectionPool, Host remoteHost) {
        super(connectionPool, remoteHost);
    }

    @Override
    public void connect() throws ConnectionFailCirrusException {
        super.connect();
        try {
            if(objectOutputStream == null) {
                objectOutputStream = new ObjectOutputStream(super.getSocket().getOutputStream());
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            throw new ConnectionFailCirrusException(e, this, super.getParentConnectionPool());
        }
    }

    @Override
    public void kill() {
        super.kill();
        try {
            if(objectOutputStream!=null) {
                objectOutputStream.close();
                objectOutputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();//todo remove
        }
    }

    @Override
    public void sendEvent(CirrusEvent event) throws SendEventFailCirrusException {
        try {
            objectOutputStream.writeObject(event);
            objectOutputStream.reset();
        } catch (NotSerializableException e) {
            System.err.println("NotSerializable: "+event.getClass().getName());
            e.printStackTrace();
        } catch (Exception e) {
            throw new SendEventFailCirrusException(e,this,super.getParentConnectionPool());
        }
    }

}
